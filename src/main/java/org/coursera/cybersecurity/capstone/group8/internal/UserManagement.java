package org.coursera.cybersecurity.capstone.group8.internal;

import org.coursera.cybersecurity.capstone.group8.internal.data.DecryptedMessage;
import org.coursera.cybersecurity.capstone.group8.internal.data.Message;
import org.coursera.cybersecurity.capstone.group8.internal.data.MessageRepository;
import org.coursera.cybersecurity.capstone.group8.internal.data.User;
import org.coursera.cybersecurity.capstone.group8.internal.data.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

public class UserManagement implements UserDetailsService {
	@SuppressWarnings("serial")
	public static final GrantedAuthority AUTHORITY_USER = new GrantedAuthority() {
		@Override
		public String getAuthority() {
			return "USER";
		}
	};

	private Logger log = LoggerFactory.getLogger(UserManagement.class);
	
	@Lazy
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CryptoEngine cryptoEngine;

	@Autowired
    private MessageRepository msgRepository;

	public User createUser(String userId, String password, 
			String secretQuestion, String secretAnswer) throws Exception {
		if (userRepository.findOne(userId) != null)
			throw new Exception("User exists");
		String saltedPasswordHash = passwordEncoder.encode(password);
		String secretHashedAnswer = passwordEncoder.encode(secretAnswer);
		User user = new User(userId, saltedPasswordHash, secretQuestion, secretHashedAnswer);
		userRepository.save(user);
		log.info("User created: " + user);
		return user;
	}

	public List<DecryptedMessage> getMessagesForUser(User user) throws Exception {
		List<Message> encryptedMessages = msgRepository.findByToUserIdOrderByTimestampAsc(user.getId());
		List<DecryptedMessage> decryptedMessages = new ArrayList<>(encryptedMessages.size());
		for (Message m : encryptedMessages) {
			DecryptedMessage dm = cryptoEngine.decryptMessage(m);
			decryptedMessages.add(dm);
		}
		return decryptedMessages;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.info("Loading user details for \"" + username + "\"");
		UserDetails userDetails = userRepository.findOne(username);
		log.info("User details: " + userDetails);
		if (userDetails == null)
			throw new UsernameNotFoundException("Username not found");
		return userDetails;
	}

	public boolean userExists(String userId) {
		return userRepository.findOne(userId) != null;
	}

	public void persist(User user) {
		userRepository.save(user);
	}

	public void saveMessage(DecryptedMessage decryptedMessage) throws Exception {
		Message encryptedMessage = cryptoEngine.encryptMessage(decryptedMessage);
		msgRepository.save(encryptedMessage);
	}

	public void checkSecretAnswerMatches(User user, String secretAnswer) throws UserInputException {
		if (!passwordEncoder.matches(secretAnswer, user.getSecretHashedAnswer())) 
			throw new UserInputException("Secret answer doesn't match");
	}

	public void setNewPassword(User user, String password) {
		user.setSaltedPasswordHash(passwordEncoder.encode(password));
	}
}
