package dev.urosg.data.user;

import dev.urosg.adapter.UserAdapter;
import dev.urosg.model.dto.User;
import dev.urosg.model.entity.UserEntity;
import dev.urosg.model.enumeration.UserRole;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Set;
import java.util.UUID;

public final class UserTestData {

	public static final String PASSWORD = "secret-password";
	public static final UUID USER_UUID = UUID.fromString("11111111-1111-1111-1111-111111111111");
	public static final UUID ADMIN_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

	private static final String PASSWORD_HASH = new BCryptPasswordEncoder().encode(PASSWORD);

	/** A persisted, verified regular user. */
	public static UserEntity USER_ENTITY() {
		return UserEntity.builder()
			.id(1L)
			.uuid(USER_UUID)
			.username("john.doe")
			.password(PASSWORD_HASH)
			.email("john.doe@example.com")
			.firstName("John")
			.lastName("Doe")
			.roles(Set.of(UserRole.USER))
			.isVerified(true)
			.build();
	}

	public static UserEntity ADMIN_ENTITY() {
		return USER_ENTITY().toBuilder()
			.id(2L)
			.uuid(ADMIN_UUID)
			.username("admin")
			.email("admin@example.com")
			.firstName("Admin")
			.lastName("Admin")
			.roles(Set.of(UserRole.ADMINISTRATOR))
			.build();
	}

	public static UserEntity NEW_USER_ENTITY() {
		return USER_ENTITY().toBuilder()
			.id(null)
			.uuid(null)
			.username("new.user")
			.email("new.user@example.com")
			.firstName("New")
			.lastName("User")
			.isVerified(false)
			.build();
	}

	public static User USER() {
		return UserAdapter.toDto(USER_ENTITY());
	}

	public static User ADMIN() {
		return UserAdapter.toDto(ADMIN_ENTITY());
	}
}
