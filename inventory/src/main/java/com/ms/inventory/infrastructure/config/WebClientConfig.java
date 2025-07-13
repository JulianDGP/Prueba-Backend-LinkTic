package com.ms.inventory.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
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
                .build();
    }
}
