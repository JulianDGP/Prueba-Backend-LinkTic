package com.ms.inventory.domain.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryInfo {
    private Inventory inventory;
    private Product product;
}
