package com.michalmm.reactive.ws.users.infrastructure;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.michalmm.reactive.ws.users.service.JwtService;

import io.jsonwebtoken.lang.Collections;
import reactor.core.publisher.Mono;

public class JwtAuthenicationFilter implements WebFilter {
	
	private final JwtService jwtService;
	
	

	public JwtAuthenicationFilter(JwtService jwtService) {
		super();
		this.jwtService = jwtService;
	}


	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		
		String token = extractToken(exchange);
		
		if (token == null) return chain.filter(exchange);
		
		return validateToken(token)
				.flatMap(isValid -> isValid ? authenticateAndContinue(token, exchange, chain)
											: handleInvalidToken(exchange));
	}
	
	
	private Mono<Void> handleInvalidToken(ServerWebExchange exchange) {
		exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
		return exchange.getResponse().setComplete();
	}


	private Mono<Void> authenticateAndContinue(String token, ServerWebExchange exchange, WebFilterChain chain) {
		return Mono.just(jwtService.extractTokenSubject(token))
				.flatMap(subject -> {
					Authentication auth = new UsernamePasswordAuthenticationToken(subject, null, 
							Collections.emptyList());
					return chain
							.filter(exchange)
							.contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
				});
	}


	private String extractToken(ServerWebExchange exchange) {
		String authorizarionHeader = exchange.getRequest()
									.getHeaders()
									.getFirst(HttpHeaders.AUTHORIZATION);
		
		if (StringUtils.hasText(authorizarionHeader) && authorizarionHeader.startsWith("Bearer ")) {
			return authorizarionHeader.substring(7).trim();
		}
				
		return null;
	}
	
	
	private Mono<Boolean> validateToken(String token) {
		
		return jwtService.validateJwt(token);
	}

}
