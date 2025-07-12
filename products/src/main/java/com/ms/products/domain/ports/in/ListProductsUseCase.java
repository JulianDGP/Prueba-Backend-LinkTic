package com.ms.products.domain.ports.in;

import com.ms.products.domain.model.Product;

import java.util.List;

public interface ListProductsUseCase {
    List<Product> listAll();
}
