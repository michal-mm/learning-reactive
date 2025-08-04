package com.michalmm.reactive.ws.users.service;

import java.util.ArrayList;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.michalmm.reactive.ws.users.data.UserEntity;
import com.michalmm.reactive.ws.users.data.UserRepository;
import com.michalmm.reactive.ws.users.presentation.model.AlbumRest;
import com.michalmm.reactive.ws.users.presentation.model.CreateUserRequest;
import com.michalmm.reactive.ws.users.presentation.model.UserRest;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private final UserRepository userRepository;
	
	private final PasswordEncoder passwordEncoder;
	
	private final Sinks.Many<UserRest> userSink;
	
	private final WebClient webClient;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	public UserServiceImpl(UserRepository userRepository, 
			PasswordEncoder passwordEncoder,
			Sinks.Many<UserRest> userSink,
			WebClient webClient) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.userSink = userSink;
		this.webClient = webClient;
	}

	@Override
	public Mono<UserRest> createUser(Mono<CreateUserRequest> createUserRequestMono) {
		// create user entity object
		
		return createUserRequestMono
				.flatMap(this::convertToEntity)
				.flatMap(userRepository::save)
				.mapNotNull(this::convertToRest)
				.doOnSuccess(savedUser -> userSink.tryEmitNext(savedUser));
//				.onErrorMap(throwable -> {
//					if (throwable instanceof DuplicateKeyException)
//						return new ResponseStatusException(HttpStatus.CONFLICT, throwable.getMessage());
//					else if (throwable instanceof DataIntegrityViolationException)
//						return new ResponseStatusException(HttpStatus.BAD_REQUEST, throwable.getMessage());
//					else
//						return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, throwable.getMessage());
//					});				
	}
	
	@Override
	public Mono<UserRest> getUserById(UUID id, String include, String jwt) {
//		return userRepository
//				.findById(id)
//				.mapNotNull(this::convertToRest);
		return userRepository
                .findById(id)
                .mapNotNull(userEntity -> convertToRest(userEntity))
                .flatMap(user -> {
                	if (include!=null && include.equals("albums")) {
                		// fetch user's photo albums and add them to a user object
                		return includeUserAlbums(user, jwt);
                	}
                	return Mono.just(user);
                } );
	}

	
	@Override
	public Flux<UserRest> findAll(int page, int limit) {
		if(page>0) page = page - 1;
		
		Pageable pageable = PageRequest.of(page, limit);
		
		return userRepository.findAllBy(pageable)
				.map(userEntity -> convertToRest(userEntity));
	}
	
	
	@Override
	public Mono<UserDetails> findByUsername(String username) {
		return userRepository.findByEmail(username)
				.map(userEntity -> User
						.withUsername(userEntity.getEmail())
						.password(userEntity.getPassword())
						.authorities(new ArrayList<>())
						.build());
	}
	
	
	@Override
	public Flux<UserRest> streamUser() {
		return userSink.asFlux()
				.publish()
				.autoConnect();
	}
	
	
	private Mono<UserEntity> convertToEntity(CreateUserRequest createUserRequest) {
		return Mono.fromCallable(() -> {
			UserEntity userEntity = new UserEntity();
			BeanUtils.copyProperties(createUserRequest, userEntity);
			userEntity.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));
			
			return userEntity;
		}).subscribeOn(Schedulers.boundedElastic());
	}
	
	private UserRest convertToRest(UserEntity userEntity) {
		UserRest userRest = new UserRest();
		BeanUtils.copyProperties(userEntity, userRest);
		
		return userRest;
	}

    private Mono<UserRest> includeUserAlbums(UserRest user, String jwt) {
		return webClient.get()
				.uri(uriBuilder -> uriBuilder
						.port(8084)
						.path("/albums")
						//.queryParam("userId", user.getId())
						.build())
				.header("Authorization", jwt)
				.retrieve()
				.onStatus(HttpStatusCode::is4xxClientError, response -> {
					return Mono.error(new RuntimeException("Albums not found for user"));
				})
				.onStatus(HttpStatusCode::is5xxServerError, response -> {
					return Mono.error(new RuntimeException("Server error while fetching albums"));
				})
				.bodyToFlux(AlbumRest.class)
				.collectList()
				.map(albums -> {
					user.setAlbums(albums);
					return user;
				})
				.onErrorResume(e -> {
					logger.error("Error fetching albums: ", e);
					return Mono.just(user);
				});		
	}

}
