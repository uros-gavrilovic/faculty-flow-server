package dev.urosg.controller;

import dev.urosg.model.dto.User;
import dev.urosg.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {

	private final UserService userService;

	@GetMapping("/{uuid}")
	public User fetchUser(@PathVariable UUID uuid) {
		return userService.fetchUser(uuid);
	}

	@PutMapping
	public User updateUser(@RequestBody User user) {
		return userService.updateUser(user);
	}
}
