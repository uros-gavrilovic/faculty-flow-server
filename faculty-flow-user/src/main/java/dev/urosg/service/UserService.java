package dev.urosg.service;

import dev.urosg.model.dto.User;
import java.util.UUID;

public interface UserService {
	User fetchUser(UUID uuid);
	User updateUser(User user);
}
