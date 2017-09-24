package org.coursera.cybersecurity.capstone.group8.internal;

import static org.junit.Assert.*;

import org.coursera.cybersecurity.capstone.group8.TestConfig;
import org.coursera.cybersecurity.capstone.group8.internal.data.DecryptedMessage;
import org.coursera.cybersecurity.capstone.group8.internal.data.Message;
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
	
	@Test
	public void testEncryptDecryptMessage() throws Exception {
		DecryptedMessage decryptedSrc = new DecryptedMessage();
		decryptedSrc.setId(1);
		decryptedSrc.setFromUserId("fromUser");
		decryptedSrc.setToUserId("toUser");
		decryptedSrc.setTimestamp(12345);
		decryptedSrc.setPlainTextMessage("plain text");
		Message encrypted = cryptoEngine.encryptMessage(decryptedSrc);
		assertEquals(32, encrypted.getEncryptedMessage().length);
		DecryptedMessage decrypted = cryptoEngine.decryptMessage(encrypted);
		assertEquals(decryptedSrc.getId(), decrypted.getId());
		assertEquals(decryptedSrc.getFromUserId(), decrypted.getFromUserId());
		assertEquals(decryptedSrc.getToUserId(), decrypted.getToUserId());
		assertEquals(decryptedSrc.getTimestamp(), decrypted.getTimestamp());
		assertEquals(decryptedSrc.getPlainTextMessage(), decrypted.getPlainTextMessage());
	}
}
