package dev.urosg.service;

import dev.urosg.model.dto.Reservation;
import dev.urosg.model.dto.ReservationRequest;
import dev.urosg.model.dto.ReservationReview;
import dev.urosg.model.enumeration.ReservationStatus;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public interface ReservationService {
	Set<Reservation> getReservations(LocalDateTime start, LocalDateTime end);
	Set<Reservation> getReservations(String roomCode, LocalDateTime start, LocalDateTime end);
	Set<Reservation> getReservationRequests(LocalDateTime start, LocalDateTime end);
	Reservation requestReservation(ReservationRequest request);
	Reservation reviewReservation(ReservationReview review);
}
