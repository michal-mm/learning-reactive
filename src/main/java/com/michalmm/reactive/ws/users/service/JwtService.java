package com.michalmm.reactive.ws.users.service;

import reactor.core.publisher.Mono;

public interface JwtService {

	public String generateJwt(String subject);

	public Mono<Boolean> validateJwt(String token);
	
	public String extractTokenSubject(String token);
}
