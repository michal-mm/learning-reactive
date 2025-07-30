package com.michalmm.reactive.ws.users.presentation;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.michalmm.reactive.ws.users.presentation.model.CreateUserRequest;
import com.michalmm.reactive.ws.users.presentation.model.UserRest;
import com.michalmm.reactive.ws.users.service.UserService;

import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/users")
public class UserController {
	
	private final UserService userService;
	
	
	public UserController(UserService userService) {
		this.userService = userService;
	}

	
	@PostMapping
//	@ResponseStatus(HttpStatus.CREATED)
	public Mono<ResponseEntity<UserRest>> createUser(@RequestBody @Valid Mono<CreateUserRequest> createUserRequest) {

		return userService.createUser(createUserRequest)
				.map(userRest -> ResponseEntity
								.status(HttpStatus.CREATED)
								.location(URI.create("/users/" + userRest.getId()))
								.body(userRest));
		
//		return createUserRequest.map(request -> new UserRest(UUID.randomUUID(),
//													request.getFirstName(),
//													request.getLastName(),
//													request.getEmail()))
//				.map( userRest -> ResponseEntity.status(HttpStatus.CREATED)
//						.location(URI.create("/users/" + userRest.getId()) )
//						.body(userRest) );
	}
	
	/* 
	 * simple and fast operation so it doesn't have to be reactive and
	 * can stay in plain old synchronous way for input
	 * but returns in non-blocking way the response
	 */
	@GetMapping("/{userId}")
	public Mono<ResponseEntity<UserRest>> getUser(@PathVariable("userId") UUID userId) {
		
		return userService.getUserById(userId)
				.map(userRest -> ResponseEntity.status(HttpStatus.OK).body(userRest))
				.switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build()));
		
//		return Mono.just(new UserRest (
//				userId,
//				"Michal",
//				"Kichal",
//				"adres@email.com"
//				));
	}
	
	
	/**
	 * returns a "stream" - Flux - of UserRest Objects
	 * non-blocking
	 * @return
	 */
	@GetMapping()
	public Flux<UserRest> getUsers(@RequestParam(value="page", defaultValue="0") int page, 
									@RequestParam(value="limit", defaultValue="50") int limit) {
//		return Flux.just(
//				new UserRest(UUID.randomUUID(), "Michal", "Kichal", "na.adres@email.com"),
//				new UserRest(UUID.randomUUID(), "Palka", "Zapalka", "dwa.kije@kto.to"),
//				new UserRest(UUID.randomUUID(), "Ostatni", "Kibic", "na.parapecie@zmienie.cie")
//				);
		
		return userService.findAll(page, limit);
	}

}
