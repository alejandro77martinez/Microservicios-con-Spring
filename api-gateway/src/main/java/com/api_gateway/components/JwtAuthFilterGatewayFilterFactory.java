package com.api_gateway.components;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthFilterGatewayFilterFactory
        extends AbstractGatewayFilterFactory<JwtAuthFilterGatewayFilterFactory.Config> {

  private final RouterValidator routerValidator;
  private final AuthValidationClient authValidationClient;

  public JwtAuthFilterGatewayFilterFactory(RouterValidator routerValidator, AuthValidationClient authValidationClient) {
    super(Config.class);
    this.routerValidator = routerValidator;
    this.authValidationClient = authValidationClient;
  }

  @Override
  public GatewayFilter apply(Config config) {
    return (exchange, chain) -> {
      ServerHttpRequest request = exchange.getRequest();

      // Si la ruta no requiere autenticación, pasa al siguiente filtro
      if (!routerValidator.isSecured.test(request)) {
        System.out.println("Ruta sin filtro :" + request.toString());
        return chain.filter(exchange);
      }

      HttpCookie cookie = request.getCookies().getFirst("AUTH_TOKEN");
      String token = cookie != null ? cookie.getValue() : "";

      if (token.isBlank()) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete(); 
      }
      System.out.println("token de cookie: " + token); 
      return authValidationClient.validateToken(token.trim()).flatMap(isValid -> {
        if (!isValid) {
          exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
          return exchange.getResponse().setComplete();
        }
          return chain.filter(exchange);
      });

    };
  }

  public static class Config {
    // Placeholder para opciones futuras del filtro.
  }
}
