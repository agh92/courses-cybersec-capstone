package org.coursera.cybersecurity.capstone.group8.internal;

import static org.junit.Assert.*;

import java.io.File;

import org.coursera.cybersecurity.capstone.group8.TestConfig;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Because we want to test the crypto properly
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
@TestPropertySource(locations="classpath:test.properties")
public class CryptoEngineTest {
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
	
	@Autowired
	private CryptoEngine cryptoEngine;
	
	@Test
	public void testHashingWorks() {
		String hashAsHexString = cryptoEngine.createHash(CryptoEngine.stringToBytes("test"));
		assertEquals("9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08", hashAsHexString);
		assertEquals(64, hashAsHexString.length());
	}

	@Test
	public void testSaltedPasswordWorks() {
		String saltedPassword = cryptoEngine.createSaltedPasswordHash(CryptoEngine.stringToBytes("salt"), "password");
		assertEquals(64, saltedPassword.length());
		assertEquals("13601bda4ea78e55a07b98866d2be6be0744e3866f13c00c811cab608a28f322", saltedPassword);
	}

	@Test
	public void testDifferentUsersHaveDifferentSaltedPasswords() {
		String saltedPassword1 = cryptoEngine.createSaltedPasswordHash(CryptoEngine.stringToBytes("salt1"), "password");
		String saltedPassword2 = cryptoEngine.createSaltedPasswordHash(CryptoEngine.stringToBytes("salt2"), "password");
		assertNotEquals(saltedPassword1, saltedPassword2);
	}
	
	@Test
	public void testEncryptDecrypt() throws Exception {
		String s = "test";
		byte[] cipherTextBytes = cryptoEngine.encryptString(s);
		String cipherTextAsString = CryptoEngine.bytesToHex(cipherTextBytes);
		System.out.println(cipherTextAsString);
		System.out.println(cipherTextBytes.length);
		assertEquals(32, cipherTextBytes.length); // Two blocks, IV + ciphertext
		String decrypted = cryptoEngine.decryptString(cipherTextBytes);
		assertEquals(s, decrypted);
	}
	
	@Test
	public void testEncryptEveryTimeDifferent() throws Exception {
		String s = "test";
		byte[] cipherTextBytes1 = cryptoEngine.encryptString(s);
		String cipherTextAsString1 = CryptoEngine.bytesToHex(cipherTextBytes1);
		byte[] cipherTextBytes2 = cryptoEngine.encryptString(s);
		String cipherTextAsString2 = CryptoEngine.bytesToHex(cipherTextBytes2);

		System.out.println(cipherTextAsString1);
		System.out.println(cipherTextAsString2);
		
		// Because for every single encription IV is random and different
		assertNotEquals(cipherTextAsString1, cipherTextAsString2);
	}
}
