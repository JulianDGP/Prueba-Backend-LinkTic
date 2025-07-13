package com.ms.inventory.infrastructure.adapters.out.http;

import com.ms.inventory.domain.model.Product;
import com.ms.inventory.domain.ports.out.ProductClientPort;

import java.time.Duration;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Component
@RequiredArgsConstructor
public class ProductWebClientAdapter implements ProductClientPort {

    private final WebClient webClient;


    @Override
    public Optional<Product> getProductById(Long id) {
        JsonApiDocument<Product> doc = webClient.get()
                .uri("/products/{id}", id)
                .accept(MediaType.valueOf("application/vnd.api+json"))
                .retrieve()
                .onStatus(status -> status.value() == 404,
                        resp -> Mono.empty())
                .bodyToMono(new ParameterizedTypeReference<JsonApiDocument<Product>>() {
                })
                .timeout(Duration.ofSeconds(2))
                .retryWhen(Retry.backoff(2, Duration.ofMillis(500)))
                .block();

        if (doc == null || doc.getData() == null) return Optional.empty();
        Product attrs = doc.getData().getAttributes();
        // id en JSON:API viene como string
        attrs.setId(Long.valueOf(doc.getData().getId()));
        return Optional.of(attrs);
    }
}
