package com.ms.inventory.infrastructure.adapters.out.http;

import com.ms.inventory.domain.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductWebClientAdapterTest {

    @Mock
    WebClient webClient;
    @Mock
    WebClient.RequestHeadersUriSpec<?> uriSpec;
    @Mock
    WebClient.RequestHeadersSpec<?> headersSpec;
    @Mock
    WebClient.ResponseSpec responseSpec;

    ProductWebClientAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ProductWebClientAdapter(webClient);

        WebClient.RequestHeadersUriSpec<?> headersUriSpec = this.uriSpec;
        WebClient.RequestHeadersSpec<?> requestHeadersSpec = this.headersSpec;

        Mockito.<WebClient.RequestHeadersUriSpec<?>>when(webClient.get())
                .thenReturn(headersUriSpec);

        Mockito.<WebClient.RequestHeadersSpec<?>>when(headersUriSpec.uri(eq("/products/{id}"), anyLong()))
                .thenReturn(requestHeadersSpec);

        Mockito.<WebClient.RequestHeadersSpec<?>>when(requestHeadersSpec.accept(MediaType.valueOf("application/vnd.api+json")))
                .thenReturn(requestHeadersSpec);

        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    }

    @Test
    void getProductById_whenRemote404_returnsEmpty() {
        // Mono mockeado (NO real) para poder encadenar stubs
        @SuppressWarnings("unchecked")
        Mono<JsonApiDocument<Product>> mono = Mockito.mock(Mono.class);

        // bodyToMono devuelve nuestro mock
        when(responseSpec.bodyToMono(
                ArgumentMatchers.<ParameterizedTypeReference<JsonApiDocument<Product>>>any()
        )).thenReturn(mono);

        // encadenamos los mismos métodos que usa el adapter
        when(mono.timeout(any(Duration.class))).thenReturn(mono);
        when(mono.retryWhen(any(Retry.class))).thenReturn(mono);

        // simulamos el .block() final devolviendo null (404 → doc == null)
        when(mono.block()).thenReturn(null);

        // ejecución
        Optional<Product> out = adapter.getProductById(123L);

        // verificación
        assertTrue(out.isEmpty());
    }

    @Test
    void getProductById_whenDocHasNoData_returnsEmpty() {
        JsonApiDocument<Product> doc = new JsonApiDocument<>();

        @SuppressWarnings("unchecked")
        Mono<JsonApiDocument<Product>> mono = Mockito.mock(Mono.class);

        // fuerza a Mockito a usar la firma bodyToMono(ParameterizedTypeReference<T>)
        when(responseSpec.bodyToMono(
                ArgumentMatchers.<ParameterizedTypeReference<JsonApiDocument<Product>>>any())
        ).thenReturn(mono);

        // encadenamos los mismos mocks que usará el adapter
        when(mono.timeout(any(Duration.class))).thenReturn(mono);
        when(mono.retryWhen(any(Retry.class))).thenReturn(mono);

        // bloquea y entrega el doc con data == null
        when(mono.block()).thenReturn(doc);

        Optional<Product> out = adapter.getProductById(123L);
        assertTrue(out.isEmpty());
    }

    @Test
    void getProductById_whenValidJsonApiDocument_mapsAndReturnsProduct() {
        // ----- preparamos el JsonApiDocument<Product> -----
        JsonApiDocument<Product> doc = new JsonApiDocument<>();
        JsonApiDocument.Data<Product> data = new JsonApiDocument.Data<>();

        Product p = Product.builder()
                .name("Foo")
                .price(java.math.BigDecimal.valueOf(9.99))
                .description("Desc")
                .build();

        // fijamos campos privados con ReflectionTestUtils
        ReflectionTestUtils.setField(data, "id", "555");
        ReflectionTestUtils.setField(data, "attributes", p);
        ReflectionTestUtils.setField(doc, "data", data);

        // ----- Mono mockeado -----
        @SuppressWarnings("unchecked")
        Mono<JsonApiDocument<Product>> mono = Mockito.mock(Mono.class);

        // bodyToMono devolverá nuestro mock
        when(responseSpec.bodyToMono(
                ArgumentMatchers.<ParameterizedTypeReference<JsonApiDocument<Product>>>any())
        ).thenReturn(mono);

        // encadenamos los métodos que el adapter invocará
        when(mono.timeout(any(Duration.class))).thenReturn(mono);
        when(mono.retryWhen(any(Retry.class))).thenReturn(mono);

        // bloquea y entrega el documento con datos válidos
        when(mono.block()).thenReturn(doc);

        // ----- ejecución y aserciones -----
        Optional<Product> out = adapter.getProductById(555L);

        assertTrue(out.isPresent());
        Product got = out.get();
        // el adapter debe haber copiado el ID y los mismos atributos
        assertEquals(555L, got.getId());
        assertSame(p, got);
    }
}