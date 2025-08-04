package com.michalmm.reactive.ws.users.presentation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.michalmm.reactive.ws.users.infrastructure.TestSecurityConfig;
import com.michalmm.reactive.ws.users.presentation.model.CreateUserRequest;
import com.michalmm.reactive.ws.users.presentation.model.UserRest;
import com.michalmm.reactive.ws.users.service.UserService;

import reactor.core.publisher.Mono;


//@WebFluxTest(controllers={UserController.class})
@WebFluxTest(UserController.class)
@Import(TestSecurityConfig.class)
public class UserControllerTest {
	
	@MockitoBean
	private UserService userService;
	
	@Autowired
	private WebTestClient webTestClient;
	
	
	@Test
	public void testCreateUser_withValidRequest_returnsCreatedStatusAndUserDetails() {
		// Arrange
		CreateUserRequest createUserRequest = new CreateUserRequest(
				"Pete",
				"Sampras",
				"pete@tenis.com",
				"1234abcd");
		
		UUID userId = UUID.randomUUID();
		String expectedLocation = "/users/" + userId;
		
		UserRest expectedUserRest = new UserRest(
				userId,
				createUserRequest.getFirstName(),
				createUserRequest.getLastName(),
				createUserRequest.getEmail(),
				null);
		
		Mockito.when(userService.createUser(Mockito.<Mono<CreateUserRequest>>any()))
				.thenReturn(Mono.just(expectedUserRest));
		
		// Act
		webTestClient
			.post()
			.uri("/users")
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(createUserRequest)
			.exchange()
			.expectStatus().isCreated()
			.expectHeader().location(expectedLocation)
			.expectBody(UserRest.class)
			.value(response -> {
				assertEquals(expectedUserRest.getId(), response.getId());
				assertEquals(expectedUserRest.getFirstName(), response.getFirstName());
				assertEquals(expectedUserRest.getLastName(), response.getLastName());
				assertEquals(expectedUserRest.getEmail(), response.getEmail());
			});
		
		// Assert
		Mockito.verify(userService, times(1)).createUser(Mockito.<Mono<CreateUserRequest>>any());
	}
	
	
	@Test
	void testCreateUser_withInvalidRequest_returnsBadRequest() {
	    // Arrange
	    CreateUserRequest invalidRequest = new CreateUserRequest(
	        "Sergey",
	        "Kargopolov",
	        "test@test.com",
	        "123" // Short password
	    );
	 
	    // Act & Assert
	    webTestClient
	        .post()
	        .uri("/users")
	        .contentType(MediaType.APPLICATION_JSON)
	        .bodyValue(invalidRequest)
	        .exchange()
	        .expectStatus().isBadRequest();
	 
	    verify(userService, never()).createUser(any());
	}
	
	@Test
	void testCreateUser_withEmptyFirstName_returnsBadRequest() {
	    // Arrange
	    CreateUserRequest invalidRequest = new CreateUserRequest(
	        "", // Empty first name
	        "Kargopolov",
	        "user@example.com",
	        "123456789"
	    );
	 
	    // Act & Assert
	    webTestClient
	        .post()
	        .uri("/users")
	        .contentType(MediaType.APPLICATION_JSON)
	        .bodyValue(invalidRequest)
	        .exchange()
	        .expectStatus().isBadRequest();
	 
	    verify(userService, never()).createUser(any());
	}
	
	@Test
	void testCreateUser_withTooShortLastName_returnsBadRequest() {
	    // Arrange
	    CreateUserRequest invalidRequest = new CreateUserRequest(
	        "Pete", // Empty first name
	        "K",
	        "user@example.com",
	        "123456789"
	    );
	 
	    // Act & Assert
	    webTestClient
	        .post()
	        .uri("/users")
	        .contentType(MediaType.APPLICATION_JSON)
	        .bodyValue(invalidRequest)
	        .exchange()
	        .expectStatus().isBadRequest();
	 
	    verify(userService, never()).createUser(any());
	}
	
	@Test
	void testCreateUser_withInvalidEmail_returnsBadRequest() {
	    // Arrange
	    CreateUserRequest invalidRequest = new CreateUserRequest(
	        "Pete", // Empty first name
	        "Sampras",
	        "pete_sampras.com",
	        "123456789"
	    );
	 
	    // Act & Assert
	    webTestClient
	        .post()
	        .uri("/users")
	        .contentType(MediaType.APPLICATION_JSON)
	        .bodyValue(invalidRequest)
	        .exchange()
	        .expectStatus().isBadRequest();
	 
	    verify(userService, never()).createUser(any());
	}

	@Test
	void testCreateUser_whenServiceThrowsException_returnsInternalServerErrorWithExpectedStructure() {
	    // Arrange
	    CreateUserRequest validRequest = new CreateUserRequest(
	        "Sergey",
	        "Kargopolov",
	        "user@example.com",
	        "123456789"
	    );
	 
	    when(userService.createUser(any())).thenReturn(Mono.error(new RuntimeException("Service error")));
	 
	    // Act & Assert
	    webTestClient
	        .post()
	        .uri("/users")
	        .contentType(MediaType.APPLICATION_JSON)
	        .bodyValue(validRequest)
	        .exchange()
	        .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
	        .expectBody()
	        .jsonPath("$.instance").isEqualTo("/users")
	        .jsonPath("$.status").isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value())
	        .jsonPath("$.detail").isEqualTo("Service error");
	 
	    verify(userService, times(1)).createUser(any());
	}
}
