package com.michalmm.reactive.ws.users.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.michalmm.reactive.ws.users.data.UserEntity;
import com.michalmm.reactive.ws.users.data.UserRepository;

import reactor.core.publisher.Mono;


@Service
public class AuthenticationServiceImpl implements AuthenticationService {

	
	private final ReactiveAuthenticationManager reactiveAuthenticationManager;
	
	private final UserRepository userRepository; 
	
	
	
	public AuthenticationServiceImpl(ReactiveAuthenticationManager reactiveAuthenticationManager,
						UserRepository userRespoitory) {
		super();
		this.reactiveAuthenticationManager = reactiveAuthenticationManager;
		this.userRepository = userRespoitory;
	}

	@Override
	public Mono<Map<String, String>> authenticate(String username, String password) {
		return reactiveAuthenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(username, password))
				.then(getUserDetails(username))
				.map(this::createAuthResponse);
	}

	private Mono<UserEntity> getUserDetails(String username) {
		return userRepository.findByEmail(username);
	}
	
	private Map<String, String> createAuthResponse(UserEntity user) {
		Map<String, String> result = new HashMap<>();
		
		result.put("userId", user.getId().toString());
		// TODO - replace it with actual JWT
		result.put("token", "JWT"); 
		
		return result;
	}
}
