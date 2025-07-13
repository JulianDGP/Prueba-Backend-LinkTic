package com.ms.inventory.domain.ports.out;

import com.ms.inventory.domain.model.Inventory;

import java.util.Optional;

public interface InventoryRepositoryPort {
    Optional<Inventory> findByProductId(Long productId);
    Inventory save(Inventory inventory);
}
