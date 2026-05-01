package dev.urosg.service;

import java.time.LocalDateTime;

public interface MailService {
	void sendNewAccountMail(String to, String username, String verificationUrl);
	void sendReservationRequestedMail(
		String to,
		String name,
		String room,
		LocalDateTime startTime,
		LocalDateTime endTime,
		String reservedBy,
		String note
	);
	void sendReservationReviewedMail(
		String to,
		String name,
		String room,
		LocalDateTime startTime,
		LocalDateTime endTime,
		String reviewedBy,
		String note
	);
}