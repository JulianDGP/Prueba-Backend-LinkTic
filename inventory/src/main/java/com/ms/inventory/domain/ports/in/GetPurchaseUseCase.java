package com.ms.inventory.domain.ports.in;

import com.ms.inventory.domain.model.Purchase;

import java.util.Optional;

public interface GetPurchaseUseCase {
    Optional<Purchase> getById(Long id);
}
