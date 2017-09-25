package org.coursera.cybersecurity.capstone.group8.internal;

@SuppressWarnings("serial")
public class UserInputException extends Exception {

	public UserInputException() {
	}

	public UserInputException(String arg0) {
		super(arg0);
	}

	public UserInputException(Throwable arg0) {
		super(arg0);
	}

	public UserInputException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public UserInputException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}
