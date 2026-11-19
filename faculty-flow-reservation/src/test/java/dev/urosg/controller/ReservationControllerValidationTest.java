package dev.urosg.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.urosg.exception.GlobalExceptionHandler;
import dev.urosg.model.dto.ReservationRequest;
import dev.urosg.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class ReservationControllerValidationTest {

	private ReservationService reservationService;
	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		reservationService = mock(ReservationService.class);
		mockMvc = MockMvcBuilders
			.standaloneSetup(new ReservationController(reservationService))
			.setControllerAdvice(new GlobalExceptionHandler())
			.build();
	}

	private static String body(String startTime, String endTime) {
		return """
			{
				"name": "Algorithms lecture",
				"roomCode": "A101",
				"startTime": "%s",
				"endTime": "%s",
				"eventType": "LECTURE"
			}
			""".formatted(startTime, endTime);
	}

	@Test
	void rejectsEndTimeBeforeStartTime() throws Exception {
		mockMvc.perform(post("/api/reservation/request")
				.contentType(MediaType.APPLICATION_JSON)
				.content(body("2026-11-20T12:00:00", "2026-11-20T10:00:00")))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value(400));

		verify(reservationService, never()).requestReservation(any());
	}

	@Test
	void rejectsReservationLongerThanMaxDuration() throws Exception {
		mockMvc.perform(post("/api/reservation/request")
				.contentType(MediaType.APPLICATION_JSON)
				.content(body("2026-11-20T08:00:00", "2026-11-20T17:00:00")))
			.andExpect(status().isBadRequest());

		verify(reservationService, never()).requestReservation(any());
	}

	@Test
	void acceptsValidRequest() throws Exception {
		when(reservationService.requestReservation(any(ReservationRequest.class))).thenReturn(null);

		mockMvc.perform(post("/api/reservation/request")
				.contentType(MediaType.APPLICATION_JSON)
				.content(body("2026-11-20T10:00:00", "2026-11-20T12:00:00")))
			.andExpect(status().isOk());

		verify(reservationService).requestReservation(any(ReservationRequest.class));
	}
}
