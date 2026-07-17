package dev.urosg.service;

import dev.urosg.model.event.ReservationRequestedEvent;
import dev.urosg.model.event.ReservationReviewedEvent;

import java.time.LocalDateTime;

public interface MailService {
	void sendNewAccountMail(String to, String username, String verificationUrl);
	void sendReservationRequestedMail(String to, ReservationRequestedEvent event);
	void sendReservationReviewedMail(String to, ReservationReviewedEvent event
	);
}