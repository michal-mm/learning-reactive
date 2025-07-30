package com.michalmm.reactive.ws.users.infrastructure;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

public class JwtAuthenicationFilter implements WebFilter {

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		return chain.filter(exchange);
	}

}
