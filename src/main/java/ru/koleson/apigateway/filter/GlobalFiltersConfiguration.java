package ru.koleson.apigateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
public class GlobalFiltersConfiguration {

    Logger logger = LoggerFactory.getLogger(GlobalFiltersConfiguration.class);

    @Order(-1)
    @Bean
    public GlobalFilter secondFilter() {
        return ((exchange, chain) -> {
            logger.info("Second Prefilter executed...");
            return chain.filter(exchange).then(Mono.fromRunnable(
                    () -> {
                        logger.info("Second PostFilter is executed");
                    }
            ));
        });
    }

    @Order(3)
    @Bean
    public GlobalFilter thirdFilter() {
        return ((exchange, chain) -> {
            logger.info("Third Prefilter executed...");
            return chain.filter(exchange).then(Mono.fromRunnable(
                    () -> {
                        logger.info("Third PostFilter is executed");
                    }
            ));
        });
    }

    @Order(2)
    @Bean
    public GlobalFilter fourthFilter() {
        return ((exchange, chain) -> {
            logger.info("Fourth Prefilter executed...");
            return chain.filter(exchange).then(Mono.fromRunnable(
                    () -> {
                        logger.info("Fourth PostFilter is executed");
                    }
            ));
        });
    }
}
