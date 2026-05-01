package dev.urosg.controller;

import dev.urosg.model.dto.User;
import dev.urosg.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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

		throw new IllegalArgumentException("Provide uuid or username");
	}

	@PutMapping
	public User updateUser(@RequestBody User user) {
		return userService.updateUser(user);
	}

	@GetMapping( "/admins")
	public Set<User> fetchAdmins() {
		return userService.fetchAdmins();
	}
}
