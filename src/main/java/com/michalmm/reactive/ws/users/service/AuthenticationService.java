package com.michalmm.reactive.ws.users.service;

import java.util.Map;

import reactor.core.publisher.Mono;


public interface AuthenticationService {

	public Mono<Map<String, String>> authenticate(String username, String password);
}
