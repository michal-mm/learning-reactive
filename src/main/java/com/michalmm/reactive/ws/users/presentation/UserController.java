package com.michalmm.reactive.ws.users.presentation;

import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/users")
public class UserController {

	@PostMapping
	public Mono<UserRest> createUser(@RequestBody @Valid Mono<CreateUserRequest> createUserRequest) {

		return createUserRequest.map(request -> new UserRest(UUID.randomUUID(),
													request.getFirstName(),
													request.getLastName(),
													request.getEmail())
		);
	}

}
