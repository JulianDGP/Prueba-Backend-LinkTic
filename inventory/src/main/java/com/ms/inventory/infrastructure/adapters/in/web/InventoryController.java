package com.ms.inventory.infrastructure.adapters.in.web;

import com.ms.inventory.application.dto.response.InventoryProductResponse;
import com.ms.inventory.application.dto.response.InventoryResponse;
import com.ms.inventory.domain.model.InventoryInfo;
import com.ms.inventory.domain.ports.in.GetInventoryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.toedter.spring.hateoas.jsonapi.MediaTypes.JSON_API_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/inventory", produces = JSON_API_VALUE)
public class InventoryController {
    private final GetInventoryUseCase getInventoryUc;
    private final InventoryRepresentationModelAssembler assembler;

    @GetMapping("/{productId}")
    public ResponseEntity<EntityModel<InventoryResponse>> getByProductId(
            @PathVariable Long productId
    ) {
        Optional<InventoryInfo> info = getInventoryUc.getByProductId(productId);
        return info.map(i -> {
            InventoryResponse dto = InventoryResponse.builder()
                    .productId(i.getInventory().getProductId())
                    .quantity(i.getInventory().getQuantity())
                    .updatedAt(i.getInventory().getUpdatedAt())
                    .product(InventoryProductResponse.builder()
                            .id(i.getProduct().getId())
                            .name(i.getProduct().getName())
                            .price(i.getProduct().getPrice())
                            .description(i.getProduct().getDescription())
                            .createdAt(i.getProduct().getCreatedAt())
                            .updatedAt(i.getProduct().getUpdatedAt())
                            .build())
                    .build();
            return ResponseEntity.ok(assembler.toModel(dto));
        }).orElse(ResponseEntity.notFound().build());
    }
}
