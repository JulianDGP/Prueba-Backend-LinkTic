package com.ms.inventory.domain.ports.in;

import com.ms.inventory.domain.model.Inventory;
import com.ms.inventory.domain.model.InventoryInfo;

import java.util.Optional;

public interface GetInventoryUseCase {
    Optional<InventoryInfo> getByProductId(Long productId);
}
