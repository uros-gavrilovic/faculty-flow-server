package dev.urosg.model.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static data.ReservationRequestTestData.START_TIME;

import data.ReservationRequestTestData;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ReservationRequestTest {

	private static ValidatorFactory validatorFactory;
	private static Validator validator;

	@BeforeAll
	static void initValidator() {
		validatorFactory = Validation.buildDefaultValidatorFactory();
		validator = validatorFactory.getValidator();
	}

	@AfterAll
	static void closeValidator() {
		validatorFactory.close();
	}

	private static ReservationRequest.ReservationRequestBuilder validRequestBuilder() {
		return ReservationRequestTestData.VALID_REQUEST().toBuilder();
	}

	private static Set<String> getViolatedFields(ReservationRequest request) {
		return validator.validate(request).stream()
			.map(ConstraintViolation::getPropertyPath)
			.map(Object::toString)
			.collect(Collectors.toSet());
	}

	@Test
	void validRequestHasNoViolations() {
		assertTrue(validator.validate(ReservationRequestTestData.VALID_REQUEST()).isEmpty());
	}

	@Test
	void optionalFieldsMayBeNull() {
		assertTrue(validator.validate(ReservationRequestTestData.MINIMAL_REQUEST()).isEmpty());
	}

	@Nested
	class RequiredFields {

		@ParameterizedTest
		@NullAndEmptySource
		@ValueSource(strings = { " ", "\t" })
		void rejectsBlankName(String name) {
			assertEquals(Set.of("name"), getViolatedFields(validRequestBuilder().name(name).build()));
		}

		@ParameterizedTest
		@NullAndEmptySource
		@ValueSource(strings = { " " })
		void rejectsBlankRoomCode(String roomCode) {
			assertEquals(Set.of("roomCode"), getViolatedFields(validRequestBuilder().roomCode(roomCode).build()));
		}

		@Test
		void eventTypeNull() {
			assertEquals(Set.of("eventType"), getViolatedFields(validRequestBuilder().eventType(null).build()));
		}

		@Test
		void startTimeNull() {
			assertEquals(Set.of("startTime"), getViolatedFields(validRequestBuilder().startTime(null).build()));
		}

		@Test
		void endTimeNull() {
			assertEquals(Set.of("endTime"), getViolatedFields(validRequestBuilder().endTime(null).build()));
		}
	}

	@Nested
	class TimeRange {

		@Test
		void rejectsEndTimeBeforeStartTime() {
			ReservationRequest request = validRequestBuilder().endTime(START_TIME.minusHours(1)).build();
			assertEquals(Set.of("timeRangeValid"), getViolatedFields(request));
		}

		@Test
		void rejectsEqualStartAndEndTime() {
			ReservationRequest request = validRequestBuilder().endTime(START_TIME).build();
			assertEquals(Set.of("timeRangeValid"), getViolatedFields(request));
		}
	}

	@Nested
	class MaxDuration {

		@Test
		void acceptsExactlyMaxDuration() {
			ReservationRequest request = validRequestBuilder().endTime(START_TIME.plus(ReservationRequest.MAX_DURATION)).build();
			assertTrue(getViolatedFields(request).isEmpty());
		}

		@Test
		void rejectsEventsOverMaxLength() {
			ReservationRequest request = validRequestBuilder()
				.endTime(START_TIME.plus(ReservationRequest.MAX_DURATION).plusHours(1))
				.build();
			assertEquals(Set.of("durationValid"), getViolatedFields(request));
		}

		@Test
		void rejectsMultiDayReservation() {
			ReservationRequest request = validRequestBuilder().endTime(START_TIME.plusDays(2)).build();
			assertEquals(Set.of("durationValid"), getViolatedFields(request));
		}
	}
}
