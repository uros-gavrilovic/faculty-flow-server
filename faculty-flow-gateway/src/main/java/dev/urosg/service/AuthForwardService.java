package dev.urosg.service;

import dev.urosg.model.dto.LoginRequest;
import dev.urosg.model.dto.LoginResponse;
import reactor.core.publisher.Mono;

public interface AuthForwardService {
	Mono<Boolean> isTokenValid(String token);
	Mono<LoginResponse> login(LoginRequest request);
}
