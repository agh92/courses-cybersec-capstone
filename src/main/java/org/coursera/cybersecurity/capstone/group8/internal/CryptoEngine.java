package org.coursera.cybersecurity.capstone.group8.internal;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;

/**
 * This is where we consolidate all crypto stuff
 */
public class CryptoEngine {
	@Value("${hash.algo:SHA-256}")
	private String hashAlgo;
	
	private MessageDigest digest;
	
	private final SecureRandom secureRandom = new SecureRandom();
	
	private int saltNumberOfBytes = 8;

	
	@PostConstruct
	private void setup() throws NoSuchAlgorithmException {
		// If there's no such algo, we want it to blow up on startup
		digest = MessageDigest.getInstance(hashAlgo);
	}
	
	/*
	 * Salt has to be random, not derived from any predictable value like user ID.
	 */
	public byte[] getRandomSalt() {
		byte[] saltBytes = new byte[saltNumberOfBytes];
		secureRandom.nextBytes(saltBytes);
		return saltBytes;
	}
	
	/*
	 * To ensure we always use the same encoding.
	 */
	public static byte[] stringToBytes(String s) {
		return s.getBytes(StandardCharsets.UTF_8);
	}

	public String createSaltedPasswordHash(final byte[] salt, final String password) {
		final byte[] passwordAsBytes = stringToBytes(password);
		final byte[] saltAndPassword = new byte[salt.length + passwordAsBytes.length];
		System.arraycopy(salt, 0, saltAndPassword, 0, salt.length);
		System.arraycopy(passwordAsBytes, 0, saltAndPassword, salt.length, passwordAsBytes.length);
		
		return createHash(saltAndPassword);
	}

	public synchronized String createHash(byte[] bytes) {
		digest.reset();
		byte[] hash = digest.digest(bytes);
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
