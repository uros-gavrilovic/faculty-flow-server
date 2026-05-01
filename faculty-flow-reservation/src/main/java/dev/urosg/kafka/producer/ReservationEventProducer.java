package dev.urosg.kafka.producer;

import dev.urosg.kafka.KafkaTopics;
import dev.urosg.kafka.event.RoomReservationRequestedEvent;
import dev.urosg.kafka.event.RoomReservationReviewedEvent;
import dev.urosg.model.dto.Reservation;
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
			new RoomReservationRequestedEvent(
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
			new RoomReservationReviewedEvent(
				reservation.name(),
				reservation.room(),
				reservation.startTime(),
				reservation.endTime(),
				reservation.reviewedBy(),
				reservation.note(),
				email
			)
		);
	}
}