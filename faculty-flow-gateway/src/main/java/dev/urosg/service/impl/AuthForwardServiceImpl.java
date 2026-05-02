package dev.urosg.service.impl;

import dev.urosg.service.AuthForwardService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class AuthForwardServiceImpl implements AuthForwardService {

	private final WebClient webClient;

	@Value("${faculty-flow.service.user.url}")
	public String USER_URL;

	public AuthForwardServiceImpl(WebClient webClient) {
		this.webClient = webClient;
	}

	public Mono<Boolean> isTokenValid(String token) {
		return webClient.post()
			.uri(USER_URL + "/api/auth/validate")
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
			.retrieve()
			.toBodilessEntity()
			.map(response -> response.getStatusCode().is2xxSuccessful())
			.onErrorResume(e -> Mono.just(false));
	}
}