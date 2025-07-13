package com.ms.inventory.application.dto.request;

import com.toedter.spring.hateoas.jsonapi.JsonApiId;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonApiTypeForClass("inventory")
public class InventoryUpdateRequest {
    private Data data;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Data {
        @JsonApiId
        private Long id;
        private Attributes attributes;
    }
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Attributes {
        private Long quantity;
    }
}
