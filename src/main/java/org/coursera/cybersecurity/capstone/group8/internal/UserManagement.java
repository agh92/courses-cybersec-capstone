package org.coursera.cybersecurity.capstone.group8.internal;

import org.coursera.cybersecurity.capstone.group8.internal.data.Message;
import org.coursera.cybersecurity.capstone.group8.internal.data.MessageRepository;
import org.coursera.cybersecurity.capstone.group8.internal.data.User;
import org.coursera.cybersecurity.capstone.group8.internal.data.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class UserManagement {
	private Logger log = LoggerFactory.getLogger(UserManagement.class);

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
		String saltedPasswordHash = cryptoEngine.createSaltedPasswordHash(saltBytes, password);
		log.info("Salted password hash is " + saltedPasswordHash);
		User user = new User(userId, saltedPasswordHash, realName, saltBytes);
		userRepository.save(user);
		log.info("User created: " + user);
		return user;
	}

	public List<Message> getMessagesForUserID(String id){
	    return msgRepository.findByToUserIdOrderByTimestampAsc(id);
    }
}
