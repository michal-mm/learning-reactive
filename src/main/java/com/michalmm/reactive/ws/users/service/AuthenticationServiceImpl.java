package com.michalmm.reactive.ws.users.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;


@Service
public class AuthenticationServiceImpl implements AuthenticationService {

	@Override
	public Mono<Map<String, String>> authenticate(String username, String password) {
		// TODO Auto-generated method stub
		return null;
	}

}
