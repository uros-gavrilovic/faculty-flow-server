package dev.urosg.exception;

import dev.urosg.model.dto.ApiError;
import dev.urosg.util.ReactiveWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;

@Slf4j
@Order(-2)
@Component
@RequiredArgsConstructor
public class GatewayExceptionHandler implements WebExceptionHandler {

	private static final String SERVICE_UNAVAILABLE_MESSAGE = "The requested service is unavailable";

	private final ReactiveWriter reactiveWriter;

	@Override
	public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
		ServerHttpResponse response = exchange.getResponse();
		if (response.isCommitted() || !isServiceUnreachable(ex)) return Mono.error(ex);

		log.error("Service unreachable for '{}': {}", exchange.getRequest().getURI().getPath(), ex.getMessage());

		HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE;
		response.setStatusCode(status);
		response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

		return reactiveWriter.write(
			exchange,
			new ApiError(status.value(), status.getReasonPhrase(), SERVICE_UNAVAILABLE_MESSAGE)
		);
	}

	private static boolean isServiceUnreachable(Throwable ex) {
		for (Throwable cause = ex; cause != null; cause = cause.getCause()) {
			if (
				cause instanceof ConnectException ||
				cause instanceof NoRouteToHostException ||
				cause instanceof UnknownHostException
			) return true;
		}
		return false;
	}
}
