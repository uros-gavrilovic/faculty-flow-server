package dev.urosg.service;

import dev.urosg.model.dto.JwtToken;

public interface JwtService {
	boolean isValidToken(String token);
	JwtToken generateToken(String username);
}
