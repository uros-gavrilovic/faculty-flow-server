package dev.urosg.service.impl;

import dev.urosg.adapter.ReservationAdapter;
import dev.urosg.model.dto.Reservation;
import dev.urosg.model.dto.ReservationRequest;
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

	private final ReservationRepository reservationRepository;

	@Override
	public Set<Reservation> getAllReservations(LocalDateTime start, LocalDateTime end) {
		validateArguments(start, end);

		Set<ReservationEntity> reservationEntities = reservationRepository
			.findByStartTimeLessThanAndEndTimeGreaterThan(end, start);

		return mapToDTOs(reservationEntities);
	}

	@Override
	public Set<Reservation> getReservations(String room, LocalDateTime start, LocalDateTime end) {
		validateArguments(start, end);

		Set<ReservationEntity> reservationEntities = reservationRepository
			.findByStartTimeLessThanAndEndTimeGreaterThanAndRoom(end, start, room);

		return mapToDTOs(reservationEntities);
	}

	@Override
	public void requestReservation(ReservationRequest request) {
//
//		return restClient.get()
//			.uri("http://room-service/api/rooms/exists/{name}", roomName)
//			.header(HttpHeaders.AUTHORIZATION, token)
//			.retrieve()
//			.body(Boolean.class);
//		// Show me how to call room Api via webClient

		ReservationEntity newReservation = ReservationEntity
			.builder()
				.name(request.name())
				.room(request.room())
				.startTime(request.startTime())
				.endTime(request.endTime())
				.reservedBy(request.reservedBy())
				.note(request.note())
				.status(ReservationStatus.PENDING)
			.build();

		ReservationEntity savedReservation = reservationRepository.saveAndFlush(newReservation);
		log.info(
			"Created new reservation request '{}' ({}) for room '{}'",
			savedReservation.getName(), savedReservation.getUuid(), savedReservation.getRoom()
		);
	}

	@Override
	public Reservation reviewReservation(UUID uuid, ReservationStatus status) {
		ReservationEntity reservationEntity = this.reservationRepository.findByUuid(uuid).orElseThrow(
			() -> new IllegalArgumentException("Reservation with UUID '" + uuid + "' not found")
		);

		reservationEntity.setStatus(status);

		ReservationEntity updatedEntity = this.reservationRepository.saveAndFlush(reservationEntity);
		log.info(
			"Updated reservation '{}' ({}) for room '{}' to status '{}'",
			updatedEntity.getName(), updatedEntity.getUuid(), updatedEntity.getRoom(), updatedEntity.getStatus()
		);

		return ReservationAdapter.toDto(updatedEntity);
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
