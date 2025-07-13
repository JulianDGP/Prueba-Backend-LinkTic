package com.ms.inventory.infrastructure.adapters.in.web;

import com.ms.inventory.application.dto.request.InventoryUpdateRequest;
import com.ms.inventory.application.dto.response.InventoryProductResponse;
import com.ms.inventory.application.dto.response.InventoryResponse;
import com.ms.inventory.domain.model.InventoryInfo;
import com.ms.inventory.domain.ports.in.AdjustInventoryUseCase;
import com.ms.inventory.domain.ports.in.GetInventoryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static com.toedter.spring.hateoas.jsonapi.MediaTypes.JSON_API_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/inventory", produces = JSON_API_VALUE)
public class InventoryController {
    private final GetInventoryUseCase getInventoryUc;
    private final InventoryRepresentationModelAssembler assembler;
    private final AdjustInventoryUseCase adjustInventoryUc;

    @GetMapping("/{productId}")
    public ResponseEntity<EntityModel<InventoryResponse>> getByProductId(@PathVariable Long productId) {
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

    @PatchMapping(path = "/{productId}", consumes = JSON_API_VALUE)
    public ResponseEntity<EntityModel<InventoryResponse>> updateQuantity(@PathVariable Long productId,
                                                                         @RequestBody InventoryUpdateRequest req) {

        if (!req.getData().getId().equals(productId)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID in path and body must match");

        Long newQty = req.getData().getAttributes().getQuantity();
        var updated = adjustInventoryUc.adjustQuantity(productId, newQty);
        // Mapeo manual a DTO
        InventoryResponse dto = InventoryResponse.builder()
                .productId(updated.getProductId())
                .quantity(updated.getQuantity())
                .updatedAt(updated.getUpdatedAt())
                // Nota: para este endpoint no se incluye datos de producto dado que no es necesario
                .build();
        return ResponseEntity.ok(assembler.toModel(dto));
    }
}
