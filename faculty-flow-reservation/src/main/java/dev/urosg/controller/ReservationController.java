package dev.urosg.controller;

import dev.urosg.model.dto.*;
import dev.urosg.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/reservation")
public class ReservationController {

	private final ReservationService reservationService;

	@GetMapping
	public Reservation getReservation(@RequestParam UUID uuid) {
		return reservationService.getReservation(uuid);
	}

	@PostMapping("/search")
	public SearchResponse<Reservation> searchReservations(@RequestBody SearchRequest<ReservationFilter> searchRequest) {
		return reservationService.searchReservations(searchRequest);
	}

	@PutMapping
	Reservation updateReservation(@RequestBody Reservation reservation) {
		return reservationService.updateReservation(reservation);
	}

	@PostMapping("/request")
	Reservation requestReservation(@RequestBody ReservationRequest request) {
		return reservationService.requestReservation(request);
	}

	@PostMapping("/review")
	Reservation reviewReservation(@RequestBody ReservationReview review) {
		return reservationService.reviewReservation(review);
	}
}
