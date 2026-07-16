package dev.urosg.service.impl;

import dev.urosg.adapter.UserAdapter;
import dev.urosg.kafka.producer.UserEventProducer;
import dev.urosg.model.dto.*;
import dev.urosg.model.entity.UserEntity;
import dev.urosg.repository.UserRepository;
import dev.urosg.service.AuthService;
import dev.urosg.service.JwtService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
	private final UserEventProducer userEventProducer;
	private final JwtService jwtService;
	private final PasswordEncoder passwordEncoder;

	@Value("${app.public-base-url}")
	private String baseUrl;

	public AuthServiceImpl(
		UserRepository userRepository,
		UserEventProducer userEventProducer,
		JwtService jwtService,
		PasswordEncoder passwordEncoder
	) {
		this.userRepository = userRepository;
		this.userEventProducer = userEventProducer;
		this.jwtService = jwtService;
		this.passwordEncoder = passwordEncoder;
	}

	public LoginResponse login(LoginRequest request) {
		UserEntity userEntity = userRepository.findByUsername(request.username())
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));

		boolean passwordsMatch = passwordEncoder.matches(request.password(), userEntity.getPassword());
		if (!passwordsMatch) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");

		boolean isVerified = userEntity.getIsVerified();
		if (!isVerified) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account not verified");

		log.info("User '{}' logged in successfully", request.username());
		JwtToken jwtToken = jwtService.generateToken(request.username(), userEntity.getRoles());

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
		userEntity.setRoles(request.roles());

		UserEntity savedUserEntity = userRepository.saveAndFlush(userEntity);
		log.info("User '{}' successfully created", savedUserEntity.getUsername());

		userEventProducer.sendVerificationEvent(
			savedUserEntity.getEmail(),
			savedUserEntity.getUsername(),
			baseUrl + "/api/auth/verify-account?token=" + savedUserEntity.getUuid()
		);

		return UserAdapter.toDto(savedUserEntity);
	}

	@Override
	public User verifyAccount(UUID uuid) {
		Optional<UserEntity> existingUserOpt = userRepository.findByUuid(uuid);
		if (existingUserOpt.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

		UserEntity userEntity = existingUserOpt.get();
		if (userEntity.getIsVerified()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account already verified");

		userEntity.setIsVerified(true);
		userRepository.saveAndFlush(userEntity);

		log.info("User '{}' successfully verified their account", userEntity.getUsername());

		return UserAdapter.toDto(userEntity);
	}
}
