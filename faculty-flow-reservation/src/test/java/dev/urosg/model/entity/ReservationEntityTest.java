package dev.urosg.model.entity;

import data.ReservationEntityTestData;
import dev.urosg.model.enumeration.ReservationStatus;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class ReservationEntityTest {

	private static ValidatorFactory validatorFactory;
	private static Validator validator;

	private ReservationEntity reservation;

	@BeforeAll
	static void initValidator() {
		validatorFactory = Validation.buildDefaultValidatorFactory();
		validator = validatorFactory.getValidator();
	}

	@AfterAll
	static void closeValidator() {
		validatorFactory.close();
	}

	@BeforeEach
	void setUp() {
		reservation = ReservationEntityTestData.ACCEPTED_RESERVATION();
	}

	@Nested
	class DefaultValues {
		@Test
		void newEntityDefaultValues() {
			ReservationEntity newReservation = ReservationEntity.builder().build();
			Assertions.assertEquals(ReservationStatus.PENDING, newReservation.getStatus());
			Assertions.assertNull(newReservation.getReviewedBy());
		}
	}

	@Nested
	class EqualityAndToString {
		@Test
		void testEquals() {
			ReservationEntity otherReservation = ReservationEntityTestData.ACCEPTED_RESERVATION();
			assertEquals(reservation, otherReservation);
			assertEquals(reservation.hashCode(), otherReservation.hashCode());
		}

		@Test
		void testNotEquals() {
			ReservationEntity rejectedReservation = ReservationEntityTestData.REJECTED_RESERVATION();
			assertNotEquals(reservation, rejectedReservation);
		}

		@Test
		void toStringContainsUuid() {
			assertTrue(reservation.toString().contains("uuid=" + reservation.getUuid()));
		}
	}

	@Nested
	class BeanValidation {

		private Set<String> violatedFields(ReservationEntity entity) {
			return validator.validate(entity).stream()
				.map(ConstraintViolation::getPropertyPath)
				.map(Object::toString)
				.collect(Collectors.toSet());
		}

		@Test
		void validReservationHasNoViolations() {
			assertTrue(validator.validate(reservation).isEmpty());
		}

		@Test
		void rejectsEndTimeBeforeStartTime() {
			reservation.setEndTime(ReservationEntityTestData.START_TIME.minusMinutes(1));
			assertEquals(Set.of("timeRangeValid"), violatedFields(reservation));
		}

		@Test
		void rejectsEqualStartAndEndTime() {
			reservation.setEndTime(ReservationEntityTestData.START_TIME);
			assertEquals(Set.of("timeRangeValid"), violatedFields(reservation));
		}

		@Test
		void acceptsOneMinuteReservation() {
			reservation.setEndTime(ReservationEntityTestData.START_TIME.plusMinutes(1));
			assertTrue(violatedFields(reservation).isEmpty());
		}

		@Test
		void missingTimesAreNotReportedAsInvalidRange() {
			reservation.setStartTime(null);
			reservation.setEndTime(null);
			assertTrue(violatedFields(reservation).isEmpty());
		}
	}

}
