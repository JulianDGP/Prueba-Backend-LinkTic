package com.ms.inventory.application.dto.request;

import com.toedter.spring.hateoas.jsonapi.JsonApiId;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonApiTypeForClass("inventory")
public class InventoryCreateRequest {
    private InventoryCreateData data;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class InventoryCreateData {
        @JsonApiId
        private Long id;
        private Attributes attributes;
    }
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Attributes {
        private Long quantity;
    }
}
