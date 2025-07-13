package com.ms.inventory.infrastructure.adapters.out.persistence.inventory;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryJpaRepository extends JpaRepository<InventoryEntity, Long> {
}
