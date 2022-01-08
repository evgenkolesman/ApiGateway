package ru.koleson.apigateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.TextCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.*;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

@Component
public class AuthHeaderFilter extends AbstractGatewayFilterFactory<AuthHeaderFilter.Config> {

    @Autowired
    Environment environment;

    public AuthHeaderFilter() {
        super(Config.class);
    }

    public static class Config {

    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            if(!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "No Authorization header",
                        HttpStatus.UNAUTHORIZED);
            }

            String authHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            String jwt = authHeader.replace("Bearer ", "");

            if(!isJwtValid(jwt)) {
                return  onError(exchange, "JWT token is not valid",
                        HttpStatus.UNAUTHORIZED);
            }
            return chain.filter(exchange);
        });
    }

    private Mono<Void> onError(ServerWebExchange exchange, String authorization, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    private boolean isJwtValid (String jwt) {
        boolean returnVal = true;
        String sub = null;
// I need to fix error here setSigningKey throwing NoClassDefFoundError
//        Claims claims = Jwts.parser()
//                .setSigningKey(environment.getProperty("token.secret"))
//                .parseClaimsJws(jwt).getBody();

// Reading Reserved Claims
//        System.out.println("Subject: " + claims.getSubject());
//        System.out.println("Expiration: " + claims.getExpiration());
// Reading Custom Claims
//        System.out.println("Role: " + claims.get("Role"));
//        System.out.println("Department: " + claims.get("Department"));
        try {
            sub = Jwts.parser()
                    .setSigningKey(environment.getProperty("token.secret"))
                    .parseClaimsJws(jwt)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            returnVal = false;
        }

        if(sub == null || sub.isEmpty()) {
            returnVal = false;
        }
//        if(jwt == null || jwt.isEmpty()) {
//            returnVal = false;
//        }
        return returnVal;
    }


}
