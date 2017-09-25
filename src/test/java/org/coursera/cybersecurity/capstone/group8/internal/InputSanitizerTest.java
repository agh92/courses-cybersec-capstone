package org.coursera.cybersecurity.capstone.group8.internal;

import static org.junit.Assert.*;

import org.coursera.cybersecurity.capstone.group8.TestConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
@TestPropertySource(locations="classpath:test.properties")
public class InputSanitizerTest {
	@Autowired
	private InputSanitizer inputSanitizer;

	// Username
	@Test(expected=UserInputException.class)
	public void testUsernameTooShort() throws UserInputException {
		inputSanitizer.checkUsername("123");
	}

	@Test(expected=UserInputException.class)
	public void testUsernameTooLong() throws UserInputException {
		inputSanitizer.checkUsername("123456789012345678901");
	}

	@Test(expected=UserInputException.class)
	public void testUsernameInvalidCharacters() throws UserInputException {
		inputSanitizer.checkUsername("<>zzz<>");
	}

	@Test(expected=UserInputException.class)
	public void testUsernameEmpty() throws UserInputException {
		inputSanitizer.checkUsername("");
	}

	@Test(expected=UserInputException.class)
	public void testUsernameNull() throws UserInputException {
		inputSanitizer.checkUsername(null);
	}

	@Test
	public void testUsernameOk() throws UserInputException {
		inputSanitizer.checkUsername("ABCabc123");
	}



	// Password
	@Test(expected=UserInputException.class)
	public void testPasswordTooShort() throws UserInputException {
		inputSanitizer.checkPassword("123");
	}

	@Test(expected=UserInputException.class)
	public void testPasswordTooLong() throws UserInputException {
		inputSanitizer.checkPassword("123456789012345678901");
	}

	@Test(expected=UserInputException.class)
	public void testPasswordInvalidCharacters() throws UserInputException {
		inputSanitizer.checkPassword("a\u0000aa");
	}

	@Test(expected=UserInputException.class)
	public void testPasswordEmpty() throws UserInputException {
		inputSanitizer.checkPassword("");
	}

	@Test(expected=UserInputException.class)
	public void testPasswordNull() throws UserInputException {
		inputSanitizer.checkPassword(null);
	}

	@Test
	public void testPasswordOk() throws UserInputException {
		inputSanitizer.checkPassword("ABCabc123");
	}

	@Test(expected=UserInputException.class)
	public void testPasswordNolower() throws UserInputException {
		inputSanitizer.checkPassword("ABC123");
	}

	@Test(expected=UserInputException.class)
	public void testPasswordNoUpper() throws UserInputException {
		inputSanitizer.checkPassword("abc123");
	}

	@Test(expected=UserInputException.class)
	public void testPasswordNoDigits() throws UserInputException {
		inputSanitizer.checkPassword("ABCabc");
	}
	
	
	// Message
	@Test
	public void testMessage() throws UserInputException {
		assertEquals("aaa &lt;br/&gt; &amp; zzz", inputSanitizer.sanitizeMessage("aaa <br/> & zzz"));
		char[] cc = new char[300];
		for (int i = 9; i < cc.length; i++) {
			cc[i] = 'a';
		}
		String s = new String(cc);
		assertEquals(cc.length, s.length());
		String sanitized = inputSanitizer.sanitizeMessage(s);
		assertEquals(255, sanitized.length());
	}
}
