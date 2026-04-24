package dev.urosg.service.impl;

import dev.urosg.model.dto.Reservation;
import dev.urosg.model.dto.ReservationRequest;
import dev.urosg.repository.ReservationRepository;
import dev.urosg.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional
@Service
public class ReservationServiceImpl implements ReservationService {

	private final ReservationRepository reservationRepository;

	@Override
	public Set<Reservation> getAllReservations(LocalDateTime start, LocalDateTime end) {
		return Set.of();
	}

	@Override
	public Set<Reservation> getReservations(UUID roomUuid) {
		return Set.of();
	}

	@Override
	public void requestReservation(ReservationRequest request) {

	}

	@Override
	public Reservation reviewReservation(UUID uuid) {
		return null;
	}
}
