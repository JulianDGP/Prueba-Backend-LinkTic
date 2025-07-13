package com.ms.inventory.infrastructure.adapters.out.persistence.purchase;

import com.ms.inventory.domain.model.Purchase;
import com.ms.inventory.domain.ports.out.PurchaseRepositoryPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PurchasePersistenceAdapter implements PurchaseRepositoryPort {

    private final PurchaseJpaRepository repo;


    @Override
    public Purchase save(Purchase purchase) {
        PurchaseEntity e = PurchaseEntity.builder()
                .productId(purchase.getProductId())
                .quantity(purchase.getQuantity().intValue())
                .priceUnitSnapshot(purchase.getPriceUnitSnapshot())
                .totalAmount(purchase.getTotalAmount())
                .purchasedAt(purchase.getPurchasedAt())
                .build();
        PurchaseEntity saved = repo.save(e);
        return mapToDomain(saved);
    }

    private Purchase mapToDomain(PurchaseEntity entity) {
        return Purchase.builder()
                .id(entity.getId())
                .productId(entity.getProductId())
                .quantity(entity.getQuantity().longValue())
                .priceUnitSnapshot(entity.getPriceUnitSnapshot())
                .totalAmount(entity.getTotalAmount())
                .purchasedAt(entity.getPurchasedAt())
                .build();
    }
}
