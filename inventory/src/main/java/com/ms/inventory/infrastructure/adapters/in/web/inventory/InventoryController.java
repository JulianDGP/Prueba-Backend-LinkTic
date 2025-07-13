package com.ms.inventory.infrastructure.adapters.in.web.inventory;

import com.ms.inventory.application.dto.request.InventoryCreateRequest;
import com.ms.inventory.application.dto.request.InventoryUpdateRequest;
import com.ms.inventory.application.dto.response.InventoryResponse;
import com.ms.inventory.application.mapper.InventoryInfoDtoMapper;
import com.ms.inventory.application.mapper.InventoryResponseMapper;
import com.ms.inventory.domain.model.Inventory;
import com.ms.inventory.domain.ports.in.AdjustInventoryUseCase;
import com.ms.inventory.domain.ports.in.CreateInventoryUseCase;
import com.ms.inventory.domain.ports.in.GetInventoryUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;

import static com.toedter.spring.hateoas.jsonapi.MediaTypes.JSON_API_VALUE;

@SecurityRequirement(name = "X-API-KEY")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/inventory", produces = JSON_API_VALUE)
public class InventoryController {
    private final GetInventoryUseCase getInventoryUc;
    private final CreateInventoryUseCase createUc;
    private final AdjustInventoryUseCase adjustInventoryUc;
    private final InventoryRepresentationModelAssembler assembler;
    private final InventoryInfoDtoMapper mapper;  // nuevo
    private final InventoryResponseMapper responseMapper;


    @Operation(
            summary = "Consulta la cantidad disponible de un producto",
            responses = {@ApiResponse(responseCode = "200", description = "Inventario encontrado", content = @Content(schema = @Schema(implementation = InventoryResponse.class))),
                    @ApiResponse(responseCode = "404", description = "No existe inventario para el producto")
            }
    )
    @GetMapping("/{productId}")
    public ResponseEntity<EntityModel<InventoryResponse>> getByProductId(@PathVariable Long productId) {
        return getInventoryUc.getByProductId(productId)
                .map(mapper::toDto)                              // paso simplificado
                .map(resp -> ResponseEntity.ok(assembler.toModel(resp)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crea el registro de inventario para un producto", requestBody = @RequestBody(description = "ID de producto y cantidad inicial", required = true, content = @Content(schema = @Schema(implementation = InventoryCreateRequest.class))),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Inventario creado", content = @Content(schema = @Schema(implementation = InventoryResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos o inventario ya existe")
            }
    )
    @PostMapping(consumes = JSON_API_VALUE)
    public ResponseEntity<EntityModel<InventoryResponse>> create(@RequestBody @Valid InventoryCreateRequest req) {
        Long productId = req.getData().getId();
        Long qty = req.getData().getAttributes().getQuantity();
        Inventory inv = createUc.create(productId, qty);
        InventoryResponse resp = responseMapper.toDto(inv);
        EntityModel<InventoryResponse> model = assembler.toModel(resp);
        URI location = model.getRequiredLink("self").toUri();
        return ResponseEntity.created(location).body(model);
    }

    @Operation(summary = "Ajusta la cantidad de inventario de un producto existente", requestBody = @RequestBody(description = "ID de producto y nueva cantidad", required = true, content = @Content(schema = @Schema(implementation = InventoryUpdateRequest.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cantidad ajustada", content = @Content(schema = @Schema(implementation = InventoryResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos o IDs no coinciden"),
                    @ApiResponse(responseCode = "404", description = "Inventario no encontrado")
            }
    )
    @PatchMapping(path = "/{productId}", consumes = JSON_API_VALUE)
    public ResponseEntity<EntityModel<InventoryResponse>> updateQuantity(@PathVariable Long productId,
                                                                         @RequestBody InventoryUpdateRequest req) {
        log.info("PATCH /inventory/{} ← payload={}", productId, req.getData().getAttributes());
        if (!req.getData().getId().equals(productId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID in path and body must match");

        Long newQty = req.getData().getAttributes().getQuantity();
        Inventory inv = adjustInventoryUc.adjustQuantity(productId, newQty);
        InventoryResponse resp = responseMapper.toDto(inv);
        return ResponseEntity.ok(assembler.toModel(resp));
    }
}
