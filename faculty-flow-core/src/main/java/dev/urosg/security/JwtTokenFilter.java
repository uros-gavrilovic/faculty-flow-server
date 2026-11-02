package dev.urosg.security;

import dev.urosg.model.dto.AuthenticatedUser;
import dev.urosg.util.AuthenticationUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

/**
 * Turns the request's bearer token into the Spring Security {@code Authentication} that
 * {@code @PreAuthorize} checks.
 */
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

	private static final String BEARER_PREFIX = "Bearer ";
	private static final String ROLE_PREFIX = "ROLE_";

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {

		String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

		if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
			// No token - let the authorization rules decide whether that's allowed
			filterChain.doFilter(request, response);
			return;
		}

		AuthenticatedUser user;
		try {
			user = AuthenticationUtils.fromToken(authHeader.substring(BEARER_PREFIX.length()));
		} catch (RuntimeException ex) {
			// Stay anonymous so a stale token can't block public paths; protected paths still return 401
			log.warn("Ignoring invalid token: {}", ex.getMessage());
			filterChain.doFilter(request, response);
			return;
		}

		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(toAuthentication(user));

		SecurityContextHolder.setContext(context);

		filterChain.doFilter(request, response);
	}

	private static UsernamePasswordAuthenticationToken toAuthentication(AuthenticatedUser user) {
		List<SimpleGrantedAuthority> authorities = user.roles().stream()
			.map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role.name()))
			.toList();

		return UsernamePasswordAuthenticationToken.authenticated(
			user.username(),
			null,
			authorities
		);
	}
}
