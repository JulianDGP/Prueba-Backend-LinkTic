package com.ms.products.domain.ports.in;

import com.ms.products.domain.model.Product;

import java.math.BigDecimal;

public interface CreateProductUseCase {
    Product create(String name, BigDecimal price, String description);
}
