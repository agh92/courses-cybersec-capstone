package org.coursera.cybersecurity.capstone.group8.internal;

public class UserManagement {

	public void createUser(String userId, String password, String realName) throws Exception {
		if (userExists(userId))
			throw new Exception("User exists");
		String saltedPasswordHash = createSaltedPasswordHash(userId, password);
		saveNewUser(userId, saltedPasswordHash, realName);
		
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
		
	}

	private String createSaltedPasswordHash(String userId, String password) {
		String salt = createHash(userId);
		String saltedPasswordHash = createHash(salt + password);
		return saltedPasswordHash;
	}

	private void saveNewUser(String userId, String saltedPasswordHash, String realName) {
		// TODO Auto-generated method stub
		
	}

	private String createHash(String userId) {
		// TODO SHA-256
		return null;
	}

	private boolean userExists(String userId) {
		// TODO Auto-generated method stub
		return false;
	}

}
