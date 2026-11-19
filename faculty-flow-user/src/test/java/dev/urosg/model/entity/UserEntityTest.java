package dev.urosg.model.entity;

import dev.urosg.data.user.UserTestData;
import dev.urosg.model.constant.PatternConstant;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserEntityTest {

	private static ValidatorFactory validatorFactory;
	private static Validator validator;

	private UserEntity user;

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
		user = UserTestData.VALID_USER();
	}

	private Set<String> getViolatedFields(UserEntity entity) {
		return validator.validate(entity).stream()
			.map(ConstraintViolation::getPropertyPath)
			.map(Object::toString)
			.collect(Collectors.toSet());
	}

	@Nested
	class DefaultValues {
		@Test
		void newEntityIsNotVerified() {
			assertEquals(Boolean.FALSE, new UserEntity().getIsVerified());
		}

		@Test
		void newEntityHasNoGeneratedValues() {
			UserEntity fresh = new UserEntity();
			assertNull(fresh.getId());
			assertNull(fresh.getUuid());
			assertNull(fresh.getRoles());
		}
	}

	@Nested
	class EqualityAndToString {
		@Test
		void testEquals() {
			UserEntity other = UserTestData.VALID_USER();
			assertEquals(user, other);
			assertEquals(user.hashCode(), other.hashCode());
		}

		@Test
		void testNotEquals() {
			UserEntity other = UserTestData.NEW_USER();
			assertNotEquals(user, other);
		}

		@Test
		void toStringContainsUsername() {
			assertTrue(user.toString().contains("username=john.doe"));
		}
	}

	@Nested
	class BeanValidation {
		@Test
		void validUserHasNoViolations() {
			assertTrue(validator.validate(user).isEmpty());
		}

		@Test
		void optionalFieldsMayBeNull() {
			user.setEmail(null);
			user.setRoles(null);
			user.setIsVerified(null);
			assertTrue(validator.validate(user).isEmpty());
		}

		@Test
		void acceptsUsernameAtMaxLength() {
			user.setUsername("a".repeat(PatternConstant.USERNAME_MAX_LENGTH));
			assertFalse(getViolatedFields(user).contains("username"));
		}

		@ParameterizedTest
		@NullAndEmptySource
		@ValueSource(strings = { "ab", "john doe", "john@doe", "jöhn", "john/doe" })
		void rejectsInvalidUsernames(String username) {
			user.setUsername(username);
			assertTrue(getViolatedFields(user).contains("username"));
		}

		@ParameterizedTest
		@ValueSource(strings = { "abc", "john_doe", "john-doe", "John.Doe.99" })
		void acceptsValidUsernames(String username) {
			user.setUsername(username);
			assertFalse(getViolatedFields(user).contains("username"));
		}

		@Test
		void rejectsUsernameOverMaxLength() {
			user.setUsername("a".repeat(PatternConstant.USERNAME_MAX_LENGTH + 1));
			assertTrue(getViolatedFields(user).contains("username"));
		}

		@ParameterizedTest
		@ValueSource(strings = { "$2a$", "$2b$", "$2y$" })
		void acceptsBcryptHashVariants(String prefix) {
			user.setPassword(prefix + "x".repeat(56));
			assertFalse(getViolatedFields(user).contains("password"));
		}

		@Test
		void rejectsNullPassword() {
			user.setPassword(null);
			assertTrue(getViolatedFields(user).contains("password"));
		}

		@Test
		void rejectsPlainTextPassword() {
			user.setPassword("plainTextPassword");
			assertTrue(getViolatedFields(user).contains("password"));
		}

		@Test
		void rejectsBcryptPrefixWithWrongLength() {
			user.setPassword("$2a$" + "x".repeat(55));
			assertTrue(getViolatedFields(user).contains("password"));

			user.setPassword("$2a$" + "x".repeat(57));
			assertTrue(getViolatedFields(user).contains("password"));
		}

		@Test
		void rejectsUnsupportedBcryptVersionWithCorrectLength() {
			user.setPassword("$2x$" + "x".repeat(56));
			assertTrue(getViolatedFields(user).contains("password"));
		}

		// --- email ---

		@ParameterizedTest
		@ValueSource(strings = { "a@b.co", "first.last+tag@sub.domain.org", "user_1%x@mail-server.rs" })
		void acceptsValidEmails(String email) {
			user.setEmail(email);
			assertFalse(getViolatedFields(user).contains("email"));
		}

		@ParameterizedTest
		@ValueSource(strings = { "", "plainaddress", "@example.com", "user@", "user@example", "user@example.c", "user name@example.com" })
		void rejectsInvalidEmails(String email) {
			user.setEmail(email);
			assertTrue(getViolatedFields(user).contains("email"));
		}

		// --- first / last name ---

		@ParameterizedTest
		@NullAndEmptySource
		void rejectsMissingFirstName(String firstName) {
			user.setFirstName(firstName);
			assertTrue(getViolatedFields(user).contains("firstName"));
		}

		@ParameterizedTest
		@NullAndEmptySource
		void rejectsMissingLastName(String lastName) {
			user.setLastName(lastName);
			assertTrue(getViolatedFields(user).contains("lastName"));
		}

		@Test
		void reportsAllViolationsAtOnce() {
			UserEntity empty = new UserEntity();
			assertEquals(
				Set.of("username", "password", "firstName", "lastName"),
				getViolatedFields(empty)
			);
		}
	}
}
