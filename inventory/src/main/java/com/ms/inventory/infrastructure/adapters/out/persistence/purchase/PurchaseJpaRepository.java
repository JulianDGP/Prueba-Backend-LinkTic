package com.ms.inventory.infrastructure.adapters.out.persistence.purchase;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseJpaRepository extends JpaRepository<PurchaseEntity, Long> {
}
