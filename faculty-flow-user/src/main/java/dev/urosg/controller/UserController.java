package dev.urosg.controller;

import dev.urosg.annotation.HasRole;
import dev.urosg.model.dto.SearchResponse;
import dev.urosg.model.dto.User;
import dev.urosg.model.enumeration.UserRole;
import dev.urosg.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {

	private final UserService userService;

	@GetMapping
	public User fetchUser(
		@RequestParam(required = false) UUID uuid,
		@RequestParam(required = false) String username
	) {
		if (uuid != null) return userService.fetchUser(uuid);
		if (username != null) return userService.fetchByUsername(username);

		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide UUID or username");
	}

	@GetMapping("/search")
	public SearchResponse<User> searchUsers(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(defaultValue = "lastName") String sortBy,
		@RequestParam(defaultValue = "asc") String direction
	) {
		return userService.searchUsers(page, size, sortBy, direction);
	}

	@PutMapping
	@HasRole(UserRole.ADMINISTRATOR)
	public User updateUser(@RequestBody User user) {
		return userService.updateUser(user);
	}

	@GetMapping( "/admins")
	public Set<User> fetchAdmins() {
		return userService.fetchAdmins();
	}
}
