package dev.urosg.service;

import dev.urosg.model.dto.SearchResponse;
import dev.urosg.model.dto.User;
import java.util.Set;
import java.util.UUID;

public interface UserService {
	User fetchUser(UUID uuid);
	User fetchByUsername(String username);
	User updateUser(User user);
	Set<User> fetchAdmins();
	SearchResponse<User> fetchUsers(int page, int size, String sortBy, String direction);
}
