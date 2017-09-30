package org.coursera.cybersecurity.capstone.group8.internal;

import java.util.regex.Pattern;

/*
 * Every user input has to be validated in order to avoid security flaws. There fore Instances of this class should
 * be used to check user input for possible dangers
 */
public class InputSanitizer {
	private static final Pattern PATTERN_USERNAME = Pattern.compile("[a-zA-Z0-9]+");
	private static final Pattern PATTERN_PASSWORD = Pattern.compile("\\p{Graph}+");
	private static final Pattern PATTERN_LOWERCASE = Pattern.compile(".*[a-z]+.*");
	private static final Pattern PATTERN_UPPERCASE = Pattern.compile(".*[A-Z]+.*");
	private static final Pattern PATTERN_DIGITS = Pattern.compile(".*[0-9]+.*");

	/*
	 * Username should:
	 * - contain only [a-zA-Z0-9]
	 * - be at least 4 and at most 20 characters
	 * - on any errors exception is thrown
	 */
	public void checkUsername(String userId) throws UserInputException {
		if (userId == null)
			throw new UserInputException("Username is null");
		if (userId.length() < 4)
			throw new UserInputException("Username too short");
		if (userId.length() > 20)
			throw new UserInputException("Username too long");
		if (!PATTERN_USERNAME.matcher(userId).matches())
			throw new UserInputException("Username contains illegal characters");
	}

	/*
	 * Passwords are never displayed, hence pessword should:
	 * - be at least 8 and at most 20 characters
	 * - have lowercase, uppercase and digits
	 * - have only ASCII characters
	 */
	public void checkPassword(String password) throws UserInputException {
		if (password == null)
			throw new UserInputException("Password is null");
		if (password.length() < 4)
			throw new UserInputException("Password too short");
		if (password.length() > 20)
			throw new UserInputException("Password too long");
		if (!PATTERN_PASSWORD.matcher(password).matches())
			throw new UserInputException("Password contains illegal characters");
		if (!(PATTERN_LOWERCASE.matcher(password).matches() 
				&& PATTERN_UPPERCASE.matcher(password).matches()
				&& PATTERN_DIGITS.matcher(password).matches()))
			throw new UserInputException("Password must contain lower case, upper case and digits");
	}
	
	/*
	 * Spring/Hibernate will take care of SQL injections, we just need 
	 * to take care of the html and max length.
	 * We need to replace < > & to disallow all html.
	 */
	public String sanitizeMessage(String message) throws UserInputException {
		message = message.substring(0, Math.min(255, message.length()));
		message = message.replaceAll("&", "&amp;"); // Must go first
		message = message.replaceAll("<", "&lt;");
		message = message.replaceAll(">", "&gt;");
		return message;
	}

	/*
	 * Here anything goes as long as it is not empty. We never display it.
	 */
	public void checkSecretAnswer(String secretAnswer) throws UserInputException {
		if (secretAnswer == null || secretAnswer.trim().isEmpty())
			throw new UserInputException("Secret answer is empty");
	}
}
