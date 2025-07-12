package com.ms.products.domain.ports.in;

import com.ms.products.domain.model.Product;

import java.util.Optional;

public interface GetProductUseCase {
    Optional<Product> getById(Long id);
}
