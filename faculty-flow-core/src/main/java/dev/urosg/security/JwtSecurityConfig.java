package dev.urosg.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.annotation.AnnotationTemplateExpressionDefaults;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import java.util.stream.Stream;

/**
 * Deliberately not annotated with {@code @Configuration}: core's component scan would load it in every
 * service, including those without Spring Security. Services opt in with {@code @EnableJwtSecurity}.
 */
@EnableWebSecurity
@EnableMethodSecurity
public class JwtSecurityConfig {

	private static final String[] ALWAYS_PUBLIC_PATHS = {"/error", "/actuator/health"};

	@Value("${faculty-flow.security.public-paths:}")
	private String[] publicPaths;

	/**
	 * Lets annotations such as {@code @HasRole} fill their values into a {@code @PreAuthorize}
	 * expression. Static so it is available before Spring sets up method security.
	 */
	@Bean
	public static AnnotationTemplateExpressionDefaults annotationTemplateExpressionDefaults() {
		return new AnnotationTemplateExpressionDefaults();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return new InMemoryUserDetailsManager();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, ObjectMapper objectMapper) {

		String[] allowedPaths = Stream.concat(Stream.of(ALWAYS_PUBLIC_PATHS), Stream.of(publicPaths))
			.toArray(String[]::new);

		return http
			.csrf(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			.logout(AbstractHttpConfigurer::disable)
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(requests -> requests
				.requestMatchers(allowedPaths).permitAll()
				.anyRequest().authenticated()
			)
			.exceptionHandling(exceptions ->
				exceptions.authenticationEntryPoint(new ApiErrorAuthenticationEntryPoint(objectMapper))) // Custom authentication error message format
			.addFilterBefore(new JwtTokenFilter(), UsernamePasswordAuthenticationFilter.class)
			.build();
	}
}
