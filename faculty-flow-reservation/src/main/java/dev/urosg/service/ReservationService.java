package dev.urosg.service;

import dev.urosg.model.dto.Reservation;
import dev.urosg.model.dto.ReservationRequest;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public interface ReservationService {
	Set<Reservation> getAllReservations(LocalDateTime start, LocalDateTime end);
	Set<Reservation> getReservations(UUID roomUuid);
	void requestReservation(ReservationRequest request);
	Reservation reviewReservation(UUID uuid);
}
