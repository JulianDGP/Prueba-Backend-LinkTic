package com.ms.inventory.domain.ports.out;

import com.ms.inventory.domain.model.Product;

import java.util.Optional;

public interface ProductClientPort {

    /**
     * Llama a GET /products/{id} en microservicio Productos
     * @return dominio.Product dentro de Optional.empty() si 404
     */
    Optional<Product> getProductById(Long id);
}
