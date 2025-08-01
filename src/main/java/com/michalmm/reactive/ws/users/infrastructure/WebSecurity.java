package com.michalmm.reactive.ws.users.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

import com.michalmm.reactive.ws.users.service.JwtService;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class WebSecurity {

	@Bean
	public SecurityWebFilterChain httpSecurityFilterChain(ServerHttpSecurity http,
									ReactiveAuthenticationManager authenticationManager,
									JwtService jwtService) {
		JwtAuthenicationFilter jwtAuthenticationFilter = new JwtAuthenicationFilter(jwtService);
		
		return http
				.authorizeExchange(exchanges -> exchanges
						.pathMatchers(HttpMethod.POST, "/users").permitAll()
						.pathMatchers(HttpMethod.POST, "/login").permitAll()
						.pathMatchers(HttpMethod.GET, "/users/stream").permitAll()
				.anyExchange().authenticated())
				// CSFR is disabled if the service is stateless and doesn't use cookies,
				// however, for services that APIs are called directly from the browser
				// one should keep CSRF enabled to protect from attacks
				.csrf(ServerHttpSecurity.CsrfSpec::disable)
				.httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
				.authenticationManager(authenticationManager)
				.addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
				.securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
				.build();
	}
	
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
