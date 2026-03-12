package dev.urosg.loader;

import com.github.javafaker.Faker;
import dev.urosg.model.entity.UserEntity;
import dev.urosg.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class MockUserLoader {

	@Value("${dataloader.init-test-data:false}")
	private boolean initTestData;

	private final Faker faker = new Faker();

	@Autowired
	private UserRepository userRepository;

	private static final Logger log = LoggerFactory.getLogger(MockUserLoader.class);

	@PostConstruct
	void loadTestData() {
		if (initTestData) {
			log.warn("Loading test data ...");

			// User 'admin' is automatically created by Flyway
			if (userRepository.count() <= 1) loadUsers(10);
		}
	}

	private void loadUsers(int counter) {
		for (int i = 0; i < counter; i++) {
			UserEntity u = new UserEntity();

			String firstName = faker.name().firstName();
			String lastName = faker.name().lastName();

			u.setUuid(UUID.randomUUID());
			u.setFirstName(firstName);
			u.setLastName(lastName);
			u.setEmail(firstName.toLowerCase() + "." + lastName.toLowerCase() + "@" + faker.internet().domainName());
			u.setUsername(firstName .toLowerCase() + lastName.toLowerCase().charAt(0));
			u.setPassword(new BCryptPasswordEncoder().encode(faker.internet().password(8, 32)));

			log.info("Created new mock user: {}", u);

			userRepository.saveAndFlush(u);
		}
	}

}
