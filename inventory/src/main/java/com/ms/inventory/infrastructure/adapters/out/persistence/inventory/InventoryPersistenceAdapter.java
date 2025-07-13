package com.ms.inventory.infrastructure.adapters.out.persistence.inventory;

import com.ms.inventory.domain.model.Inventory;
import com.ms.inventory.domain.ports.out.InventoryRepositoryPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class InventoryPersistenceAdapter implements InventoryRepositoryPort {

    private final InventoryJpaRepository jpaRepository;


    @Override
    public Optional<Inventory> findByProductId(Long productId) {
        return jpaRepository.findById(productId).map(this::mapToDomain);
    }

    @Override
    public Inventory save(Inventory inventory) {
        InventoryEntity e = InventoryEntity.builder()
                .productId(inventory.getProductId())
                .quantity(inventory.getQuantity().intValue())
                .updatedAt(inventory.getUpdatedAt())
                .build();
        InventoryEntity saved = jpaRepository.save(e);
        return mapToDomain(saved);
    }

    private Inventory mapToDomain(InventoryEntity entity) {
        return Inventory.builder()
                .productId(entity.getProductId())
                .quantity(entity.getQuantity().longValue())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
