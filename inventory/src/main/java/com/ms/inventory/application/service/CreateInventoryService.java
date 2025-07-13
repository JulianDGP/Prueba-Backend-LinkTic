package com.ms.inventory.application.service;

import com.ms.inventory.domain.model.Inventory;
import com.ms.inventory.domain.ports.in.CreateInventoryUseCase;
import com.ms.inventory.domain.ports.out.InventoryRepositoryPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@AllArgsConstructor
public class CreateInventoryService  implements CreateInventoryUseCase {
    private final InventoryRepositoryPort repo;
    @Override
    public Inventory create(Long productId, Long initialQuantity) {
        if (initialQuantity < 0) {
            throw new IllegalArgumentException("Quantity must be ≥ 0");
        }
        if (repo.findByProductId(productId).isPresent()) {
            throw new IllegalArgumentException("Inventory already exists for product " + productId);
        }
        Inventory inv = Inventory.builder()
                .productId(productId)
                .quantity(initialQuantity)
                .updatedAt(Instant.now())
                .build();
        return repo.save(inv);
    }
}
