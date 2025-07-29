package com.michalmm.reactive.ws.users.service;

import org.springframework.stereotype.Service;

import com.michalmm.reactive.ws.users.presentation.CreateUserRequest;
import com.michalmm.reactive.ws.users.presentation.UserRest;

import reactor.core.publisher.Mono;

@Service
public class UserServiceImpl implements UserService {

	@Override
	public Mono<UserRest> createUser(Mono<CreateUserRequest> createUserRequestMono) {
		// TODO Auto-generated method stub
		return null;
	}

}
