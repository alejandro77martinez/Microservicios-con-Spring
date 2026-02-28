package com.api_gateway.components;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.function.Predicate;

@Component
public class RouterValidator {
    public static final List<String> OPEN_API_ENDPOINTS = List.of(
        "/auth/login",
        "/auth/register"
    );

    public Predicate<ServerHttpRequest> isSecured = request -> 
        OPEN_API_ENDPOINTS.stream()
            .noneMatch(uri -> request.getURI().getPath().startsWith(uri));
}