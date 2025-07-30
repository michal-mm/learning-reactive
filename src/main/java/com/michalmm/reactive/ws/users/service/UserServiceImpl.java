package com.michalmm.reactive.ws.users.service;

import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.michalmm.reactive.ws.users.data.UserEntity;
import com.michalmm.reactive.ws.users.data.UserRepository;
import com.michalmm.reactive.ws.users.presentation.CreateUserRequest;
import com.michalmm.reactive.ws.users.presentation.UserRest;

import reactor.core.publisher.Flux;
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
				.mapNotNull(this::convertToRest);
	}
	
	@Override
	public Mono<UserRest> getUserById(UUID id) {
		return userRepository
				.findById(id)
				.mapNotNull(this::convertToRest);
	}

	
	@Override
	public Flux<UserRest> findAll(int page, int limit) {
		if(page>0) page = page - 1;
		
		Pageable pageable = PageRequest.of(page, limit);
		
		return userRepository.findAllBy(pageable)
				.map(userEntity -> convertToRest(userEntity));
	}
	
	
	private UserEntity convertToEntity(CreateUserRequest createUserRequest) {
		UserEntity userEntity = new UserEntity();
		BeanUtils.copyProperties(createUserRequest, userEntity);
		
		return userEntity;
	}
	
	private UserRest convertToRest(UserEntity userEntity) {
		UserRest userRest = new UserRest();
		BeanUtils.copyProperties(userEntity, userRest);
		
		return userRest;
	}

}
