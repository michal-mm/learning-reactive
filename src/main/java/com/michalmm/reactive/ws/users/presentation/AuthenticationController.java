package com.michalmm.reactive.ws.users.presentation;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.michalmm.reactive.ws.users.presentation.model.AuthenticationRequest;
import com.michalmm.reactive.ws.users.service.AuthenticationService;

import reactor.core.publisher.Mono;

@RestController
public class AuthenticationController {
	
	private final AuthenticationService authenticationService;
	
	

	public AuthenticationController(AuthenticationService authenticationService) {
		super();
		this.authenticationService = authenticationService;
	}



	@PostMapping("/login")
	public Mono<ResponseEntity<Object>> login(@RequestBody Mono<AuthenticationRequest> authenticationRequestMono) {
		return authenticationRequestMono
				.flatMap(authenticationRequest -> 
					authenticationService.authenticate(authenticationRequest.getEmail(), 
											authenticationRequest.getPassword()))
				.map(authenticationResultMap -> ResponseEntity.ok()
						.header(HttpHeaders.AUTHORIZATION, "Bearer "
								+ authenticationResultMap.get("token"))
						.header("UserId", authenticationResultMap.get("userId"))
						.build());
//				.onErrorReturn(BadCredentialsException.class, 
//						ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//						.body("Invalid credentials"))
//				.onErrorReturn(Exception.class, 
//						ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//						.build());
	}
}
