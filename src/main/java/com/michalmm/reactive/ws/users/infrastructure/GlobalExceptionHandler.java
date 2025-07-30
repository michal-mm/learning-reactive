package com.michalmm.reactive.ws.users.infrastructure;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
}
