package dev.urosg.controller;

import dev.urosg.model.dto.LoginRequest;
import dev.urosg.model.dto.LoginResponse;
import dev.urosg.service.AuthService;
import dev.urosg.service.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class AuthController {

	private final AuthService authService;
	private final JwtService jwtService;

	public AuthController(
		AuthService authService,
		JwtService jwtService
	) {
		this.authService = authService;
		this.jwtService = jwtService;
	}

	@PostMapping("/validate")
	public ResponseEntity<Void> validate(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {

		boolean isValidBearerToken = jwtService.isValidToken(authHeader);
		if (!isValidBearerToken) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		return ResponseEntity.ok().build();
	}

	@PostMapping("/login")
	public LoginResponse login(@RequestBody LoginRequest request) {
		return authService.login(request);
	}
}