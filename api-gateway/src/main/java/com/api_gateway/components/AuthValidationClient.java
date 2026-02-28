package com.api_gateway.components;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class AuthValidationClient {

    private final WebClient.Builder webClientBuilder;

    public AuthValidationClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public Mono<Boolean> validateToken(String token, String user) {
        return webClientBuilder
                .build()
                .post()       
                .uri("lb://auth-service/auth/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("token", token, "user", user))
                .retrieve()
                .bodyToMono(Boolean.class);
    }
}
