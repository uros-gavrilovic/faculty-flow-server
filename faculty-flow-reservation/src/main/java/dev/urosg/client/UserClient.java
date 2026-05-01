package dev.urosg.client;

import dev.urosg.context.RequestContext;
import dev.urosg.model.constant.HttpClientConstant;
import dev.urosg.model.dto.Room;
import dev.urosg.model.dto.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class UserClient {

	@Value("${faculty-flow.service.user.url}")
	public String USER_URL;

	private final RequestContext requestContext;
	private final RestClient restClient = RestClient.create();

	public Set<User> getAdmins() {
		return restClient.get()
			.uri(USER_URL + "/admins")
			.header(HttpClientConstant.AUTHORIZATION, HttpClientConstant.BEARER + requestContext.getToken())
			.retrieve()
			.body(new ParameterizedTypeReference<>() {});
	}

	public User getUserByUsername(String username) {
		return restClient.get()
			.uri(USER_URL + "?username=" + username)
			.header(HttpClientConstant.AUTHORIZATION, HttpClientConstant.BEARER + requestContext.getToken())
			.retrieve()
			.body(User.class);
	}
}