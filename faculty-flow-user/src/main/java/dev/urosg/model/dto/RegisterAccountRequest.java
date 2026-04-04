package dev.urosg.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterAccountRequest(
	@NotBlank String username,
	@NotBlank String password,
	@NotBlank String firstName,
	@NotBlank String lastName,
	@Email String email
) { }
