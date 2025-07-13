package com.ms.inventory.domain.model;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {
    private Long productId;
    private Long quantity;
    private Instant updatedAt;
}
