package dev.urosg.service.impl;

import dev.urosg.model.dto.LoginRequest;
import dev.urosg.model.dto.LoginResponse;
import dev.urosg.model.entity.UserEntity;
import dev.urosg.repository.UserRepository;
import dev.urosg.service.AuthService;
import dev.urosg.service.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthServiceImpl implements AuthService {

	private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

	private final UserRepository userRepository;
	private final JwtService jwtService;
	private final PasswordEncoder passwordEncoder;

	public AuthServiceImpl(
		UserRepository userRepository,
		JwtService jwtService,
		PasswordEncoder passwordEncoder
	) {
		this.userRepository = userRepository;
		this.jwtService = jwtService;
		this.passwordEncoder = passwordEncoder;
	}

	public LoginResponse login(LoginRequest request) {
		UserEntity user = userRepository.findByUsername(request.username())
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));

		boolean passwordsMatch = passwordEncoder.matches(request.password(), user.getPassword());
		if (!passwordsMatch) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");

		log.info("User '{}' logged in successfully", request.username());
		return new LoginResponse(jwtService.generateToken(request.username()));
	}
}
