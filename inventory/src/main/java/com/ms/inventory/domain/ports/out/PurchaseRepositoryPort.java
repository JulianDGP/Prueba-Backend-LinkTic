package com.ms.inventory.domain.ports.out;

import com.ms.inventory.domain.model.Purchase;

import java.util.Optional;

public interface PurchaseRepositoryPort {
    Purchase save(Purchase purchase);
    Optional<Purchase> findById(Long id);

}
