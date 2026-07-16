package dev.urosg.model.dto;

import dev.urosg.model.enumeration.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.Set;

public record RegisterAccountRequest(
	@NotBlank String username,
	@NotBlank String password,
	@NotBlank String firstName,
	@NotBlank String lastName,
	@Email String email,
	Set<UserRole> roles
) { }
