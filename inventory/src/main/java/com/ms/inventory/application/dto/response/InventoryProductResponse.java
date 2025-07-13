package com.ms.inventory.application.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryProductResponse {
    private Long id;
    private String name;
    private BigDecimal price;
    private String description;
    @JsonSerialize(using = ToStringSerializer.class)
    private Instant createdAt;
    @JsonSerialize(using = ToStringSerializer.class)
    private Instant updatedAt;
}
