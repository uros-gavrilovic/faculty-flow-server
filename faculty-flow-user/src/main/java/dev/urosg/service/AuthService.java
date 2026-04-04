package dev.urosg.service;

import dev.urosg.model.dto.LoginRequest;
import dev.urosg.model.dto.LoginResponse;
import dev.urosg.model.dto.RegisterAccountRequest;
import dev.urosg.model.dto.User;

public interface AuthService {
	LoginResponse login(LoginRequest request);
	User register(RegisterAccountRequest request);
}
