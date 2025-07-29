package com.michalmm.reactive.ws.users.service;

import java.util.UUID;

import com.michalmm.reactive.ws.users.presentation.CreateUserRequest;
import com.michalmm.reactive.ws.users.presentation.UserRest;

import reactor.core.publisher.Mono;

public interface UserService {

	Mono<UserRest> createUser(Mono<CreateUserRequest> createUserRequestMono);
	
	Mono<UserRest> getUserById(UUID id);
}
