package dev.urosg.exception;

import dev.urosg.model.dto.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Separate from {@link GlobalExceptionHandler} because Spring Security is optional in core.
 * */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE) //The high precedence stops that handler's catch-all from turning a denied request into a 500.
@ConditionalOnClass(AccessDeniedException.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@RestControllerAdvice
public class SecurityExceptionHandler {

	private static final String ACCESS_DENIED_MESSAGE = "You do not have permission to perform this action";

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex) {
		log.warn("Access denied: {}", ex.getMessage());

		HttpStatus status = HttpStatus.FORBIDDEN;
		return ResponseEntity
			.status(status)
			.body(new ApiError(status.value(), status.getReasonPhrase(), ACCESS_DENIED_MESSAGE));
	}
}
