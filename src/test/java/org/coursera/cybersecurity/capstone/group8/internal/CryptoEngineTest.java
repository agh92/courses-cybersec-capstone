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
		String hashAsHexString = cryptoEngine.createHash("test");
		assertEquals("9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08", hashAsHexString);
		assertEquals(64, hashAsHexString.length());
	}

	@Test
	public void testSaltedPasswordWorks() {
		String saltedPassword = cryptoEngine.createSaltedPasswordHash("user", "password");
		assertEquals(128, saltedPassword.length());
		assertEquals("04f8996da763b7a969b1028ee3007569eaf3a635486ddab211d512c85b9df8fbab3327d85463f0feb5574b6fb6fd5ee2f388fbea4f0c0c28707d65c8d7ec78ea", saltedPassword);
	}

	@Test
	public void testDifferentUsersHaveDifferentSaltedPasswords() {
		String saltedPassword1 = cryptoEngine.createSaltedPasswordHash("user1", "password");
		String saltedPassword2 = cryptoEngine.createSaltedPasswordHash("user2", "password");
		assertNotEquals(saltedPassword1, saltedPassword2);
	}
}
