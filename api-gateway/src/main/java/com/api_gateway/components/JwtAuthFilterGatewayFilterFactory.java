package com.api_gateway.components;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import reactor.core.publisher.Mono;
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
                return chain.filter(exchange);
            }

            String authorization = request.getHeaders().getFirst("Authorization");
            String user = request.getHeaders().getFirst("X-User");
        
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String token = authorization.substring(7);

            if (token.isBlank()) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            return authValidationClient.validateToken(token.trim(), user.trim()).flatMap(isValid -> {
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
