package org.coursera.cybersecurity.capstone.group8.internal;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;

/**
 * This is where we consolidate all crypto stuff
 */
public class CryptoEngine {
	@Value("${hash.algo:SHA-256}")
	private String hashAlgo;
	
	private MessageDigest digest;
	
	@PostConstruct
	private void setup() throws NoSuchAlgorithmException {
		// If there's no such algo, we want it to blow up on startup
		digest = MessageDigest.getInstance(hashAlgo);
	}

	public String createSaltedPasswordHash(String userId, String password) {
		String salt = createHash(userId);
		String saltedPasswordHash = createHash(salt + password);
		return salt + saltedPasswordHash;
	}

	public synchronized String createHash(String s) {
		digest.reset();
		byte[] hash = digest.digest(s.getBytes(StandardCharsets.UTF_8));
		return bytesToHex(hash);
	}

	public static String bytesToHex(byte[] hash) {
	    StringBuffer hexString = new StringBuffer();
	    for (int i = 0; i < hash.length; i++) {
	    String hex = Integer.toHexString(0xff & hash[i]);
	    if(hex.length() == 1) hexString.append('0');
	        hexString.append(hex);
	    }
	    return hexString.toString();
	}
}
