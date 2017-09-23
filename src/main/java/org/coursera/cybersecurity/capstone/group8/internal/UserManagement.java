package org.coursera.cybersecurity.capstone.group8.internal;

import org.coursera.cybersecurity.capstone.group8.internal.data.User;
import org.coursera.cybersecurity.capstone.group8.internal.data.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class UserManagement {
	@Autowired
	private UserRepository userRepository;

	public void createUser(String userId, String password, String realName) throws Exception {
		if (userRepository.findOne(userId) != null)
			throw new Exception("User exists");
		String saltedPasswordHash = createSaltedPasswordHash(userId, password);
		User user = new User(userId, saltedPasswordHash, realName);
		userRepository.save(user);
		
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
		
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
