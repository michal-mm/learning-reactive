package com.michalmm.reactive.ws.users.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.michalmm.reactive.ws.users.data.UserEntity;
import com.michalmm.reactive.ws.users.data.UserRepository;
import com.michalmm.reactive.ws.users.presentation.CreateUserRequest;
import com.michalmm.reactive.ws.users.presentation.UserRest;

import reactor.core.publisher.Mono;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private final UserRepository userRepository;
	
	public UserServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;}

	@Override
	public Mono<UserRest> createUser(Mono<CreateUserRequest> createUserRequestMono) {
		// create user entity object
		
		return createUserRequestMono
				.mapNotNull(this::convertToEntity)
				.flatMap(userRepository::save)
				.mapNotNull(this::converToRest);
	}

	
	private UserEntity convertToEntity(CreateUserRequest createUserRequest) {
		UserEntity userEntity = new UserEntity();
		BeanUtils.copyProperties(createUserRequest, userEntity);
		
		return userEntity;
	}
	
	private UserRest converToRest(UserEntity userEntity) {
		UserRest userRest = new UserRest();
		BeanUtils.copyProperties(userEntity, userRest);
		
		return userRest;
	}
}
