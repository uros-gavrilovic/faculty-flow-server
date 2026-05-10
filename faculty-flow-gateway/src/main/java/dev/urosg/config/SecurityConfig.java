package dev.urosg.config;

import dev.urosg.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.reactive.CorsConfigurationSource;

@Configuration
public class SecurityConfig {

	@Bean
	public SecurityWebFilterChain securityWebFilterChain(
		ServerHttpSecurity http,
		JwtAuthenticationFilter jwtFilter,
		CorsConfigurationSource corsConfigurationSource
	) {
		return http
			.csrf(ServerHttpSecurity.CsrfSpec::disable)
			.cors(cors -> cors.configurationSource(corsConfigurationSource))
			.authorizeExchange(exchanges -> exchanges
				.anyExchange().permitAll()
			)
			.addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
			.build();
	}
}