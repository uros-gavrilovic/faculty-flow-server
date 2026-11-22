package dev.urosg.controller;

import dev.urosg.config.JacksonConfig;
import dev.urosg.context.RequestContext;
import dev.urosg.model.dto.ReservationRequest;
import dev.urosg.model.dto.ReservationReview;
import dev.urosg.service.ReservationService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(ReservationController.class)
@Import(JacksonConfig.class) // ObjectMapper used by the security error handler
@ActiveProfiles("test")
class ReservationControllerTest {

	@Autowired MockMvc mockMvc;

	@MockitoBean ReservationService reservationService;
	@MockitoBean RequestContext requestContext; // Needed for core's TokenInterceptor class

	@Nested
	@WithMockUser(username = "john.doe", roles = "USER")
	class RequestReservation {

		@Test
		void rejectsEndTimeBeforeStartTime() throws Exception {
			mockMvc.perform(post("/api/reservation/request")
					.contentType(MediaType.APPLICATION_JSON)
					.content(requestBody("2026-11-20T12:00:00", "2026-11-20T10:00:00")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400));

			verify(reservationService, never()).requestReservation(any());
		}

		@Test
		void rejectsReservationLongerThanMaxDuration() throws Exception {
			mockMvc.perform(post("/api/reservation/request")
					.contentType(MediaType.APPLICATION_JSON)
					.content(requestBody("2026-11-20T08:00:00", "2026-11-20T17:00:00")))
				.andExpect(status().isBadRequest());

			verify(reservationService, never()).requestReservation(any());
		}

		@Test
		void acceptsValidRequest() throws Exception {
			mockMvc.perform(post("/api/reservation/request")
					.contentType(MediaType.APPLICATION_JSON)
					.content(requestBody("2026-11-20T10:00:00", "2026-11-20T12:00:00")))
				.andExpect(status().isOk());

			verify(reservationService).requestReservation(any(ReservationRequest.class));
		}
	}

	@Nested
	class Security {

		@Test
		void rejectsRequestWithoutLoggedInUser() throws Exception {
			mockMvc.perform(
					post("/api/reservation/request")
					.contentType(MediaType.APPLICATION_JSON)
					.content(requestBody("2026-11-20T10:00:00", "2026-11-20T12:00:00"))
				).andExpect(status().isUnauthorized());

			verify(reservationService, never()).requestReservation(any());
		}

		@Test
		@WithMockUser(username = "john.doe", roles = "USER")
		void forbidsReviewForRegularUser() throws Exception {
			mockMvc.perform(post("/api/reservation/review")
					.contentType(MediaType.APPLICATION_JSON)
					.content(reviewBody()))
				.andDo(result -> log.debug(result.getResponse().getContentAsString()))
				.andExpect(status().isForbidden());

			verify(reservationService, never()).reviewReservation(any());
		}

		@Test
		@WithMockUser(username = "admin", roles = "ADMINISTRATOR")
		void allowsReviewForAdministrator() throws Exception {
			mockMvc.perform(post("/api/reservation/review")
					.contentType(MediaType.APPLICATION_JSON)
					.content(reviewBody()))
				.andExpect(status().isOk());

			verify(reservationService).reviewReservation(any(ReservationReview.class));
		}
	}

	private static String requestBody(String startTime, String endTime) {
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

	private static String reviewBody() {
		return """
			{
				"uuid": "%s",
				"status": "ACCEPTED",
				"comment": "Looks good"
			}
			""".formatted(UUID.randomUUID());
	}
}
