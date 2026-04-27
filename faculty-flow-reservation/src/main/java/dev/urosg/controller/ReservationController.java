package dev.urosg.controller;

import dev.urosg.model.dto.Reservation;
import dev.urosg.model.dto.ReservationRequest;
import dev.urosg.model.enumeration.ReservationStatus;
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
	Set<Reservation> getAllReservations(@RequestParam LocalDateTime start, @RequestParam LocalDateTime end) {
		return reservationService.getAllReservations(start, end);
	}

	@GetMapping("/{room}")
	Set<Reservation> getReservations(@PathVariable String room, @RequestParam LocalDateTime start, @RequestParam LocalDateTime end) {
		return reservationService.getReservations(room, start, end);
	}

	@PostMapping("/request")
	void requestReservation(@RequestBody ReservationRequest request) {
		reservationService.requestReservation(request);
	}

	@PostMapping("/review")
	Reservation reviewReservation(@RequestBody UUID uuid, ReservationStatus status) {
		return reservationService.reviewReservation(uuid, status);
	}
}
