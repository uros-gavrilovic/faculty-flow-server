package dev.urosg.model.dto;

import java.util.Date;

public record JwtToken(
	String token,
	Date expiration
) {}
