package com.ms.inventory.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
@Slf4j
public class WebClientConfig {

    @Bean
    public WebClient productsWebClient(
            WebClient.Builder builder,
            @Value("${products.service.url}") String baseUrl,
            @Value("${inventory.api.key}") String apiKey
    ) {
        return builder
                .baseUrl(baseUrl)
                .defaultHeader("X-API-KEY", apiKey)
                // log request
                .filter(ExchangeFilterFunction.ofRequestProcessor(req -> {
                    log.info(">> [{}] {}", req.method(), req.url());
                    req.headers().forEach((n, v) -> log.info(">> {}={}", n, v));
                    return Mono.just(req);
                }))
                // log response
                .filter(ExchangeFilterFunction.ofResponseProcessor(res -> {
                    log.info("<< status {}", res.statusCode());
                    res.headers().asHttpHeaders().forEach((n, v) -> log.info("<< {}={}", n, v));
                    return Mono.just(res);
                }))
                .build();
    }
}
