package dev.urosg.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.urosg.model.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import java.io.IOException;

/**
 * Writes 401 responses. Security filters run before Spring MVC, so these errors never reach
 * {@code GlobalExceptionHandler}.
 */
@RequiredArgsConstructor
public class ApiErrorAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private static final String MESSAGE = "Missing, invalid or expired token";

	private final ObjectMapper objectMapper;

	@Override
	public void commence(
		HttpServletRequest request,
		HttpServletResponse response,
		AuthenticationException authException
	) throws IOException {
		HttpStatus status = HttpStatus.UNAUTHORIZED;

		response.setStatus(status.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		objectMapper.writeValue(
			response.getOutputStream(),
			new ApiError(status.value(), status.getReasonPhrase(), MESSAGE)
		);
	}
}
