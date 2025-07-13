package com.ms.inventory.application.mapper;

import com.ms.inventory.application.dto.response.InventoryResponse;
import com.ms.inventory.domain.model.Inventory;
import org.springframework.stereotype.Component;

@Component
public class InventoryResponseMapper {

    public InventoryResponse toDto(Inventory inv) {
        return InventoryResponse.builder()
                .productId(inv.getProductId())
                .quantity(inv.getQuantity())
                .updatedAt(inv.getUpdatedAt())
                .build();
    }
}
