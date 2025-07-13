package com.ms.inventory.domain.ports.in;

import com.ms.inventory.domain.model.Inventory;

public interface AdjustInventoryUseCase {
    Inventory adjust(Long productId, Long newQuantity);
}
