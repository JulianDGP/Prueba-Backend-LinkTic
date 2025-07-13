package com.ms.inventory.domain.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Purchase {
    private Long id;
    private Long productId;
    private Long quantity;
    private BigDecimal priceUnitSnapshot;
    private BigDecimal totalAmount;
    private Instant purchasedAt;
}
