package com.michalmm.reactive.ws.users.infrastructure;

import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.michalmm.reactive.ws.users.service.JwtService;

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
		
		return chain.filter(exchange);
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
