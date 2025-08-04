package com.michalmm.reactive.ws.users.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.reactive.function.client.WebClient;

import com.michalmm.reactive.ws.users.data.UserEntity;
import com.michalmm.reactive.ws.users.data.UserRepository;
import com.michalmm.reactive.ws.users.presentation.model.AlbumRest;
import com.michalmm.reactive.ws.users.presentation.model.CreateUserRequest;
import com.michalmm.reactive.ws.users.presentation.model.UserRest;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

	@Mock
	private UserRepository userRepository; 
	
	@Mock
	private PasswordEncoder passwordEncoder;
	
	@Mock
	private WebClient webClient;
	
	
	private Sinks.Many<UserRest> userSink;
	
	private UserServiceImpl userService;
	
	
	@BeforeEach
	void setUp() {
		userSink = Sinks.many().multicast().onBackpressureBuffer();
		userService = new UserServiceImpl(userRepository, passwordEncoder, userSink, webClient);
	}
	
	
	@Test
	public void testCreateUser_withValidRequest_returnsCreatedUserDetails() {
		// Arrange
		CreateUserRequest request = new  CreateUserRequest(
				"Pete",
				"Sampras",
				"pete@tenis.com",
				"1234abcd");
		
		UserEntity savedEntity = new UserEntity();
		savedEntity.setId(UUID.randomUUID());
		savedEntity.setFirstName(request.getFirstName());
		savedEntity.setLastName(request.getLastName());
		savedEntity.setEmail(request.getEmail());
		savedEntity.setPassword(request.getPassword());
		
		when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
		when(userRepository.save(any(UserEntity.class))).thenReturn(Mono.just(savedEntity));
		
		
		// Act
		Mono<UserRest> result = userService.createUser(Mono.just(request));
		
		// Assert
		StepVerifier.create(result)
			.expectNextMatches(userRest -> 
				userRest.getId().equals(savedEntity.getId()) &&
				userRest.getFirstName().equals(savedEntity.getFirstName()) &&
				userRest.getLastName().equals(savedEntity.getLastName()) &&
				userRest.getEmail().equals(savedEntity.getEmail())
			)
			.verifyComplete();
		verify(userRepository, times(1)).save(any(UserEntity.class));
		
		// the code below is blocking, it will wait till Mono finishes
//		UserRest user = result.block();
//		assertEquals(savedEntity.getFirstName(), user.getFirstName());
		// ... and other assertions
	}
	
	@Test
	public void testCreateUser_withValidRequest_EmitsEventToSink() {
		// Arrange
		CreateUserRequest request = new CreateUserRequest("John", "Doe", "john@example.com", "password123");
		 
		UserEntity savedEntity = new UserEntity();
		savedEntity.setId(UUID.randomUUID());
		savedEntity.setFirstName("John");
		savedEntity.setLastName("Doe");
		savedEntity.setEmail("john@example.com");
		savedEntity.setPassword("encodedPassword");
		 
		when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
		when(userRepository.save(any(UserEntity.class))).thenReturn(Mono.just(savedEntity));
		 
		// Subscribe to the sink before triggering the service call.
		Flux<UserRest> sinkFlux = userSink.asFlux();
	 
	    // Act
		StepVerifier.create(
			    userService.createUser(Mono.just(request))
			        .thenMany(userSink.asFlux().take(1))
			)
			.expectNextMatches(userRest ->
			    userRest.getId().equals(savedEntity.getId()) &&
			    userRest.getFirstName().equals(savedEntity.getFirstName()) &&
			    userRest.getLastName().equals(savedEntity.getLastName()) &&
			    userRest.getEmail().equals(savedEntity.getEmail())
			)
			.verifyComplete();
		
	    // Assert
	}
	
    @Test
    void testCreateUser_withValidRequest_EmitsEventToSink_2() {
            // Arrange
            CreateUserRequest request = new CreateUserRequest(
                            "Sergey",
                            "Kargopolov",
                            "test@test.com",
                            "123456789");

            UserEntity savedEntity = new UserEntity();
            savedEntity.setId(UUID.randomUUID());
            savedEntity.setFirstName(request.getFirstName());
            savedEntity.setLastName(request.getLastName());
            savedEntity.setEmail(request.getEmail());
            savedEntity.setPassword(request.getPassword());

            when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
            when(userRepository.save(any(UserEntity.class))).thenReturn(Mono.just(savedEntity));

            // Subscribe to the sink before triggering the service call.
            Flux<UserRest> sinkFlux = userSink.asFlux();

            // Act and Assert
            StepVerifier.create(userService.createUser(Mono.just(request))
            .thenMany(userSink.asFlux().take(1)))
                            .expectNextMatches(userRest -> userRest.getId().equals(savedEntity.getId()) &&
                                            userRest.getFirstName().equals(savedEntity.getFirstName()) &&
                                            userRest.getLastName().equals(savedEntity.getLastName()) &&
                                            userRest.getEmail().equals(savedEntity.getEmail()))
                            .verifyComplete();
    }
    
    @Test
    void testGetUserById_WithExistingUser_ReturnsUserRest() {
     // Arrange
		UUID userId = UUID.randomUUID();
		UserEntity userEntity = new UserEntity();
		userEntity.setId(userId);
		userEntity.setFirstName("Sergey");
		userEntity.setLastName("Kargopolov");
		userEntity.setEmail("test@test.com");
    
		when(userRepository.findById(userId)).thenReturn(Mono.just(userEntity));
		
     // Act
		Mono<UserRest> result = userService.getUserById(userId, null, "jwt-token");
    
     // Assert
		StepVerifier.create(result)
	    .expectNextMatches(userRest -> userRest.getId().equals(userId) &&
	        userRest.getFirstName().equals(userEntity.getFirstName()) &&
	        userRest.getLastName().equals(userEntity.getLastName()) &&
	        userRest.getEmail().equals(userEntity.getEmail()) &&
	        userRest.getAlbums() == null)
	    .verifyComplete();
		
		 // Verify that findById() method was called once
		 verify(userRepository, times(1)).findById(userId);
		 
		 // Verify that WebClient is not invoked when include is null
		 verify(webClient, never()).get();
    }
    
    
    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    void testGetUserById_WithIncludeAlbums_ReturnsAlbums() {
     // Arrange
    	UUID userId = UUID.randomUUID();
    	String jwt = "valid-jwt";
    	 
    	// 1. Setup UserEntity
    	UserEntity userEntity = new UserEntity();
    	userEntity.setId(userId);
    	userEntity.setFirstName("Sergey");
    	userEntity.setLastName("Kargopolov");
    	userEntity.setEmail("test@test.com");
    	userEntity.setPassword("encodedPass");
     
    	// 2. Mock repository response
    	when(userRepository.findById(userId)).thenReturn(Mono.just(userEntity));
    	
    	// 3. Mock WebClient response with albums
    	WebClient.RequestHeadersUriSpec getSpec = mock(WebClient.RequestHeadersUriSpec.class);
    	WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
    	WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
    	 
    	when(webClient.get()).thenReturn(getSpec);
    	when(getSpec.uri(any(Function.class))).thenReturn(headersSpec);
    	when(headersSpec.header(eq("Authorization"), eq(jwt))).thenReturn(headersSpec);
    	when(headersSpec.retrieve()).thenReturn(responseSpec);
    	when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    	
    	// Explicitly return test albums
    	AlbumRest album1 = new AlbumRest("album1", "Summer Vacation");
    	AlbumRest album2 = new AlbumRest("album2", "Family Reunion");
    	when(responseSpec.bodyToFlux(AlbumRest.class)).thenReturn(Flux.just(album1, album2));
     // Act
    	Mono<UserRest> result = userService.getUserById(userId, "albums", jwt);
    	
     // Assert
    	// Assert: Verify albums are present
    	StepVerifier.create(result)
    	 .expectNextMatches(user -> {
    	  // Verify user details
    	  assertEquals(userId, user.getId(), "User ID mismatch");
    	  assertEquals(userEntity.getFirstName(), user.getFirstName(), "First name mismatch");
    	  assertEquals(userEntity.getLastName(), user.getLastName(), "Last name mismatch");
    	  assertEquals(userEntity.getEmail(), user.getEmail(), "Email mismatch");
    	 
    	  // Verify albums
    	  assertNotNull(user.getAlbums(), "Albums list should not be null");
    	  assertEquals(2, user.getAlbums().size(), "Incorrect number of albums");
    	  assertEquals("Summer Vacation", user.getAlbums().get(0).getTitle());
    	  assertEquals("Family Reunion", user.getAlbums().get(1).getTitle());
    	  return true;
    	 })
    	 .verifyComplete();
    	
    	// Verify repository call
    	verify(userRepository).findById(userId);
    }

}
