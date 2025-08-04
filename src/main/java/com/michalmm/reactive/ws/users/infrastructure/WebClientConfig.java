package com.michalmm.reactive.ws.users.infrastructure;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

	@Value("${base-uri}")
	private String baseUri;
	
	
	@Bean
	public WebClient webClient() {
		System.out.println("BASE-URI: " + baseUri);
		return WebClient.builder()
				.baseUrl(baseUri)
				//.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.defaultHeaders(headers -> {
					headers.setContentType(MediaType.APPLICATION_JSON);
					headers.setAccept(List.of(MediaType.APPLICATION_JSON));
				})
				.build();
	}
}
