package dev.urosg.service;

import data.ReservationRequestTestData;
import data.ReservationReviewTestData;
import data.UserTestData;
import dev.urosg.client.RoomClient;
import dev.urosg.client.UserClient;
import dev.urosg.context.RequestContext;
import dev.urosg.kafka.producer.ReservationEventProducer;
import dev.urosg.model.dto.*;
import dev.urosg.model.entity.ReservationEntity;
import dev.urosg.model.enumeration.ReservationStatus;
import dev.urosg.repository.ReservationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Testcontainers // Lets JUnit manage the @Container fields below
@SpringBootTest
@ActiveProfiles("test")
class ReservationServiceIntegrationTest {

	@Container
	static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17-alpine");

	@DynamicPropertySource
	static void databaseProperties(DynamicPropertyRegistry registry) {
		// The port is picked by random, so this configuration has to happen at runtime
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
		registry.add("spring.flyway.url", postgres::getJdbcUrl);
		registry.add("spring.flyway.user", postgres::getUsername);
		registry.add("spring.flyway.password", postgres::getPassword);
	}

	@MockitoBean RoomClient roomClient;
	@MockitoBean UserClient userClient;
	@MockitoBean RequestContext requestContext;
	@MockitoBean ReservationEventProducer reservationEventProducer;

	@Autowired ReservationService reservationService;
	@Autowired ReservationRepository reservationRepository;

	@BeforeEach
	void initMocks() {
		when(roomClient.getAllRooms())
			.thenReturn(Set.of(
				new Room(UUID.randomUUID(), "Room A101", "A101")
			));
		when(userClient.getAdmins())
			.thenReturn(Set.of(
				new User("Admin", "Admin", "admin@faculty.com", "admin")
			));
	}

	@AfterEach
	void cleanDatabaseAfterEach() {
		reservationRepository.deleteAll();
	}

	@Test
	void requestReservation_savesPendingReservationToDatabase_andNotifiesAdmins() {
		ReservationRequest request = ReservationRequestTestData.VALID_REQUEST();

		Reservation result = reservationService.requestReservation(request);

		assertThat(result.uuid()).isNotNull();
		assertThat(result.status()).isEqualTo(ReservationStatus.PENDING);

		ReservationEntity savedEntity = reservationRepository.findByUuid(result.uuid())
			.orElseThrow();
		assertThat(savedEntity.getName()).isEqualTo(request.name());
		assertThat(savedEntity.getRoom()).isEqualTo(ReservationRequestTestData.VALID_REQUEST().roomCode());
		assertThat(savedEntity.getReservedBy()).isEqualTo(ReservationRequestTestData.VALID_REQUEST().reservedBy());
		assertThat(savedEntity.getStartTime()).isEqualTo(request.startTime());
		assertThat(savedEntity.getEndTime()).isEqualTo(request.endTime());
		assertThat(savedEntity.getStatus()).isEqualTo(ReservationStatus.PENDING);

		// ---------- SIDE EFFECTS ----------
		verify(reservationEventProducer).sendReservationRequestedEvent(eq(Set.of("admin@faculty.com")), eq(result));
	}

	@Test
	void reviewReservationTest() {
		when(requestContext.getToken()).thenReturn(UserTestData.USER_TOKEN());

		ReservationRequest reservationRequest = ReservationRequestTestData.VALID_REQUEST();
		Reservation reservationRequestResult = reservationService.requestReservation(reservationRequest);

		ReservationReview reviewRequest = ReservationReviewTestData.ACCEPTED_REVIEW(reservationRequestResult.uuid());

		when(requestContext.getToken()).thenReturn(UserTestData.ADMIN_TOKEN()); // Added for clarity, not required
		when(userClient.getUserByUsername(reservationRequest.reservedBy()))
			.thenReturn(UserTestData.USER());

		Reservation reviewedReservation = reservationService.reviewReservation(reviewRequest);

		assertThat(reviewedReservation.uuid()).isEqualTo(reservationRequestResult.uuid());
		assertThat(reviewedReservation.status()).isEqualTo(ReservationStatus.ACCEPTED);
		assertThat(reviewedReservation.status()).isNotEqualTo(reservationRequestResult.status());
		assertThat(reviewedReservation.comment()).isEqualTo("Chuck Norris is awesome!");

		verify(reservationEventProducer).sendReservationReviewedEvent(
			eq(UserTestData.USER().email()),
			eq(reviewedReservation)
		);
	}

	@Disabled("Deprecated, tested in ReservationControllerValidationTest as only the REST controller has @HasRole checks")
	@Test
	@WithMockUser(username = "john.doe", roles = "USER") // simulates @HasRole checks
	void insufficientReviewPrivilegesTest() {
		when(requestContext.getToken()).thenReturn(UserTestData.USER_TOKEN());

		ReservationRequest reservationRequest = ReservationRequestTestData.VALID_REQUEST();
		Reservation reservationRequestResult = reservationService.requestReservation(reservationRequest);

		ReservationReview reviewRequest = ReservationReviewTestData.ACCEPTED_REVIEW(reservationRequestResult.uuid());

		assertThatThrownBy(() -> reservationService.reviewReservation(reviewRequest))
			.isInstanceOf(AccessDeniedException.class);
	}
}
