package org.coursera.cybersecurity.capstone.group8.internal;

import static org.junit.Assert.*;

import org.coursera.cybersecurity.capstone.group8.TestConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Because we want to test the crypto properly
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class CryptoEngineTest {
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
}
