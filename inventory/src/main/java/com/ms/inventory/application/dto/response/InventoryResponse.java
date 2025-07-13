package com.ms.inventory.application.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.toedter.spring.hateoas.jsonapi.JsonApiId;
import lombok.*;

import java.time.Instant;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InventoryResponse {
    @JsonApiId
    private Long productId;
    private InventoryProductResponse product;
    private Long quantity;
    @JsonSerialize(using = ToStringSerializer.class)
    private Instant updatedAt;
}
