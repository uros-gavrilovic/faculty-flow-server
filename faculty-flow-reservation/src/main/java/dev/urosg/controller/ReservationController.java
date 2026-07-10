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

	@GetMapping("/search")
	public SearchResponse<Reservation> searchReservations(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(defaultValue = "startTime") String sortBy,
		@RequestParam(defaultValue = "asc") String direction
	) {
		return reservationService.searchReservations(page, size, sortBy, direction);
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
