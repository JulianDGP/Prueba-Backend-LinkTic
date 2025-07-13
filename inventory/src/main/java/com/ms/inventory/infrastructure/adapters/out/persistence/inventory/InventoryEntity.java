package com.ms.inventory.infrastructure.adapters.out.persistence.inventory;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "inventory", schema = "sales")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryEntity {
    @Id
    private Long productId;
    private Integer quantity;
    private Instant updatedAt;
}
