package dev.urosg.kafka.producer;

import dev.urosg.kafka.KafkaTopics;
import dev.urosg.model.dto.Reservation;
import dev.urosg.model.event.ReservationRequestedEvent;
import dev.urosg.model.event.ReservationReviewedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ReservationEventProducer {

	private final KafkaTemplate<String, Object> kafkaTemplate;

	public void sendReservationRequestedEvent(Set<String> emails, Reservation reservation) {
		kafkaTemplate.send(
			KafkaTopics.RESERVATION_REQUESTED,
			reservation.room(),
			new ReservationRequestedEvent(
				reservation.uuid(),
				reservation.name(),
				reservation.room(),
				reservation.startTime(),
				reservation.endTime(),
				reservation.reservedBy(),
				reservation.note(),
				emails
			)
		);
	}

	public void sendReservationReviewedEvent(String email, Reservation reservation) {
		kafkaTemplate.send(
			KafkaTopics.RESERVATION_REVIEWED,
			reservation.room(),
			new ReservationReviewedEvent(
				reservation.uuid(),
				reservation.name(),
				reservation.room(),
				reservation.startTime(),
				reservation.endTime(),
				reservation.reviewedBy(),
				reservation.status().toString(),
				reservation.comment(),
				email
			)
		);
	}
}