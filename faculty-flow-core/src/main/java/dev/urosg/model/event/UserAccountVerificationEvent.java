package dev.urosg.model.event;

public record UserAccountVerificationEvent(
	String email,
	String username,
	String verificationUrl
) {}