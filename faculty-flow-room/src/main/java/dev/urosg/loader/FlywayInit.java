package dev.urosg.loader;

import jakarta.annotation.PostConstruct;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FlywayInit {

	@Value("${spring.flyway.enabled}")
	private boolean enabled;

	private final Flyway flyway;

	public FlywayInit(Flyway flyway) {
		this.flyway = flyway;
	}

	@PostConstruct
	public void init() {
		if (enabled) flyway.migrate();
	}
}
