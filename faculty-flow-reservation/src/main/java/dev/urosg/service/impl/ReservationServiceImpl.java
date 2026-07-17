package dev.urosg.service.impl;

import dev.urosg.adapter.ReservationAdapter;
import dev.urosg.client.RoomClient;
import dev.urosg.client.UserClient;
import dev.urosg.context.RequestContext;
import dev.urosg.kafka.producer.ReservationEventProducer;
import dev.urosg.model.dto.*;
import dev.urosg.model.entity.ReservationEntity;
import dev.urosg.model.enumeration.ReservationStatus;
import dev.urosg.repository.ReservationRepository;
import dev.urosg.repository.specification.ReservationSpecifications;
import dev.urosg.service.ReservationService;
import dev.urosg.util.AuthenticationUtils;
import dev.urosg.util.SearchUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
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

	private final RequestContext requestContext;
	private final RoomClient roomClient;
	private final ReservationRepository reservationRepository;
	private final ReservationEventProducer reservationEventProducer;
	private final UserClient userClient;

	@Override
	public SearchResponse<Reservation> searchReservations(SearchRequest<ReservationFilter> request) {
		Page<Reservation> page = reservationRepository.findAll(
			ReservationSpecifications.toSpecification(request.filter()),
			SearchUtil.toPageable(request)
		);

		return SearchUtil.toSearchResponse(page);
	}

	@Override
	public Reservation updateReservation(Reservation reservation) {
		ReservationEntity reservationEntity = this.reservationRepository.findByUuid(reservation.uuid())
			.orElseThrow(() -> new IllegalArgumentException("Reservation with UUID '" + reservation.uuid() + "' not found"));

		reservationEntity.setName(reservation.name());
		reservationEntity.setRoom(reservation.room());
		reservationEntity.setEventType(reservation.eventType());
		reservationEntity.setStartTime(reservation.startTime());
		reservationEntity.setEndTime(reservation.endTime());
		reservationEntity.setReservedBy(reservation.reservedBy());
		reservationEntity.setNote(reservation.note());

		ReservationEntity updatedEntity = this.reservationRepository.saveAndFlush(reservationEntity);
		log.info(
			"Updated reservation '{}' ({}) for roomCode '{}'",
			updatedEntity.getName(), updatedEntity.getUuid(), updatedEntity.getRoom()
		);

		return ReservationAdapter.toDto(updatedEntity);
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
				.eventType(request.eventType())
				.startTime(request.startTime())
				.endTime(request.endTime())
				.reservedBy(request.reservedBy() == null ? AuthenticationUtils.getCurrentUserUsername(requestContext) : request.reservedBy())
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
		reservationEntity.setReviewedBy(AuthenticationUtils.getCurrentUserUsername(requestContext));
		reservationEntity.setComment(review.comment());

		ReservationEntity updatedEntity = this.reservationRepository.saveAndFlush(reservationEntity);
		log.info(
			"Reviewed reservation '{}' ({}) for roomCode '{}' to status '{}'",
			updatedEntity.getName(), updatedEntity.getUuid(), updatedEntity.getRoom(), updatedEntity.getStatus()
		);

		Reservation reservation = ReservationAdapter.toDto(updatedEntity);
		log.info("Reservation review: {}", reservation);

		User user = userClient.getUserByUsername(reservation.reservedBy());
		if (user == null) throw new IllegalArgumentException("User with username '" + reservation.reservedBy() + "' not found");

		reservationEventProducer.sendReservationReviewedEvent(user.email(), reservation);

		return reservation;
	}
}
