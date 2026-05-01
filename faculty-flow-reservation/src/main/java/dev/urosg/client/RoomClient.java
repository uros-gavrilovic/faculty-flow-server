package dev.urosg.client;

import dev.urosg.context.RequestContext;
import dev.urosg.model.constant.HttpClientConstant;
import dev.urosg.model.dto.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RoomClient {

	@Value("${faculty-flow.service.room.url}")
	public String ROOM_URL;

	private final RequestContext requestContext;
	private final RestClient restClient = RestClient.create();

	public Set<Room> getAllRooms() {
		return restClient.get()
			.uri(ROOM_URL)
			.header(HttpClientConstant.AUTHORIZATION, HttpClientConstant.BEARER + requestContext.getToken())
			.retrieve()
			.body(new ParameterizedTypeReference<>() {});
	}
}