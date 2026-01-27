package dev.urosg.model.constant;

public class PatternConstant {

	public static final String USERNAME_PATTERN = "^[a-zA-Z0-9._-]*$";
	public static final int USERNAME_MIN_LENGTH = 3;
	public static final int USERNAME_MAX_LENGTH = 64;

	public static final String PASSWORD_PATTERN = "^\\$2[ayb]\\$.{56}$";
	public static final int PASSWORD_LENGTH = 60;

	public static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
}
