package dev.urosg.service.impl;

import dev.urosg.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtServiceImpl implements JwtService {

	private static final Logger log = LoggerFactory.getLogger(JwtServiceImpl.class);

	private static final String BEARER_TOKEN_PREFIX = "Bearer ";

	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.expiration}")
	private long expiration;

	@Override
	public boolean isValidToken(String token) {
		return getIsValidToken(
			token.replace(BEARER_TOKEN_PREFIX, "").trim()
		);
	}

	@Override
	public String generateToken(String username) {
		return Jwts.builder()
			.subject(username)
			.issuedAt(new Date())
			.expiration(new Date(System.currentTimeMillis() + expiration))
			.signWith(getSigningKey())
			.compact();
	}

	private boolean getIsValidToken(String token) {
		try {
			Jwts.parser()
				.verifyWith(getSigningKey())
				.build()
				.parseSignedClaims(token);

			return true;
		} catch (ExpiredJwtException e) {
			log.warn("Token '{}' is expired: {}", token, e.getMessage());
		} catch (MalformedJwtException e) {
			log.warn("Token '{}' is malformed: {}", token, e.getMessage());
		} catch (JwtException e) {
			log.warn("Token '{}' is invalid: {}", token, e.getMessage());
		}
		return false;
	}

	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
	}
}