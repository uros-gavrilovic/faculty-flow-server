package dev.urosg.model;

import lombok.Getter;

@Getter
public enum MailType {
	VERIFY_ACCOUNT(TemplatePath.VERIFY_ACCOUNT, "FacultyFlow • Verify account"),
	RESERVATION_REQUESTED(TemplatePath.RESERVATION_REQUESTED, "FacultyFlow • Reservation requested"),
	RESERVATION_REVIEWED(TemplatePath.RESERVATION_REVIEWED, "FacultyFlow • Reservation reviewed");

	private final String templatePath;
	private final String subject;

	MailType(String templatePath, String subject) {
		this.templatePath = templatePath;
		this.subject = subject;
	}
}

class TemplatePath {
	static final String VERIFY_ACCOUNT = "account/verify-account";
	static final String RESERVATION_REQUESTED = "reservation/reservation-requested";
	static final String RESERVATION_REVIEWED = "reservation/reservation-reviewed";
}
