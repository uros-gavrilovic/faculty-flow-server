package dev.urosg.service.impl;

import dev.urosg.adapter.ReservationAdapter;
import dev.urosg.client.RoomClient;
import dev.urosg.client.UserClient;
import dev.urosg.kafka.producer.ReservationEventProducer;
import dev.urosg.model.dto.*;
import dev.urosg.model.entity.ReservationEntity;
import dev.urosg.model.enumeration.ReservationStatus;
import dev.urosg.repository.ReservationRepository;
import dev.urosg.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ReservationServiceImpl implements ReservationService {

	private final RoomClient roomClient;
	private final ReservationRepository reservationRepository;

	private final ReservationEventProducer reservationEventProducer;
	private final UserClient userClient;

	@Override
	public Set<Reservation> getReservations(LocalDateTime start, LocalDateTime end) {
		validateArguments(start, end);

		Set<ReservationEntity> reservationEntities = reservationRepository
			.findByStartTimeLessThanAndEndTimeGreaterThan(end, start);

		return mapToDTOs(reservationEntities);
	}

	@Override
	public Set<Reservation> getReservations(String roomCode, LocalDateTime start, LocalDateTime end) {
		validateArguments(start, end);

		Set<ReservationEntity> reservationEntities = reservationRepository
			.findByStartTimeLessThanAndEndTimeGreaterThanAndRoom(end, start, roomCode);

		return mapToDTOs(reservationEntities);
	}

	@Override
	public Set<Reservation> getReservationRequests(LocalDateTime start, LocalDateTime end) {
		validateArguments(start, end);

		Set<ReservationEntity> reservationRequestEntities =
			reservationRepository.findByStartTimeLessThanAndEndTimeGreaterThanAndStatus(end, start, ReservationStatus.PENDING);

		return mapToDTOs(reservationRequestEntities);
	}

	@Override
	public Reservation requestReservation(ReservationRequest request) {
		Set<Room> rooms = roomClient.getAllRooms();
		rooms.stream()
			.filter(r -> r.code().equalsIgnoreCase(request.roomCode()))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("Room with code '" + request.roomCode() + "' not found"));

		ReservationEntity newReservation = ReservationEntity
			.builder()
				.uuid(UUID.randomUUID())
				.name(request.name())
				.room(request.roomCode())
				.startTime(request.startTime())
				.endTime(request.endTime())
				.reservedBy(request.reservedBy())
				.note(request.note())
				.status(ReservationStatus.PENDING)
			.build();

		ReservationEntity savedReservation = reservationRepository.saveAndFlush(newReservation);
		log.info(
			"Created new reservation request '{}' ({}) for roomCode '{}' by user '{}'",
			savedReservation.getName(), savedReservation.getUuid(), savedReservation.getRoom(), request.reservedBy()
		);

		Reservation reservation = ReservationAdapter.toDto(savedReservation);
		Set<String> adminEmails = userClient.getAdmins().stream()
			.map(User::email)
			.collect(java.util.stream.Collectors.toSet());

		reservationEventProducer.sendReservationRequestedEvent(adminEmails, reservation);

		return reservation;
	}

	@Override
	public Reservation reviewReservation(ReservationReview review) {
		ReservationEntity reservationEntity = this.reservationRepository.findByUuid(review.uuid()).orElseThrow(
			() -> new IllegalArgumentException("Reservation with UUID '" + review.uuid() + "' not found")
		);

		reservationEntity.setStatus(review.status());
//		reservationEntity.setReviewedBy(review.reviewedBy()); // TODO: Add authentication util

		ReservationEntity updatedEntity = this.reservationRepository.saveAndFlush(reservationEntity);
		log.info(
			"Updated reservation '{}' ({}) for roomCode '{}' to status '{}'",
			updatedEntity.getName(), updatedEntity.getUuid(), updatedEntity.getRoom(), updatedEntity.getStatus()
		);

		Reservation reservation = ReservationAdapter.toDto(updatedEntity);

		User user = userClient.getUserByUsername(reservation.reservedBy());
		if (user == null) throw new IllegalArgumentException("User with username '" + reservation.reservedBy() + "' not found");

		reservationEventProducer.sendReservationReviewedEvent(user.email(), reservation);

		return reservation;
	}

	private static void validateArguments(LocalDateTime start, LocalDateTime end) {
		if (start == null || end == null) throw new IllegalArgumentException("Start and end date must not be null");
		if (start.isAfter(end)) throw new IllegalArgumentException("Start date must be before end date");
	}

	private static @NonNull Set<Reservation> mapToDTOs(Set<ReservationEntity> reservationEntities) {
		return reservationEntities.stream()
			.map(ReservationAdapter::toDto)
			.collect(java.util.stream.Collectors.toSet());
	}
}
