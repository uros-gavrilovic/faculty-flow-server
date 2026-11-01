package dev.urosg.exception;

import dev.urosg.model.dto.ApiError;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	private static final String DOWNSTREAM_REJECTED_MESSAGE = "Request rejected by a downstream service";
	private static final String DOWNSTREAM_FAILED_MESSAGE = "A downstream service failed to process the request";
	private static final String DOWNSTREAM_UNAVAILABLE_MESSAGE = "A downstream service is unavailable";
	private static final String INVALID_TOKEN_MESSAGE = "Invalid or expired token";
	private static final String UNEXPECTED_ERROR_MESSAGE = "An unexpected error occurred";

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(
		Exception ex,
		Object body,
		HttpHeaders headers,
		HttpStatusCode statusCode,
		WebRequest request
	) {
		String detail = ex instanceof ErrorResponse errorResponse ? errorResponse.getBody().getDetail() : null;
		return super.handleExceptionInternal(ex, apiError(statusCode, detail), headers, statusCode, request);
	}

	@ExceptionHandler(RestClientResponseException.class)
	public ResponseEntity<ApiError> handleDownstreamResponse(RestClientResponseException ex) {
		if (ex.getStatusCode().is4xxClientError()) {
			log.warn("Downstream service responded with {}", ex.getStatusCode());
			return response(ex.getStatusCode(), downstreamMessage(ex));
		}

		log.error("Downstream service responded with {}", ex.getStatusCode(), ex);
		return response(HttpStatus.BAD_GATEWAY, DOWNSTREAM_FAILED_MESSAGE);
	}

	@ExceptionHandler(ResourceAccessException.class)
	public ResponseEntity<ApiError> handleDownstreamUnavailable(ResourceAccessException ex) {
		log.error("Downstream service is unreachable", ex);
		return response(HttpStatus.SERVICE_UNAVAILABLE, DOWNSTREAM_UNAVAILABLE_MESSAGE);
	}

	@ExceptionHandler(JwtException.class)
	public ResponseEntity<ApiError> handleInvalidToken(JwtException ex) {
		log.warn("Rejected invalid token: {}", ex.getMessage());
		return response(HttpStatus.UNAUTHORIZED, INVALID_TOKEN_MESSAGE);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> handleUnexpected(Exception ex) {
		log.error("Unhandled exception", ex);
		return response(HttpStatus.INTERNAL_SERVER_ERROR, UNEXPECTED_ERROR_MESSAGE);
	}

	private static String downstreamMessage(RestClientResponseException ex) {
		try {
			ApiError downstreamError = ex.getResponseBodyAs(ApiError.class);
			if (downstreamError != null && downstreamError.message() != null && !downstreamError.message().isBlank()) {
				return downstreamError.message();
			}
		} catch (RuntimeException parseFailure) {
			log.debug("Could not parse downstream error body", parseFailure);
		}
		return DOWNSTREAM_REJECTED_MESSAGE;
	}

	private static ResponseEntity<ApiError> response(HttpStatusCode statusCode, String message) {
		return ResponseEntity.status(statusCode).body(apiError(statusCode, message));
	}

	private static ApiError apiError(HttpStatusCode statusCode, String message) {
		HttpStatus status = HttpStatus.resolve(statusCode.value());
		String reason = status != null ? status.getReasonPhrase() : String.valueOf(statusCode.value());
		return new ApiError(statusCode.value(), reason, message != null ? message : reason);
	}
}
