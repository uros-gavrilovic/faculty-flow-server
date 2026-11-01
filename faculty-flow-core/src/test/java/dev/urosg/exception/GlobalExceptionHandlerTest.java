package dev.urosg.exception;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.jsonwebtoken.MalformedJwtException;
import java.io.IOException;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

class GlobalExceptionHandlerTest {

	private static final String DOWNSTREAM_URL = "http://downstream/resource";

	private MockMvc mockMvc;
	private MockRestServiceServer downstream;

	@BeforeEach
	void setUp() {
		RestClient.Builder builder = RestClient.builder();
		downstream = MockRestServiceServer.bindTo(builder).build();

		mockMvc = MockMvcBuilders
			.standaloneSetup(new TestController(builder.build()))
			.setControllerAdvice(new GlobalExceptionHandler())
			.build();
	}

	@Test
	void responseStatusExceptionKeepsStatusAndMessage() throws Exception {
		mockMvc.perform(get("/not-found"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.status").value(404))
			.andExpect(jsonPath("$.error").value("Not Found"))
			.andExpect(jsonPath("$.message").value("Room not found"));
	}

	@Test
	void responseStatusExceptionWithoutReasonFallsBackToReasonPhrase() throws Exception {
		mockMvc.perform(get("/no-reason"))
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.message").value("Conflict"));
	}

	@Test
	void invalidParameterStaysBadRequest() throws Exception {
		mockMvc.perform(get("/typed").param("uuid", "not-a-uuid"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value(400));
	}

	@Test
	void unsupportedMethodStaysMethodNotAllowed() throws Exception {
		mockMvc.perform(post("/not-found"))
			.andExpect(status().isMethodNotAllowed())
			.andExpect(jsonPath("$.status").value(405));
	}

	@Test
	void downstreamClientErrorKeepsStatusAndMessage() throws Exception {
		downstream.expect(requestTo(DOWNSTREAM_URL)).andRespond(
			withStatus(HttpStatus.NOT_FOUND)
				.contentType(MediaType.APPLICATION_JSON)
				.body("{\"status\":404,\"error\":\"Not Found\",\"message\":\"User not found\"}")
		);

		mockMvc.perform(get("/downstream"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("User not found"));
	}

	@Test
	void downstreamClientErrorWithUnreadableBodyUsesGenericMessage() throws Exception {
		downstream.expect(requestTo(DOWNSTREAM_URL)).andRespond(
			withStatus(HttpStatus.CONFLICT)
				.contentType(MediaType.TEXT_PLAIN)
				.body("not json")
		);

		mockMvc.perform(get("/downstream"))
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.message").value("Request rejected by a downstream service"));
	}

	@Test
	void downstreamServerErrorBecomesBadGateway() throws Exception {
		downstream.expect(requestTo(DOWNSTREAM_URL)).andRespond(
			withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
				.contentType(MediaType.APPLICATION_JSON)
				.body("{\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"secret detail\"}")
		);

		mockMvc.perform(get("/downstream"))
			.andExpect(status().isBadGateway())
			.andExpect(jsonPath("$.status").value(502))
			.andExpect(jsonPath("$.message").value("A downstream service failed to process the request"));
	}

	@Test
	void unreachableDownstreamBecomesServiceUnavailable() throws Exception {
		mockMvc.perform(get("/unreachable"))
			.andExpect(status().isServiceUnavailable())
			.andExpect(jsonPath("$.status").value(503));
	}

	@Test
	void invalidTokenBecomesUnauthorized() throws Exception {
		mockMvc.perform(get("/invalid-token"))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.message").value("Invalid or expired token"));
	}

	@Test
	void unexpectedExceptionBecomesGenericServerError() throws Exception {
		mockMvc.perform(get("/unexpected"))
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.status").value(500))
			.andExpect(jsonPath("$.message").value(containsString("unexpected")));
	}

	@RestController
	static class TestController {

		private final RestClient restClient;

		TestController(RestClient restClient) {
			this.restClient = restClient;
		}

		@GetMapping("/not-found")
		void notFound() {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found");
		}

		@GetMapping("/no-reason")
		void noReason() {
			throw new ResponseStatusException(HttpStatus.CONFLICT);
		}

		@GetMapping("/typed")
		void typed(@RequestParam UUID uuid) {
		}

		@GetMapping("/downstream")
		String downstream() {
			return restClient.get().uri(DOWNSTREAM_URL).retrieve().body(String.class);
		}

		@GetMapping("/unreachable")
		void unreachable() {
			throw new ResourceAccessException("Connection refused", new IOException("Connection refused"));
		}

		@GetMapping("/invalid-token")
		void invalidToken() {
			throw new MalformedJwtException("Malformed token");
		}

		@GetMapping("/unexpected")
		void unexpected() {
			throw new IllegalArgumentException("internal detail");
		}
	}
}
