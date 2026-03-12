package dev.urosg.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
		http
			.csrf(ServerHttpSecurity.CsrfSpec::disable)
			.authorizeExchange(auth -> auth
				.pathMatchers("/public/**").permitAll()
				.anyExchange().authenticated()
			)
			.httpBasic(basic -> {});
		return http.build();
	}
}
