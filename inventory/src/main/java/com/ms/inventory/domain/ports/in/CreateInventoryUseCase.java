package com.ms.inventory.domain.ports.in;

import com.ms.inventory.domain.model.Inventory;

public interface CreateInventoryUseCase {
    Inventory create(Long productId, Long initialQuantity);
}
