package dev.urosg.controller;

import dev.urosg.model.dto.*;
import dev.urosg.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/reservation")
public class ReservationController {

	private final ReservationService reservationService;

	@PostMapping("/search")
	public SearchResponse<Reservation> searchReservations(@RequestBody SearchRequest<ReservationFilter> searchRequest) {
		return reservationService.searchReservations(searchRequest);
	}

	@GetMapping
	Set<Reservation> getReservations(@RequestParam LocalDateTime start, @RequestParam LocalDateTime end) {
		return reservationService.getReservations(start, end);
	}

	@GetMapping("/{roomCode}")
	Set<Reservation> getReservations(@PathVariable String roomCode, @RequestParam LocalDateTime start, @RequestParam LocalDateTime end) {
		return reservationService.getReservations(roomCode, start, end);
	}

	@PutMapping
	Reservation updateReservation(@RequestBody Reservation reservation) {
		return reservationService.updateReservation(reservation);
	}

	@GetMapping("/request")
	Set<Reservation> getReservationRequests(@RequestParam LocalDateTime start, @RequestParam LocalDateTime end) {
		return reservationService.getReservationRequests(start, end);
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
