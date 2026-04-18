package dev.urosg.kafka.event;

public record UserAccountVerificationRequestedEvent(
	String email,
	String username,
	String verificationUrl
) {}