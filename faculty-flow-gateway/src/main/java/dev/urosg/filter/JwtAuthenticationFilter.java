package dev.urosg.filter;

import dev.urosg.model.dto.ApiError;
import dev.urosg.service.AuthForwardService;
import dev.urosg.util.ReactiveWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import java.util.List;

@Component
public class JwtAuthenticationFilter implements WebFilter {

	private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

	private static final List<String> PUBLIC_PATHS = List.of("/api/user/login", "/api/user/register");

	private final AuthForwardService authForwardService;
	private final ReactiveWriter reactiveWriter;

	public JwtAuthenticationFilter(
		AuthForwardService authForwardService,
		ReactiveWriter reactiveWriter
	) {
		this.authForwardService = authForwardService;
		this.reactiveWriter = reactiveWriter;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		String path = exchange.getRequest().getURI().getPath();

		if (PUBLIC_PATHS.stream().anyMatch(path::startsWith)) return chain.filter(exchange);

		String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			log.warn("Rejected request to '{}' - missing or malformed Authorization header", path);
			return rejectWith(exchange, HttpStatus.UNAUTHORIZED, "Missing or invalid authorization token");
		}

		return authForwardService.isTokenValid(authHeader)
			.flatMap(isValid -> {
				if (!isValid) {
					log.warn("Rejected request to '{}' - invalid or expired token", path);
					return rejectWith(exchange, HttpStatus.UNAUTHORIZED, "Invalid or expired token");
				}
				return chain.filter(exchange);
			});
	}

	private Mono<Void> rejectWith(ServerWebExchange exchange, HttpStatus status, String message) {
		ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(status);
		response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

		return reactiveWriter.write(
			exchange,
			new ApiError(status.value(), status.getReasonPhrase(), message)
		);
	}
}