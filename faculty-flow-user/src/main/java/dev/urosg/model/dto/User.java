package dev.urosg.model.dto;

import dev.urosg.model.enumeration.UserRole;
import java.util.Set;

public record User(
	String uuid,
	String firstName,
	String lastName,
	String email,
	String username,
	Set<UserRole> roles
) {
	public User(String uuid, String firstName, String lastName, String email, String username) {
		this(uuid, firstName, lastName, email, username, null);
	}
}
