package dev.urosg.service;

import dev.urosg.model.dto.*;

import java.time.LocalDateTime;
import java.util.Set;

public interface ReservationService {
	SearchResponse<Reservation> searchReservations(SearchRequest<ReservationFilter> searchRequest);
	Reservation updateReservation(Reservation reservation);
	Reservation requestReservation(ReservationRequest request);
	Reservation reviewReservation(ReservationReview review);
}
