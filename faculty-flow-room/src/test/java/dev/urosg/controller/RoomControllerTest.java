package dev.urosg.controller;

import dev.urosg.config.JacksonConfig;
import dev.urosg.context.RequestContext;
import dev.urosg.data.RoomTestData;
import dev.urosg.service.RoomService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@Import(JacksonConfig.class) // ObjectMapper used by the security error handler
@WebMvcTest(RoomController.class)
@ActiveProfiles("test")
public class RoomControllerTest {

	@Autowired MockMvc mockMvc;
	@Autowired JsonMapper jsonMapper;

	@MockitoBean RoomService roomService;
	@MockitoBean RequestContext requestContext; // Needed for core's TokenInterceptor class

	@Nested
	class InsufficientPrivileges {

		@Test
		void requiresLogIn() throws Exception {
			mockMvc.perform(get("/api/room").param("uuid", RoomTestData.CLASSROOM().uuid().toString()))
				.andExpect(status().isUnauthorized());

			verifyNoInteractions(roomService);
		}

		@Test
		@WithMockUser(username = "john.doe", roles = "USER")
		void forbidsUpdateForRegularUser() throws Exception {
			mockMvc.perform(put("/api/room")
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonMapper.writeValueAsString(RoomTestData.CLASSROOM())))
				.andExpect(status().isForbidden());

			verifyNoInteractions(roomService);
		}

		@Test
		@WithMockUser(username = "john.doe", roles = "USER")
		void forbidsDeleteForRegularUser() throws Exception {
			mockMvc.perform(delete("/api/room/{uuid}", RoomTestData.CLASSROOM_UUID))
				.andExpect(status().isForbidden());

			verifyNoInteractions(roomService);
		}
	}

	@Nested
	class SufficientPrivileges {
		@Test
		@WithMockUser(username = "admin", roles = "ADMINISTRATOR")
		void allowsUpdateForAdministrator() throws Exception {
			when(roomService.updateRoom(any())).thenReturn(RoomTestData.CLASSROOM());

			mockMvc.perform(put("/api/room")
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonMapper.writeValueAsString(RoomTestData.CLASSROOM())))
				.andDo(response -> log.debug(response.getResponse().getContentAsString()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(RoomTestData.CLASSROOM().code()));

			verify(roomService).updateRoom(any());
		}

		@Test
		@WithMockUser(username = "admin", roles = "ADMINISTRATOR")
		void allowsDeleteForAdministrator() throws Exception {
			mockMvc.perform(delete("/api/room/{uuid}", RoomTestData.CLASSROOM_UUID))
				.andDo(response -> log.debug(response.getResponse().getContentAsString()))
				.andExpect(status().isNoContent());

			verify(roomService).deleteRoom(RoomTestData.CLASSROOM_UUID);
		}
	}
}
