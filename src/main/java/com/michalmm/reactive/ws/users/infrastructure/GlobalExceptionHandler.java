package com.michalmm.reactive.ws.users.infrastructure;

import java.util.stream.Collectors;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(DuplicateKeyException.class)
	public Mono<ErrorResponse> handleDuplicateKeyException(DuplicateKeyException dke) {
		System.out.println("[EXC]:: DuplicateKeyException found: " + dke.getMessage());
		
		return Mono.just(ErrorResponse.builder(dke, HttpStatus.CONFLICT, dke.getMessage()).build());
	}
	
	@ExceptionHandler(Exception.class)
	public Mono<ErrorResponse> handleGeneralException(Exception e) {
		System.out.println("[EXC]:: GENERAL Exception found: " + e.getMessage());
		
		return Mono.just(ErrorResponse.builder(e, HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()).build());
	}
	
	@ExceptionHandler(WebExchangeBindException.class)
	public Mono<ErrorResponse> handleWebExchangeBindException(WebExchangeBindException webe) {
		String errorMessage = webe.getBindingResult().getAllErrors().stream()
								.map(error -> error.getDefaultMessage())
								.collect(Collectors.joining(", "));
		
		return Mono.just(ErrorResponse.builder(webe, HttpStatus.BAD_REQUEST, errorMessage)
				.build());
	}
	
	@ExceptionHandler(BadCredentialsException.class)
	public Mono<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
		return Mono.just(ErrorResponse.builder(ex, HttpStatus.UNAUTHORIZED, "!!!"+ex.getMessage()).build());
	}
}
