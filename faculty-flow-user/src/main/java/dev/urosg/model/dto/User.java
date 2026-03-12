package dev.urosg.model.dto;

public record User(
	String uuid,
	String firstName,
	String lastName,
	String email,
	String username
) {}
