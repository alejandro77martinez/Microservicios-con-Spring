package com.api_gateway.components;

import java.nio.charset.StandardCharsets;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Flux;

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

      final ServerWebExchange exchangeToFilter = routerValidator.shouldInjectCookieTokenIntoBody.test(request)
        ? mutateExchangeWithTokenBody(exchange, token.trim())
        : exchange;

      //System.out.println("token de cookie: " + token); 
      return authValidationClient.validateToken(token.trim()).flatMap(isValid -> {
        if (!isValid) {
          exchangeToFilter.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
          return exchangeToFilter.getResponse().setComplete();
        }
          return chain.filter(exchangeToFilter);
      });

    };
  }

  public static class Config {
    // Placeholder para opciones futuras del filtro.
  }

  private ServerWebExchange mutateExchangeWithTokenBody(ServerWebExchange exchange, String token) {
    String body = """
        {"token":"%s"}
        """.formatted(escapeJson(token));
    byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);
    DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();

    ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
      @Override
      public HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(super.getHeaders());
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setContentLength(bodyBytes.length);
        return headers;
      }

      @Override
      public Flux<DataBuffer> getBody() {
        DataBuffer buffer = bufferFactory.wrap(bodyBytes);
        return Flux.just(buffer);
      }
    };

    return exchange.mutate().request(mutatedRequest).build();
  }

  private String escapeJson(String value) {
    return value
      .replace("\\", "\\\\")
      .replace("\"", "\\\"");
  }
}
