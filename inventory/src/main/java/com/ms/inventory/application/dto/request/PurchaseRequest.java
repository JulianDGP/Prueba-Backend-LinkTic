package com.ms.inventory.application.dto.request;

import com.toedter.spring.hateoas.jsonapi.JsonApiId;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonApiTypeForClass("purchase")
public class PurchaseRequest {
    private Data data;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Data {
        @JsonApiId // ignorar o validar igual al path
        private Long id;
        private Attributes attributes;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Attributes {
        private Long quantity;
    }
}
