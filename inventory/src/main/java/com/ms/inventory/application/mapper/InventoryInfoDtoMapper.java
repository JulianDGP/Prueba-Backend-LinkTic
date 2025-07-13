package com.ms.inventory.application.mapper;

import com.ms.inventory.application.dto.response.InventoryProductResponse;
import com.ms.inventory.application.dto.response.InventoryResponse;
import com.ms.inventory.domain.model.Inventory;
import com.ms.inventory.domain.model.InventoryInfo;
import org.springframework.stereotype.Component;

@Component
public class InventoryInfoDtoMapper {
    public InventoryResponse toDto(InventoryInfo info) {
        return InventoryResponse.builder()
                .productId(info.getInventory().getProductId())
                .quantity(info.getInventory().getQuantity())
                .updatedAt(info.getInventory().getUpdatedAt())
                .product(InventoryProductResponse.builder()
                        .id(info.getProduct().getId())
                        .name(info.getProduct().getName())
                        .price(info.getProduct().getPrice())
                        .description(info.getProduct().getDescription())
                        .createdAt(info.getProduct().getCreatedAt())
                        .updatedAt(info.getProduct().getUpdatedAt())
                        .build()
                )
                .build();
    }

    public InventoryResponse fromAdjusted(Inventory updated) {
        return InventoryResponse.builder()
                .productId(updated.getProductId())
                .quantity(updated.getQuantity())
                .updatedAt(updated.getUpdatedAt())
                .build();
    }
}
