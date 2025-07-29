package com.michalmm.reactive.ws.users.presentation;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
//	@ResponseStatus(HttpStatus.CREATED)
	public Mono<ResponseEntity<UserRest>> createUser(@RequestBody @Valid Mono<CreateUserRequest> createUserRequest) {

		return createUserRequest.map(request -> new UserRest(UUID.randomUUID(),
													request.getFirstName(),
													request.getLastName(),
													request.getEmail()))
				.map( userRest -> ResponseEntity.status(HttpStatus.CREATED)
						.location(URI.create("/users/" + userRest.getId()) )
						.body(userRest) );
	}

}
