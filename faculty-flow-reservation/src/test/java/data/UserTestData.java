package data;

import dev.urosg.model.constant.JwtClaim;
import dev.urosg.model.dto.User;
import dev.urosg.model.enumeration.UserRole;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.List;

public class UserTestData {

	// Same value as jwt.secret in application-test.properties
	// Base64 of 'faculty-flow-test-only-jwt-secret-do-not-use-in-prod'
	private static final String SECRET = "ZmFjdWx0eS1mbG93LXRlc3Qtb25seS1qd3Qtc2VjcmV0LWRvLW5vdC11c2UtaW4tcHJvZA==";

	public static User USER() {
		return new User("John", "Doe", "john.doe@faculty.com", "john.doe");
	}

	public static User ADMIN() {
		return new User("Admin", "Admin", "admin@faculty.com", "admin");
	}

	public static String ADMIN_TOKEN() {
		return generateToken(ADMIN().username(), List.of(UserRole.ADMINISTRATOR));
	}

	public static String USER_TOKEN() {
		return generateToken(USER().username(), List.of(UserRole.USER));
	}

	private static String generateToken(String username, List<UserRole> roles) {
		return Jwts.builder()
			.subject(username)
			.claim(JwtClaim.ROLES.name(), roles)
			.issuedAt(new Date())
			.expiration(new Date(System.currentTimeMillis() + 3_600_000))
			.signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET)))
			.compact();
	}
}