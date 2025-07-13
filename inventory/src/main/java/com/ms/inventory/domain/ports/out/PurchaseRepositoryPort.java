package com.ms.inventory.domain.ports.out;

import com.ms.inventory.domain.model.Purchase;

public interface PurchaseRepositoryPort {
    Purchase save(Purchase purchase);

}
