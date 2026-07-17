package dev.urosg.model.dto;

import dev.urosg.model.enumeration.UserRole;
import java.util.Set;

public record User(
	String uuid,
	String firstName,
	String lastName,
	String email,
	String username,
	boolean isVerified,

	Set<UserRole> roles
) {
	public User(String uuid, String firstName, String lastName, String email, String username, boolean isVerified) {
		this(uuid, firstName, lastName, email, username, isVerified, null);
	}
}
