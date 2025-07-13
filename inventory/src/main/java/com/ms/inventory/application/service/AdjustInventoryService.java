package com.ms.inventory.application.service;

import com.ms.inventory.domain.model.Inventory;
import com.ms.inventory.domain.ports.in.AdjustInventoryUseCase;
import com.ms.inventory.domain.ports.out.InventoryRepositoryPort;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class AdjustInventoryService implements AdjustInventoryUseCase {
    private final InventoryRepositoryPort inventoryRepo;

    @Override
    @Transactional
    public Inventory adjustQuantity(Long productId, Long newQuantity) {
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Inventory quantity must be >= 0");
        }
        Inventory inv = inventoryRepo.findByProductId(productId)
                .orElseThrow(() ->
                        new NoSuchElementException("No inventory found for product " + productId));
        inv.setQuantity(newQuantity);
        inv.setUpdatedAt(Instant.now());
        return inventoryRepo.save(inv);
    }
}
