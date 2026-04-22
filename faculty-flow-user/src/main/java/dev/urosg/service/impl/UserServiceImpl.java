package dev.urosg.service.impl;

import dev.urosg.adapter.UserAdapter;
import dev.urosg.model.dto.User;
import dev.urosg.model.entity.UserEntity;
import dev.urosg.repository.UserRepository;
import dev.urosg.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	@Override
	public User fetchUser(UUID uuid) {
		UserEntity userEntity = findByUUID(uuid);
		return UserAdapter.toDto(userEntity);
	}

	@Override
	public User updateUser(User user) {
		UserEntity userEntity = findByUUID(user.uuid());

		userEntity.setFirstName(user.firstName());
		userEntity.setLastName(user.lastName());
		userEntity.setEmail(user.email());
		userEntity.setRoles(user.roles());

		UserEntity updatedUserEntity = userRepository.saveAndFlush(userEntity);
		return UserAdapter.toDto(updatedUserEntity);
	}

	private UserEntity findByUUID(String uuid) {
		return findByUUID(UUID.fromString(uuid));
	}

	private UserEntity findByUUID(UUID uuid) {
		return userRepository.findByUuid(uuid)
			.orElseThrow(() -> new IllegalArgumentException("User with UUID '" + uuid + "' not found"));
	}
}
