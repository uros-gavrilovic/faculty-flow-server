package dev.urosg.util;

import dev.urosg.context.RequestContext;
import dev.urosg.model.dto.AuthenticatedUser;
import dev.urosg.model.constant.JwtClaim;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Set;

public class AuthenticationUtils {

	private static String secret;

	public static void init(String secret) {
		AuthenticationUtils.secret = secret;
	}

	public static String getFullyAuthenticatedUser(RequestContext requestContext) {
		AuthenticatedUser user = fromToken(requestContext.getToken());
		return user != null ? user.username() : null;
	}

	public static AuthenticatedUser getAuthentication(RequestContext requestContext) {
		return fromToken(requestContext.getToken());
	}

	public static AuthenticatedUser fromToken(String token) {
		if (token == null) return null;
		Claims claims = parseClaims(token);
		return new AuthenticatedUser(
			claims.getSubject(),
			Set.copyOf(claims.get(JwtClaim.ROLES.name(), java.util.List.class))
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
}