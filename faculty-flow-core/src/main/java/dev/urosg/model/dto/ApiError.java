package dev.urosg.model.dto;

public record ApiError(
	int status,
	String error,
	String message
) {}