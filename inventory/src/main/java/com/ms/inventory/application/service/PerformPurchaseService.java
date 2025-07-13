package com.ms.inventory.application.service;

import com.ms.inventory.domain.exception.NotFoundException;
import com.ms.inventory.domain.model.Inventory;
import com.ms.inventory.domain.model.Product;
import com.ms.inventory.domain.model.Purchase;
import com.ms.inventory.domain.ports.in.GetPurchaseUseCase;
import com.ms.inventory.domain.ports.in.PerformPurchaseUseCase;
import com.ms.inventory.domain.ports.out.InventoryRepositoryPort;
import com.ms.inventory.domain.ports.out.ProductClientPort;
import com.ms.inventory.domain.ports.out.PurchaseRepositoryPort;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class PerformPurchaseService implements PerformPurchaseUseCase, GetPurchaseUseCase {
    private final InventoryRepositoryPort invRepo;
    private final ProductClientPort prodClient;
    private final PurchaseRepositoryPort purchaseRepo;

    @Override
    @Transactional
    public Purchase purchase(Long productId, Long quantity) {
        log.info("Start purchase: productId={} quantity={}", productId, quantity);
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        // 1. Verificar producto existe y obtener precio
        Product prod = prodClient.getProductById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found: " + productId));

        // 2. Verificar inventario
        Inventory inv = invRepo.findByProductId(productId)
                .orElseThrow(() -> new NotFoundException("Inventory not found for product: " + productId));
        if (inv.getQuantity() < quantity) {
            throw new IllegalArgumentException("Insufficient stock");
        }

        // 3. Descontar stock
        inv.setQuantity(inv.getQuantity() - quantity);
        inv.setUpdatedAt(Instant.now());
        invRepo.save(inv);

        // 4. Grabar compra
        BigDecimal unitPrice = prod.getPrice();
        BigDecimal total = unitPrice.multiply(BigDecimal.valueOf(quantity));
        Purchase purchase = Purchase.builder()
                .productId(productId)
                .quantity(quantity)
                .priceUnitSnapshot(unitPrice)
                .totalAmount(total)
                .purchasedAt(Instant.now())
                .build();
        Purchase saved = purchaseRepo.save(purchase);

        log.info("End purchase: id={} total={}", saved.getId(), saved.getTotalAmount());
        return saved;
    }

    @Override
    public Optional<Purchase> getById(Long id) {
        return purchaseRepo.findById(id);
    }
}