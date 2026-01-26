package dev.urosg.model.entity;

import java.util.UUID;
import java.util.Set;
import dev.urosg.model.enumeration.UserRole;

public class UserEntity {
	private Long id;

	private UUID uuid;

	private String username;

	private String password;

	private String email;

	private String firstName;

	private String lastName;

	private Set<UserRole> roles;
}
