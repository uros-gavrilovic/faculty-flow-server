package dev.urosg.exception;

import dev.urosg.model.dto.ApiError;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLException;
import org.postgresql.util.ServerErrorMessage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import static dev.urosg.model.constant.CoreMessageKey.*;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE) // Must run before GlobalExceptionHandler's catch-all
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@RestControllerAdvice
public class DataExceptionHandler {

	private static final String UNIQUE_VIOLATION = "23505";
	private static final String FOREIGN_KEY_VIOLATION = "23503";
	private static final String NOT_NULL_VIOLATION = "23502";
	private static final String CHECK_VIOLATION = "23514";

	private final MessageSource messageSource;

	public DataExceptionHandler(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ApiError> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
		Locale locale = LocaleContextHolder.getLocale();

		if (!(ex.getMostSpecificCause() instanceof PSQLException psql) || psql.getServerErrorMessage() == null) {
			log.warn("Data integrity violation without PostgreSQL details", ex);
			return response(HttpStatus.CONFLICT, message(GENERIC_KEY, locale));
		}

		ServerErrorMessage error = psql.getServerErrorMessage();
		String constraintName = error.getConstraint();
		log.warn(
			"Data integrity violation: sqlState={}, table={}, constraintName={}, detail={}",
			psql.getSQLState(), error.getTable(), constraintName, error.getDetail()
		);

		String constraintMessage = constraintName != null ?
			messageSource.getMessage(CONSTRAINT_KEY_PREFIX + constraintName, null, null, locale) :
			null;

		return switch (String.valueOf(psql.getSQLState())) {
			case UNIQUE_VIOLATION -> response(HttpStatus.CONFLICT, orDefault(constraintMessage, UNIQUE_KEY, locale));
			case FOREIGN_KEY_VIOLATION -> response(HttpStatus.CONFLICT, orDefault(constraintMessage, FOREIGN_KEY_KEY, locale));
			case CHECK_VIOLATION -> response(HttpStatus.BAD_REQUEST, orDefault(constraintMessage, CHECK_KEY, locale));
			case NOT_NULL_VIOLATION -> response(HttpStatus.BAD_REQUEST, notNullMessage(error.getColumn(), locale));
			default -> response(HttpStatus.CONFLICT, message(GENERIC_KEY, locale));
		};
	}

	private static ResponseEntity<ApiError> response(HttpStatus status, String message) {
		return ResponseEntity
			.status(status)
			.body(new ApiError(status.value(), status.getReasonPhrase(), message));
	}

	private String orDefault(String constraintMessage, String fallbackKey, Locale locale) {
		return constraintMessage != null ? constraintMessage : message(fallbackKey, locale);
	}

	private String message(String key, Locale locale) {
		return messageSource.getMessage(key, null, locale);
	}

	private String notNullMessage(String column, Locale locale) {
		return column != null ?
			messageSource.getMessage(NOT_NULL_KEY, new Object[]{column}, locale) :
			message(NOT_NULL_GENERIC_KEY, locale);
	}
}
