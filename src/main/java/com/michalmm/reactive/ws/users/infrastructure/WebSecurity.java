package com.michalmm.reactive.ws.users.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class WebSecurity {

	@Bean
	public SecurityWebFilterChain httpSecurityFilterChain(ServerHttpSecurity http,
									ReactiveAuthenticationManager authenticationManager) {
		return http
				.authorizeExchange(exchanges -> exchanges
						.pathMatchers(HttpMethod.POST, "/users").permitAll()
						.pathMatchers(HttpMethod.POST, "/login").permitAll()
				.anyExchange().authenticated())
				// CSFR is disabled if the service is stateless and doesn't use cookies,
				// however, for services that APIs are called directly from the browser
				// one should keep CSRF enabled to protect from attacks
				.csrf(ServerHttpSecurity.CsrfSpec::disable)
				.httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
				.authenticationManager(authenticationManager)
				.build();
	}
	
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
