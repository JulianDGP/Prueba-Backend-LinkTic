package com.ms.inventory.domain.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    private Long id;
    private String name;
    private BigDecimal price;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;
}
