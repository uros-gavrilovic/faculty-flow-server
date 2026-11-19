package dev.urosg.model.entity;

import data.ReservationTestData;
import dev.urosg.model.enumeration.ReservationStatus;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;

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
		reservation = ReservationTestData.ACCEPTED_RESERVATION();
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
			ReservationEntity otherReservation = ReservationTestData.ACCEPTED_RESERVATION();
			assertEquals(reservation, otherReservation);
			assertEquals(reservation.hashCode(), otherReservation.hashCode());
		}

		@Test
		void testNotEquals() {
			ReservationEntity rejectedReservation = ReservationTestData.REJECTED_RESERVATION();
			assertNotEquals(reservation, rejectedReservation);
		}

		@Test
		void toStringContainsUuid() {
			assertTrue(reservation.toString().contains("uuid=" + reservation.getUuid()));
		}
	}

	@Nested
	class BeanValidation {

	}

}
