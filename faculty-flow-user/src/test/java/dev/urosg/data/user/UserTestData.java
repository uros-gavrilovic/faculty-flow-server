package dev.urosg.data.user;

import dev.urosg.model.entity.UserEntity;
import dev.urosg.model.enumeration.UserRole;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Set;
import java.util.UUID;

public class UserTestData {

	private static final String VALID_HASH = new BCryptPasswordEncoder().encode("secret-password");
	private static final UUID VALID_USER_UUID = UUID.fromString("11111111-1111-1111-1111-111111111111");
	private static final UUID ADMIN_USER_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

	private static UserEntity generateUser() {
		return UserEntity
			.builder()
			.id(1L)
			.uuid(VALID_USER_UUID)
			.username("john.doe")
			.password(VALID_HASH)
			.email("john.doe@example.com")
			.firstName("John")
			.lastName("Doe")
			.roles(Set.of(UserRole.USER))
			.isVerified(true)
			.build();
	}

	public static UserEntity VALID_USER() {
		return generateUser();
	}

	public static UserEntity NEW_USER() {
		return generateUser()
			.toBuilder()
			.id(null)
			.uuid(null)
			.username("new-user")
			.password(VALID_HASH)
			.email("new-user@example.com")
			.firstName("New")
			.lastName("User")
			.roles(Set.of(UserRole.USER))
			.isVerified(false)
			.build();
	}

	public static UserEntity ADMIN_USER() {
		return UserEntity
			.builder()
			.id(0L)
			.uuid(ADMIN_USER_UUID)
			.username("admin")
			.password(VALID_HASH)
			.email("admin@example.com")
			.firstName("Admin")
			.lastName("Admin")
			.roles(Set.of(UserRole.ADMINISTRATOR))
			.isVerified(true)
			.build();
	}
}
