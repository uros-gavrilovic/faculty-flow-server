package dev.urosg.model.dto;

import dev.urosg.model.enumeration.UserRole;
import java.util.Set;

public record AuthenticatedUser(
	String username,
	Set<UserRole> roles
) {}