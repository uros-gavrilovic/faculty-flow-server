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

@Component
public class MockInit {

	@Value("${dataloader.init-test-data:false}")
	private boolean initTestData;

	@Autowired
	private Faker faker;

	@Autowired
	private UserRepository userRepository;

	private static final Logger log = LoggerFactory.getLogger(MockInit.class);

	@PostConstruct
	void loadTestData() {
		if (initTestData) {
			log.warn("Loading test data ...");
			loadUsers(5);
		}
	}

	private void loadUsers(int counter) {
		for (int i = 0; i < counter; i++) {
			UserEntity u = new UserEntity();

			String firstName = faker.name().firstName();
			String lastName = faker.name().lastName();

			u.setFirstName(firstName);
			u.setLastName(lastName);
			u.setEmail(firstName.toLowerCase() + "." + lastName.toLowerCase() + "@" + faker.internet().domainName());
			u.setUsername(firstName .toLowerCase() + "." + lastName.toLowerCase());
			u.setPassword(new BCryptPasswordEncoder().encode(faker.internet().password(8, 32)));

			userRepository.saveAndFlush(u);
		}
	}

}
