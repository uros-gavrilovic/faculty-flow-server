package dev.urosg.service;

public interface JwtService {
	boolean isValidToken(String token);
	String generateToken(String username);
}
