package dev.urosg.loader;

import com.github.javafaker.Faker;
import dev.urosg.model.entity.UserEntity;
import dev.urosg.model.enumeration.UserRole;
import dev.urosg.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

@Component
@Profile("dev")
@ConditionalOnProperty(name = "dataloader.init-test-data", havingValue = "true")
public class MockUserLoader {

	private static final Logger log = LoggerFactory.getLogger(MockUserLoader.class);

	// ---- Constraints declared in 'V1__init.sql' file ----
	private static final int USERNAME_MAX_LENGTH = 64;
	private static final int EMAIL_LOCAL_MAX_LENGTH = 64;
	private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9._-]*$");
	private static final Pattern PASSWORD_PATTERN = Pattern.compile("^\\$2[aby]\\$.{56}$");
	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
	private static final Pattern ILLEGAL_CHARS = Pattern.compile("[^a-z0-9._-]");

	private final Faker faker = new Faker();
	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	private final UserRepository userRepository;

	public MockUserLoader(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@PostConstruct
	void loadTestData() {
		log.warn("Loading test data ...");
		// User 'admin' is automatically created by Flyway
		if (userRepository.count() <= 1) {
			generateGenericUser(1);
			generateGenericUser(2);
			generateMockUsers(10);
		}
	}

	private void generateGenericUser(int index) {
		UserEntity u = new UserEntity();

		u.setUuid(UUID.randomUUID());
		u.setFirstName("User");
		u.setLastName(Integer.toString(index));
		u.setUsername(checkConstraints(USERNAME_PATTERN, "user" + index, "username"));
		u.setEmail(checkConstraints(EMAIL_PATTERN, "user" + index + "@example.com", "email"));
		u.setPassword(checkConstraints(PASSWORD_PATTERN, passwordEncoder.encode("user" + index), "password"));
		u.setRoles(Set.of(UserRole.USER));
		u.setIsVerified(true);

		userRepository.saveAndFlush(u);
	}

	private void generateMockUsers(int userCount) {
		Set<String> usernames = new HashSet<>(Set.of("admin"));
		Set<String> emails = new HashSet<>();

		for (int i = 0; i < userCount; i++) {
			String firstName = faker.name().firstName();
			String lastName = faker.name().lastName();

			String sluggedFirstName = slug(firstName);
			String sluggedLastName = slug(lastName);

			String username = unique(sluggedFirstName + sluggedLastName.charAt(0), usernames, USERNAME_MAX_LENGTH);
			String emailLocal = unique(sluggedFirstName + "." + sluggedLastName, emails, EMAIL_LOCAL_MAX_LENGTH);
			String email = faker.internet().safeEmailAddress(emailLocal);
			String passwordHash = passwordEncoder.encode(faker.internet().password(8, 32));

			UserEntity u = new UserEntity();
			u.setUuid(UUID.randomUUID());
			u.setFirstName(firstName);
			u.setLastName(lastName);
			u.setUsername(checkConstraints(USERNAME_PATTERN, username, "username"));
			u.setEmail(checkConstraints(EMAIL_PATTERN, email, "email"));
			u.setPassword(checkConstraints(PASSWORD_PATTERN, passwordHash, "password"));
			u.setIsVerified(faker.random().nextInt(100) < 75);

			userRepository.saveAndFlush(u);
			log.info("Created new mock user: {} <{}>", u.getUsername(), u.getEmail());
		}
	}

	private static String slug(String value) {
		String cleaned = ILLEGAL_CHARS.matcher(value.toLowerCase(Locale.ROOT)).replaceAll("");
		return cleaned.isEmpty() ? "user" : cleaned;
	}

	private static String unique(String base, Set<String> used, int maxLen) {
		String candidate = base.length() > maxLen ? base.substring(0, maxLen) : base;
		int n = 1;
		while (!used.add(candidate)) {
			String suffix = String.valueOf(++n);
			candidate = base.substring(0, Math.min(base.length(), maxLen - suffix.length())) + suffix;
		}
		return candidate;
	}

	private static String checkConstraints(Pattern pattern, String value, String field) {
		if (!pattern.matcher(value).matches()) {
			throw new IllegalStateException(
				"Generated '" + field + "' violates DB constraint " + pattern.pattern());
		}
		return value;
	}
}