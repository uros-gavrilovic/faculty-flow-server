package dev.urosg.service.impl;

import dev.urosg.adapter.SearchResponseAdapter;
import dev.urosg.adapter.UserAdapter;
import dev.urosg.model.dto.SearchResponse;
import dev.urosg.model.dto.User;
import dev.urosg.model.entity.UserEntity;
import dev.urosg.model.enumeration.UserRole;
import dev.urosg.repository.UserRepository;
import dev.urosg.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	@Override
	public User fetchUser(UUID uuid) {
		return UserAdapter.toDto(findByUUID(uuid));
	}

	@Override
	public User fetchByUsername(String username) {
		return userRepository.findByUsername(username)
			.map(UserAdapter::toDto)
			.orElseThrow(() -> new IllegalArgumentException("User with username '" + username + "' not found"));
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

	@Override
	public Set<User> fetchAdmins() {
		return userRepository.findByRolesContaining(UserRole.ADMINISTRATOR).stream()
			.map(UserAdapter::toDto)
			.collect(java.util.stream.Collectors.toSet());
	}

	public SearchResponse<User> searchUsers(int page, int size, String sortBy, String direction) {
		Sort sort = direction.equalsIgnoreCase("desc") ?
			Sort.by(sortBy).descending() :
			Sort.by(sortBy).ascending();

		Pageable pageable = PageRequest.of(page, size, sort);

		return SearchResponseAdapter.from(
			userRepository.findAll(pageable),
			UserAdapter::toDto
		);
	}

	private UserEntity findByUUID(String uuid) {
		return findByUUID(UUID.fromString(uuid));
	}

	private UserEntity findByUUID(UUID uuid) {
		return userRepository.findByUuid(uuid)
			.orElseThrow(() -> new IllegalArgumentException("User with UUID '" + uuid + "' not found"));
	}
}
