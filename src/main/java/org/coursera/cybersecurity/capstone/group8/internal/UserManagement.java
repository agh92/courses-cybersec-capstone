package org.coursera.cybersecurity.capstone.group8.internal;

import org.coursera.cybersecurity.capstone.group8.internal.data.Message;
import org.coursera.cybersecurity.capstone.group8.internal.data.MessageRepository;
import org.coursera.cybersecurity.capstone.group8.internal.data.User;
import org.coursera.cybersecurity.capstone.group8.internal.data.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

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
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CryptoEngine cryptoEngine;

	@Autowired
    private MessageRepository msgRepository;

	public User createUser(String userId, String password, String realName) throws Exception {
		if (userRepository.findOne(userId) != null)
			throw new Exception("User exists");
		byte[] saltBytes = cryptoEngine.getRandomSalt();
		String saltedPasswordHash = passwordEncoder.encode(password);
		log.info("Salted password hash is " + saltedPasswordHash);
		User user = new User(userId, saltedPasswordHash, realName, saltBytes);
		userRepository.save(user);
		log.info("User created: " + user);
		return user;
	}

	public List<Message> getMessagesForUser(User user) {
		List<Message> encryptedMessages = msgRepository.findByToUserIdOrderByTimestampAsc(user.getId());
		return encryptedMessages;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.info("Loading user details for \"" + username + "\"");
		UserDetails userDetails = userRepository.findOne(username);
		log.info("User details: " + userDetails);
		if (userDetails == null)
			throw new UsernameNotFoundException(username);
		return userDetails;
	}

	public boolean userExists(String userId) {
		return userRepository.findOne(userId) != null;
	}
}
