package com.example.Api_gateway.services;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;


import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final RouterValidator validator;
    private final JwtUtils jwtUtils;
    public AuthenticationFilter(RouterValidator validator, JwtUtils jwtUtils) {
        super(Config.class);
        this.validator = validator;
        this.jwtUtils = jwtUtils;
    }
    @Override
    public GatewayFilter apply(Config config){
        return ((exchange, chain) -> {
            System.out.println("hola k hace");
            // Add your custom logic here
            var request = exchange.getRequest();
            ServerHttpRequest serverHttpRequest =exchange.getRequest();;

            if(validator.isSecured.test(request)){
                if(authMising(request)){
                    return onError(exchange, HttpStatus.UNAUTHORIZED);
                }

                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);

                System.out.println(authHeader);
                if (authHeader!= null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                    System.out.println(authHeader);

                } else {
                    return onError(exchange, HttpStatus.UNAUTHORIZED);
                }
                if(jwtUtils.isExpired(authHeader)){
                    return onError(exchange, HttpStatus.UNAUTHORIZED);
                }
                System.out.println(authHeader);
                serverHttpRequest = (ServerHttpRequest) exchange.getRequest()
                        .mutate()
                        .header("userIdRequest", jwtUtils.extractUserId(authHeader).toString())
                        .build();
            }
            return chain.filter(exchange.mutate().request(serverHttpRequest).build());

        });
    }

    private Mono<Void> onError(ServerWebExchange exchange,  HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }
    private boolean authMising(ServerHttpRequest serverHttpRequest){
        return !serverHttpRequest.getHeaders().containsKey("Authorization");
    }

    public static class Config {}
}
