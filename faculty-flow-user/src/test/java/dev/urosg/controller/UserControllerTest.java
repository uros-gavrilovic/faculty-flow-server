package dev.urosg.controller;

import dev.urosg.config.JacksonConfig;
import dev.urosg.context.RequestContext;
import dev.urosg.data.user.UserTestData;
import dev.urosg.model.dto.SearchResponse;
import dev.urosg.model.dto.User;
import dev.urosg.service.UserService;
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
import java.util.Set;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(UserController.class)
@Import(JacksonConfig.class) // ObjectMapper used by the security error handler
@ActiveProfiles("test")
class UserControllerTest {

	@Autowired MockMvc mockMvc;
	@Autowired JsonMapper jsonMapper;

	@MockitoBean UserService userService;
	@MockitoBean RequestContext requestContext; // Needed for core's TokenInterceptor class

	@Nested
	@WithMockUser(username = "john.doe", roles = "USER")
	class FetchUser {

		@Test
		void fetchesByUuid() throws Exception {
			when(userService.fetchUser(UserTestData.USER_UUID)).thenReturn(UserTestData.USER());

			mockMvc.perform(get("/api/user").param("uuid", UserTestData.USER_UUID.toString()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.uuid").value(UserTestData.USER_UUID.toString()))
				.andExpect(jsonPath("$.username").value("john.doe"));

			verify(userService).fetchUser(UserTestData.USER_UUID);
			verify(userService, never()).fetchByUsername(anyString());
		}

		@Test
		void fetchesByUsername() throws Exception {
			when(userService.fetchByUsername("john.doe")).thenReturn(UserTestData.USER());

			mockMvc.perform(get("/api/user").param("username", "john.doe"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.email").value("john.doe@example.com"));

			verify(userService).fetchByUsername("john.doe");
			verify(userService, never()).fetchUser(any());
		}


		@Test
		void rejectsRequestWithoutUuidOrUsername() throws Exception {
			mockMvc.perform(get("/api/user"))
				.andExpect(status().isBadRequest());

			verifyNoInteractions(userService);
		}

		@Test
		void rejectsMalformedUuid() throws Exception {
			mockMvc.perform(get("/api/user").param("uuid", "not-a-uuid"))
				.andExpect(status().isBadRequest());

			verifyNoInteractions(userService);
		}
	}

	@Nested
	@WithMockUser(username = "john.doe", roles = "USER")
	class SearchUsers {

		@Test
		void passesSearchParametersToService() throws Exception {
			when(userService.searchUsers(2, 25, "email", "desc"))
				.thenReturn(new SearchResponse<>(Set.of(), 0, 2, 25));

			mockMvc
				.perform(
					get("/api/user/search")
					.param("page", "2")
					.param("size", "25")
					.param("sortBy", "email")
					.param("direction", "desc")
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.page").value(2))
				.andExpect(jsonPath("$.size").value(25));

			verify(userService).searchUsers(2, 25, "email", "desc");
		}
	}

	@Nested
	class Security {

		@Test
		void rejectsFetchWithoutLoggedInUser() throws Exception {
			mockMvc.perform(get("/api/user").param("uuid", UserTestData.USER_UUID.toString()))
				.andExpect(status().isUnauthorized());

			verifyNoInteractions(userService);
		}

		@Test
		void rejectsUpdateWithoutLoggedInUser() throws Exception {
			mockMvc.perform(put("/api/user")
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonMapper.writeValueAsString(UserTestData.USER())))
				.andDo(result -> log.debug(result.getResponse().getContentAsString()))
				.andExpect(status().isUnauthorized());

			verifyNoInteractions(userService);
		}

		@Test
		@WithMockUser(username = "john.doe", roles = "USER")
		void forbidsUpdateForRegularUser() throws Exception {
			mockMvc.perform(put("/api/user")
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonMapper.writeValueAsString(UserTestData.USER())))
				.andDo(result -> log.debug(result.getResponse().getContentAsString()))
				.andExpect(status().isForbidden());

			verify(userService, never()).updateUser(any());
		}

		@Test
		@WithMockUser(username = "admin", roles = "ADMINISTRATOR")
		void allowsUpdateForAdministrator() throws Exception {
			when(userService.updateUser(any(User.class))).thenReturn(UserTestData.USER());

			mockMvc.perform(put("/api/user")
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonMapper.writeValueAsString(UserTestData.USER())))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value("john.doe"));

			verify(userService).updateUser(any(User.class));
		}
	}
}
