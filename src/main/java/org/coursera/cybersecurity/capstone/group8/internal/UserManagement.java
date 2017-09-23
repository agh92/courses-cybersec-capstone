package org.coursera.cybersecurity.capstone.group8.internal;

import org.coursera.cybersecurity.capstone.group8.internal.data.User;
import org.coursera.cybersecurity.capstone.group8.internal.data.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class UserManagement {
	private Logger log = LoggerFactory.getLogger(UserManagement.class);
	
	@Autowired
	private UserRepository userRepository;

	public User createUser(String userId, String password, String realName) throws Exception {
		if (userRepository.findOne(userId) != null)
			throw new Exception("User exists");
		String saltedPasswordHash = createSaltedPasswordHash(userId, password);
		User user = new User(userId, saltedPasswordHash, realName);
		userRepository.save(user);
		log.info("User created: " + user);
		return user;
		
	}

	private String createSaltedPasswordHash(String userId, String password) {
		String salt = createHash(userId);
		String saltedPasswordHash = createHash(salt + password);
		return saltedPasswordHash;
	}

	private String createHash(String userId) {
		// TODO SHA-256
		return null;
	}
}
