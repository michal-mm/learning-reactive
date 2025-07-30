package com.michalmm.reactive.ws.users.service;

import java.util.UUID;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;

import com.michalmm.reactive.ws.users.presentation.model.CreateUserRequest;
import com.michalmm.reactive.ws.users.presentation.model.UserRest;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService extends ReactiveUserDetailsService {

	Mono<UserRest> createUser(Mono<CreateUserRequest> createUserRequestMono);
	
	Mono<UserRest> getUserById(UUID id);
	
	Flux<UserRest> findAll(int page, int limit);
}
