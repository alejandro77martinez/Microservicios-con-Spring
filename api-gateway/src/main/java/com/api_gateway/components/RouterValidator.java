package com.api_gateway.components;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.function.Predicate;

@Component
public class RouterValidator {

  public static final List<String> OPEN_API_ENDPOINTS = List.of(
    "/api/v1/auth/login",
    "/api/v1/user/register",
    "/api/v1/user/exist"
  );

  public static final List<String> COOKIE_TOKEN_BODY_ENDPOINTS = List.of(
    "/api/v1/auth/refresh",
    "/api/v1/auth/session"
  );

  public Predicate<ServerHttpRequest> isSecured = request -> 
    OPEN_API_ENDPOINTS.stream()
      .noneMatch(uri -> request.getURI().getPath().startsWith(uri));

  public Predicate<ServerHttpRequest> shouldInjectCookieTokenIntoBody = request ->
    COOKIE_TOKEN_BODY_ENDPOINTS.stream()
      .anyMatch(uri -> request.getURI().getPath().startsWith(uri));
}
