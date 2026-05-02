package dev.urosg.kafka.listener;

import dev.urosg.kafka.KafkaTopics;
import dev.urosg.kafka.event.RoomReservationRequestedEvent;
import dev.urosg.kafka.event.RoomReservationReviewedEvent;
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
	public void handle(RoomReservationRequestedEvent event) {
		event.adminEmails().forEach(email -> {
			mailService.sendReservationRequestedMail(
				email,
				event.name(),
				event.room(),
				event.startTime(),
				event.endTime(),
				event.reservedBy(),
				event.note()
			);
		});
	}

	@KafkaListener(
		topics = KafkaTopics.RESERVATION_REVIEWED,
		groupId = "mail-service"
	)
	public void handle(RoomReservationReviewedEvent event) {
		log.info("{}", event);

		mailService.sendReservationReviewedMail(
			event.recipientEmail(),
			event.name(),
			event.room(),
			event.startTime(),
			event.endTime(),
			event.reviewedBy(),
			event.status(),
			event.comment()
		);
	}
}
