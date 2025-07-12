package com.ms.products.application.service;

import com.ms.products.domain.model.Product;
import com.ms.products.domain.ports.in.CreateProductUseCase;
import com.ms.products.domain.ports.in.GetProductUseCase;
import com.ms.products.domain.ports.in.ListProductsUseCase;
import com.ms.products.domain.ports.out.ProductRepositoryPort;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class ProductService implements CreateProductUseCase, GetProductUseCase, ListProductsUseCase {
    private final ProductRepositoryPort repositoryPort;

    @Override
    public Product create(String name, BigDecimal price, String description) {
        Product product = Product.builder()
                .name(name)
                .price(price)
                .description(description)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        return repositoryPort.save(product);
    }

    @Override
    public Optional<Product> getById(Long id) {
        return repositoryPort.findById(id);
    }

    @Override
    public List<Product> listAll() {
        return repositoryPort.findAll();
    }
}
