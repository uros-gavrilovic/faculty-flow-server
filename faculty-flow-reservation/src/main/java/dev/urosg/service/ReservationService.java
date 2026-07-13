package dev.urosg.service;

import dev.urosg.model.dto.*;

import java.time.LocalDateTime;
import java.util.Set;

public interface ReservationService {
	SearchResponse<Reservation> searchReservations(SearchRequest<ReservationFilter> searchRequest);
	Set<Reservation> getReservations(LocalDateTime start, LocalDateTime end);
	Set<Reservation> getReservations(String roomCode, LocalDateTime start, LocalDateTime end);
	Set<Reservation> getReservationRequests(LocalDateTime start, LocalDateTime end);
	Reservation updateReservation(Reservation reservation);
	Reservation requestReservation(ReservationRequest request);
	Reservation reviewReservation(ReservationReview review);
}
