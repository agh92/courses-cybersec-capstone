package org.coursera.cybersecurity.capstone.group8.internal;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.springframework.beans.factory.annotation.Value;

/**
 * This is where we consolidate all crypto stuff
 */
public class CryptoEngine {
	@Value("${hash.algo:SHA-256}")
	private String hashAlgo;
	
	private MessageDigest digest;
	
	@Value("${cipher.algo:AES/CBC/PKCS5Padding}")
	private String dataCipherAlgo;
    
	@Value("${key.algo:AES}")
    private String keyGenAlgo;
    
	@Value("${key.size.bits:128}")
    private int keySizeBits;
    private int keySizeBytes;

    // TODO this has to be read from the file, for now randomly generated on every invocation
    private SecretKey secKey;
	
	private SecureRandom secureRandom;
	
	private int saltNumberOfBytes = 8;

	
	@PostConstruct
	private void setup() throws NoSuchAlgorithmException, NoSuchPaddingException {
		secureRandom = new SecureRandom();
		
		// If there's no such algo, we want it to blow up on startup
		digest = MessageDigest.getInstance(hashAlgo);
		
	    keySizeBytes = keySizeBits >> 3;
        KeyGenerator keyGen = KeyGenerator.getInstance(keyGenAlgo);
        keyGen.init(keySizeBits, secureRandom);
        secKey = keyGen.generateKey();
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

	/*
	 * To ensure we always use the same encoding.
	 */
	public static String bytesToString(byte[] bytes) {
		return new String(bytes, StandardCharsets.UTF_8);
	}

	public String createSaltedPasswordHash(final byte[] salt, final String password) {
		final byte[] passwordAsBytes = stringToBytes(password);
		return createHash(joinBytes(salt, passwordAsBytes));
	}
	
	public byte[] joinBytes(byte[] bb1, byte[] bb2) {
		final byte[] joined = new byte[bb1.length + bb2.length];
		System.arraycopy(bb1, 0, joined, 0, bb1.length);
		System.arraycopy(bb2, 0, joined, bb1.length, bb2.length);
		return joined;
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
	
	public IvParameterSpec getRandomIv() {
        byte[] ivBytes = new byte[keySizeBytes];
        secureRandom.nextBytes(ivBytes);
        IvParameterSpec iv = new IvParameterSpec(ivBytes);
        return iv;
	}
	
	public byte[] encryptString(String s) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException {
        byte[] byteText = stringToBytes(s);
		Cipher cipher = Cipher.getInstance(dataCipherAlgo);
		IvParameterSpec iv = getRandomIv(); // Same as salt
        cipher.init(Cipher.ENCRYPT_MODE, secKey, iv);
        byte[] byteCipherText = cipher.doFinal(byteText);
        return joinBytes(iv.getIV(), byteCipherText);
	}
	
	public String decryptString(byte[] cipherTextBytes) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException {
		byte[] ivBytes = new byte[keySizeBytes];
		System.arraycopy(cipherTextBytes, 0, ivBytes, 0, ivBytes.length);
        IvParameterSpec iv = new IvParameterSpec(ivBytes);
        byte[] cipherTextBytesNoIV = new byte[cipherTextBytes.length - ivBytes.length];
        System.arraycopy(cipherTextBytes, ivBytes.length, cipherTextBytesNoIV, 0, cipherTextBytesNoIV.length);
		Cipher cipher = Cipher.getInstance(dataCipherAlgo);
        cipher.init(Cipher.DECRYPT_MODE, secKey, iv);
        byte[] bytePlainText = cipher.doFinal(cipherTextBytesNoIV);
        return bytesToString(bytePlainText);
	}
}
