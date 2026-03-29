package dev.urosg.util;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

@Component
public class ReactiveWriter {

	private final ObjectMapper objectMapper;

	public ReactiveWriter(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public Mono<Void> write(ServerWebExchange exchange, Object dto) {
		ServerHttpResponse response = exchange.getResponse();

		byte[] bytes = objectMapper.writeValueAsBytes(dto);
		DataBuffer buffer = response.bufferFactory().wrap(bytes);

		return response.writeWith(Mono.just(buffer));
	}
}
