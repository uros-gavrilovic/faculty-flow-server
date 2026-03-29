package dev.urosg.service;

import dev.urosg.model.dto.LoginRequest;
import dev.urosg.model.dto.LoginResponse;

public interface AuthService {
	LoginResponse login(LoginRequest request);
}
