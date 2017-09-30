package org.coursera.cybersecurity.capstone.group8.internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Scanner;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.coursera.cybersecurity.capstone.group8.internal.data.DecryptedMessage;
import org.coursera.cybersecurity.capstone.group8.internal.data.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 * This class contains all the necessary functions to encript user messages before saving them in the database
 * and decrypt them.
 */
public class CryptoEngine {
	private Logger log = LoggerFactory.getLogger(CryptoEngine.class);
	
	@Value("${cipher.algo:AES/CBC/PKCS5Padding}")
	private String dataCipherAlgo;
    
	@Value("${key.algo:AES}")
    private String keyGenAlgo;
    
	@Value("${key.size.bits:128}")
    private int keySizeBits;
	private int keySizeBytes;
	
    private SecretKeySpec secretKeySpec;
	
	private SecureRandom secureRandom;
	
	@Value("${use.one.time.key:false}")
	private boolean useOneTimeKey = true;
	
	// This syntax means "look up encryption.key.path property in application.properties, or use default key.txt
	@Value("${encryption.key.path:key.txt}")
	private String encryptionKeyPath;
	
	@PostConstruct
	private void setup() throws Exception {
		keySizeBytes = keySizeBits >> 3;
		secureRandom = new SecureRandom();
		
		if (useOneTimeKey && secretKeySpec == null) {
			log.warn("Using one-time secret key, suitable only for one-time in-memory database");
			secretKeySpec = createRandomSecretKey();
		} else {
			Scanner scan = new Scanner(new File(encryptionKeyPath));
			secretKeySpec = new SecretKeySpec(stringToBytes(scan.next()), keyGenAlgo);
			scan.close();
		}
		if (secretKeySpec == null) 
			throw new Exception("Cannot obtain encryption key");
	}

	private SecretKeySpec createRandomSecretKey() {
		byte[] key = new byte[keySizeBytes];
        secureRandom.nextBytes(key);
		return new SecretKeySpec(key, keyGenAlgo);
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
	
	public byte[] joinBytes(byte[] bb1, byte[] bb2) {
		final byte[] joined = new byte[bb1.length + bb2.length];
		System.arraycopy(bb1, 0, joined, 0, bb1.length);
		System.arraycopy(bb2, 0, joined, bb1.length, bb2.length);
		return joined;
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
        return new IvParameterSpec(ivBytes);
	}
	
	public synchronized byte[] encryptString(String s) throws Exception {
        byte[] byteText = stringToBytes(s);
		Cipher cipher = Cipher.getInstance(dataCipherAlgo);
		IvParameterSpec iv = getRandomIv(); // Same as salt
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);
        byte[] byteCipherText = cipher.doFinal(byteText);
        return joinBytes(iv.getIV(), byteCipherText);
	}
	
	public synchronized String decryptString(byte[] cipherTextBytes) throws Exception {
		byte[] ivBytes = new byte[keySizeBytes];
		System.arraycopy(cipherTextBytes, 0, ivBytes, 0, ivBytes.length);
        IvParameterSpec iv = new IvParameterSpec(ivBytes);
        byte[] cipherTextBytesNoIV = new byte[cipherTextBytes.length - ivBytes.length];
        System.arraycopy(cipherTextBytes, ivBytes.length, cipherTextBytesNoIV, 0, cipherTextBytesNoIV.length);
		Cipher cipher = Cipher.getInstance(dataCipherAlgo);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);
        byte[] bytePlainText = cipher.doFinal(cipherTextBytesNoIV);
        return bytesToString(bytePlainText);
	}
	
	public Message encryptMessage(DecryptedMessage decryptedMsg) throws Exception {
		byte[] cipherText = encryptString(decryptedMsg.getPlainTextMessage());
		return new Message(decryptedMsg, cipherText);
	}
	
	public DecryptedMessage decryptMessage(Message encryptedMsg) throws Exception {
		String plainText = decryptString(encryptedMsg.getEncryptedMessage());
		return new DecryptedMessage(encryptedMsg, plainText);
	}
}
