package com.petgram.auth_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petgram.auth_service.dto.UserRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthServiceApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void testRegisterSuccess() throws Exception {
		UserRequest newUser = new UserRequest();
		newUser.setEmail("test@example.com");
		newUser.setPassword("password123");

		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newUser)))
				.andExpect(status().isOk());
	}

	@Test
	void testRegisterDuplicateFail() throws Exception {
		UserRequest user = new UserRequest();
		user.setEmail("duplicate@example.com");
		user.setPassword("password123");

		// Primero registramos
		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(user)))
				.andExpect(status().isOk());

		// Intentamos registrar el mismo de nuevo
		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(user)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testLoginSuccess() throws Exception {
		UserRequest user = new UserRequest();
		user.setEmail("login@example.com");
		user.setPassword("password123");

		// Registrar primero
		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(user)));

		// Intentar Login
		mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(user)))
				.andExpect(status().isOk());
	}

	@Test
	void testLoginFail() throws Exception {
		UserRequest user = new UserRequest();
		user.setEmail("wrong@example.com");
		user.setPassword("wrongpass");

		mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(user)))
				.andExpect(status().isUnauthorized());
	}

}
