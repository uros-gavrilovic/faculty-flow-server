package dev.urosg.service;

import reactor.core.publisher.Mono;

public interface AuthForwardService {
	Mono<Boolean> isTokenValid(String token);
}
