package dev.urosg.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import java.util.List;

@Slf4j
@Configuration
public class CorsConfig {

	@Value("${faculty-flow.service.front-end.url}") private String frontEndUrl;

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		log.info("Configuring CORS with allowed origin '{}'", frontEndUrl);

		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(List.of(frontEndUrl));
		config.setAllowedMethods(List.of("*"));
		config.setAllowedHeaders(List.of("*"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);

		return source;
	}
}