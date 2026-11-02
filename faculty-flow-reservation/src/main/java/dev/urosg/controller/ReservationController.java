package dev.urosg.controller;

import dev.urosg.annotation.HasRole;
import dev.urosg.model.dto.*;
import dev.urosg.model.enumeration.UserRole;
import dev.urosg.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
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
	public Reservation updateReservation(@RequestBody Reservation reservation) {
		return reservationService.updateReservation(reservation);
	}

	@PostMapping("/request")
	public Reservation requestReservation(@RequestBody ReservationRequest request) {
		return reservationService.requestReservation(request);
	}

	@HasRole(UserRole.ADMINISTRATOR)
	@PostMapping("/review")
	public Reservation reviewReservation(@RequestBody ReservationReview review) {
		return reservationService.reviewReservation(review);
	}
}
