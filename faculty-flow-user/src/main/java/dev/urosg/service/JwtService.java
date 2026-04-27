package dev.urosg.service;

import dev.urosg.model.dto.JwtToken;
import dev.urosg.model.enumeration.UserRole;
import java.util.Set;

public interface JwtService {
	boolean isValidToken(String token);
	JwtToken generateToken(String username, Set<UserRole> roles);
}
