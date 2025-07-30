package com.michalmm.reactive.ws.users.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;

@Service
public class JwtServiceImpl implements JwtService {

	private final Environment environment;
	
	
	
	public JwtServiceImpl(Environment environment) {
		super();
		this.environment = environment;
	}



	@Override
	public String generateJwt(String subject) {
		
		return Jwts
				.builder()
				.subject(subject)
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
				.signWith(getSigningKey())
				.compact();
	}

	
	@Override
	public Mono<Boolean> validateJwt(String token) {
		return Mono.just(token)
				.map(jwt -> parseToken(jwt))
				.map(claims -> !claims.getExpiration().before(new Date()))
				.onErrorReturn(false);
	}
	
	
	private Claims parseToken(String token) {
		return Jwts.parser()
				.verifyWith(getSigningKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}
	
	
	private SecretKey getSigningKey() {
		
		return Optional.ofNullable(environment.getProperty("token.secret"))
				.map(tokenSecret -> tokenSecret.getBytes())
				.map(tokenSecretBytes -> Keys.hmacShaKeyFor(tokenSecretBytes))
				.orElseThrow(() -> 
					new IllegalArgumentException("token.secret must be configured in the application.properties file"));
	}

}


