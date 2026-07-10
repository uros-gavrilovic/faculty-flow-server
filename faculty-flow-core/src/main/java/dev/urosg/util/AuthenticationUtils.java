package dev.urosg.util;

import dev.urosg.context.RequestContext;
import dev.urosg.model.dto.AuthenticatedUser;
import dev.urosg.model.constant.JwtClaim;
import dev.urosg.model.enumeration.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.List;

public class AuthenticationUtils {

	private static String secret;

	public static void init(String secret) {
		AuthenticationUtils.secret = secret;
	}

	public static AuthenticatedUser getAuthentication(RequestContext requestContext) {
		AuthenticatedUser authenticatedUser = fromToken(requestContext.getToken());
		if (authenticatedUser == null) throw new IllegalStateException("User not authenticated");

		return authenticatedUser;
	}

	public static AuthenticatedUser fromToken(String token) {
		if (token == null) return null;
		Claims claims = parseClaims(token);
		return new AuthenticatedUser(
			claims.getSubject(),
			((List<String>) claims.get(JwtClaim.ROLES.name(), List.class))
				.stream()
				.map(UserRole::valueOf)
				.collect(java.util.stream.Collectors.toSet())
		);
	}

	private static Claims parseClaims(String token) {
		return Jwts.parser()
			.verifyWith(getSigningKey())
			.build()
			.parseSignedClaims(token)
			.getPayload();
	}

	private static SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
	}

	public static boolean isCurrentUserAdmin(RequestContext requestContext) {
		AuthenticatedUser user = getAuthentication(requestContext);
		return user.roles().contains(UserRole.ADMINISTRATOR);
	}

	public static String getCurrentUserUsername(RequestContext requestContext) {
		AuthenticatedUser user = getAuthentication(requestContext);
		return user.username();
	}
}
