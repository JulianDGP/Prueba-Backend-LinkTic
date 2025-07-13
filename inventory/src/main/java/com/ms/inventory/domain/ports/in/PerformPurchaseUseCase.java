package com.ms.inventory.domain.ports.in;

import com.ms.inventory.domain.model.Purchase;

public interface PerformPurchaseUseCase {
    Purchase purchase(Long productId, Long quantity);
}
