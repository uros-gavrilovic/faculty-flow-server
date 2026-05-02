package dev.urosg.config;

import dev.urosg.util.AuthenticationUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;

public class AuthenticationUtilsConfig {

	@Value("${jwt.secret}")
	private String secret;

	@PostConstruct
	public void init() {
		AuthenticationUtils.init(secret);
	}
}