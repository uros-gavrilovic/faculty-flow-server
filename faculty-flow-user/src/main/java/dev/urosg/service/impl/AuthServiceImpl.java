package dev.urosg.service.impl;

import dev.urosg.adapter.UserAdapter;
import dev.urosg.model.dto.*;
import dev.urosg.model.entity.UserEntity;
import dev.urosg.repository.UserRepository;
import dev.urosg.service.AuthService;
import dev.urosg.service.JwtService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.Optional;
import java.util.UUID;

@Transactional
@Service
public class AuthServiceImpl implements AuthService {

	private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

	private final UserRepository userRepository;
	private final JwtService jwtService;
	private final PasswordEncoder passwordEncoder;

	public AuthServiceImpl(
		UserRepository userRepository,
		JwtService jwtService,
		EntityManager entityManager,
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
		JwtToken jwtToken = jwtService.generateToken(request.username());

		return new LoginResponse(
			jwtToken.token(),
			jwtToken.expiration()
		);
	}

	@Override
	public User register(RegisterAccountRequest request) {
		Optional<UserEntity> existingUserOpt = userRepository.findByUsername(request.username());
		if (existingUserOpt.isPresent()) throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");

		UserEntity userEntity = UserAdapter.from(request);
		userEntity.setUuid(UUID.randomUUID());
		userEntity.setPassword(passwordEncoder.encode(request.password()));

		UserEntity savedUserEntity = userRepository.saveAndFlush(userEntity);
		log.info("User '{}' successfully created", savedUserEntity.getUsername());

		// TODO: Registration e-mail logic.

		return UserAdapter.toDto(savedUserEntity);
	}
}
