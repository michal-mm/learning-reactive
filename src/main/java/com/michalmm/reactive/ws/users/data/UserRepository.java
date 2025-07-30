package com.michalmm.reactive.ws.users.data;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;

public interface UserRepository extends ReactiveCrudRepository<UserEntity, UUID> {

	public Flux<UserEntity> findAllBy(Pageable pageable);
}
