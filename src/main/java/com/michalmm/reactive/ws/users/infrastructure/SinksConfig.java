package com.michalmm.reactive.ws.users.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.michalmm.reactive.ws.users.presentation.model.UserRest;

import reactor.core.publisher.Sinks;

@Configuration
public class SinksConfig {

	@Bean
	public Sinks.Many<UserRest> userSink() {
		return Sinks.many().multicast().onBackpressureBuffer();
	}
}
