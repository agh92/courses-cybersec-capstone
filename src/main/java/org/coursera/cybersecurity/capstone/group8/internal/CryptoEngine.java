package org.coursera.cybersecurity.capstone.group8.internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Scanner;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 * This is where we consolidate all crypto stuff
 */
public class CryptoEngine {
	private Logger log = LoggerFactory.getLogger(CryptoEngine.class);

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
	
    private SecretKeySpec secretKeySpec;
	
	private SecureRandom secureRandom;
	
	private int saltNumberOfBytes = 8;
	
	@Value("${use.one.time.key:false}")
	private boolean useOneTimeKey = true;
	
	@PostConstruct
	private void setup() throws Exception {
		keySizeBytes = keySizeBits >> 3;
		secureRandom = new SecureRandom();
		
		// If there's no such algo, we want it to blow up on startup
		digest = MessageDigest.getInstance(hashAlgo);
		
		if (useOneTimeKey && secretKeySpec == null) {
			log.warn("Using one-time secret key, suitable only for one-time in-memory database");
			secretKeySpec = createRandomSecretKey();
		} else {
			// TODO dynamically load file name/path or read from some config file - to be determined
			Scanner scan = new Scanner(new File("key.txt"));
			secretKeySpec = new SecretKeySpec(stringToBytes(scan.next()), keyGenAlgo);
		}
		if (secretKeySpec == null) 
			throw new Exception("Cannot obtain encryption key");
	}
	
	public void generateAndWriteKey(File f) throws Exception {
        SecretKeySpec secretKeySpec = createRandomSecretKey();
        
        Base64.Encoder encoder = Base64.getEncoder();
        Writer out = new FileWriter(f);
        out.write("-----BEGIN " + keyGenAlgo + " KEY-----\n");
        out.write(encoder.encodeToString(secretKeySpec.getEncoded()));
        out.write("\n-----END " + keyGenAlgo + " KEY-----\n");
        out.close();
        log.info("New random key written to " + f.getAbsolutePath());
    }

	private SecretKeySpec createRandomSecretKey() {
		byte[] key = new byte[keySizeBytes];
        secureRandom.nextBytes(key);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, keyGenAlgo);
		return secretKeySpec;
	}
	
	public void setKeyFromFile(File f) throws IOException {
		this.secretKeySpec = readKeyFromFile(f);
	}
	
	public SecretKeySpec readKeyFromFile(File f) throws IOException {
		log.info("Reading key from " + f.getAbsolutePath());
		SecretKeySpec secretKeySpec = null;
        Base64.Decoder decoder = Base64.getDecoder();
        StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new FileReader(f));
		String line;
		while ((line = br.readLine()) != null) {
			if (!line.startsWith("-") && secretKeySpec == null) {
				byte[] bytes = decoder.decode(line);
				secretKeySpec = new SecretKeySpec(bytes, keyGenAlgo);
			}
			sb.append(line).append("\n");
		}
		br.close();
		log.info("File content:\n" + sb.toString());
		if (secretKeySpec == null)
			throw new IOException("No key found in " + f);
		return secretKeySpec;
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

	public SecretKeySpec getSecretKeySpec() {
		return secretKeySpec;
	}
	
	
}
