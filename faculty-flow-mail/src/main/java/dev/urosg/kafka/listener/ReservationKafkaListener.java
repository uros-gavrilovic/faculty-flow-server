package dev.urosg.kafka.listener;

import dev.urosg.kafka.KafkaTopics;
import dev.urosg.model.event.ReservationRequestedEvent;
import dev.urosg.model.event.ReservationReviewedEvent;
import dev.urosg.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationKafkaListener {

	private final MailService mailService;

	@KafkaListener(
		topics = KafkaTopics.RESERVATION_REQUESTED,
		groupId = "mail-service"
	)
	public void handle(ReservationRequestedEvent event) {
		event.adminEmails().forEach(email -> {
			mailService.sendReservationRequestedMail(email, event);
		});
	}

	@KafkaListener(
		topics = KafkaTopics.RESERVATION_REVIEWED,
		groupId = "mail-service"
	)
	public void handle(ReservationReviewedEvent event) {
		mailService.sendReservationReviewedMail(event.recipientEmail(), event);
	}
}
