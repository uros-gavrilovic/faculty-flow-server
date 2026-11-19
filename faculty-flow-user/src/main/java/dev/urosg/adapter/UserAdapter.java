package dev.urosg.adapter;

import dev.urosg.model.dto.RegisterAccountRequest;
import dev.urosg.model.dto.User;
import dev.urosg.model.entity.UserEntity;

public class UserAdapter {
	public static User toDto(UserEntity entity) {
		return new User(
			entity.getUuid().toString(),
			entity.getFirstName(),
			entity.getLastName(),
			entity.getEmail(),
			entity.getUsername(),
			entity.getIsVerified(),
			entity.getRoles()
		);
	}

	public static UserEntity from(RegisterAccountRequest request) {
		return UserEntity.builder()
			.username(request.username())
			.password(request.password())
			.email(request.email())
			.firstName(request.firstName())
			.lastName(request.lastName())
			.build();
	}
}
