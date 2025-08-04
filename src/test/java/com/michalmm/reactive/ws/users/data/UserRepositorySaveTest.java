package com.michalmm.reactive.ws.users.data;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;

import reactor.test.StepVerifier;

@DataR2dbcTest
class UserRepositorySaveTest {

	@Autowired
	private UserRepository userRepository;
	
	
	@Test
	void testSave_whenValidUserProvided_shouldSucceed() {
	    // Arrange
		UserEntity validUser = new UserEntity(null, "Sergey", "Kargopolov", "test@test.com", "password123");
	 
	    // Act & Assert
		userRepository.save(validUser)
        .as(StepVerifier::create)
        .expectNextMatches(savedUser -> {
            return savedUser.getId() != null
                    && savedUser.getFirstName().equals(validUser.getFirstName())
                    && savedUser.getLastName().equals(validUser.getLastName())
                    && savedUser.getEmail().equals(validUser.getEmail());
        })
        .verifyComplete();
	}
}
