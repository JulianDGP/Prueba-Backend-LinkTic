package com.ms.inventory.infrastructure.adapters.in.web.purchase;


import com.ms.inventory.application.dto.request.PurchaseRequest;
import com.ms.inventory.application.dto.response.PurchaseResponse;
import com.ms.inventory.domain.model.Purchase;
import com.ms.inventory.domain.ports.in.GetPurchaseUseCase;
import com.ms.inventory.domain.ports.in.PerformPurchaseUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static com.toedter.spring.hateoas.jsonapi.MediaTypes.JSON_API_VALUE;

@SecurityRequirement(name = "X-API-KEY")
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/inventory", produces = JSON_API_VALUE)
public class PurchaseController {
    private final PerformPurchaseUseCase purchaseUc;
    private final GetPurchaseUseCase getPurchaseUc;
    private final PurchaseRepresentationModelAssembler assembler;


    @Operation(summary = "Realiza una compra de un producto",
            responses = {@ApiResponse(responseCode = "201", description = "Compra realizada con éxito", content = @Content(schema = @Schema(implementation = PurchaseResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos o cantidad insuficiente"),
                    @ApiResponse(responseCode = "404", description = "Producto o inventario no encontrado")
            }
    )
    @PostMapping(path = "/{productId}/purchase", consumes = JSON_API_VALUE)
    public ResponseEntity<EntityModel<PurchaseResponse>> purchase(
            @PathVariable Long productId,
            @RequestBody PurchaseRequest req
    ) {
        // opcional: validar req.data.id == productId
        Purchase p = purchaseUc.purchase(productId, req.getData().getAttributes().getQuantity());
        PurchaseResponse resp = assembler.toResponse(p);
        URI loc = assembler.toModel(resp).getRequiredLink("self").toUri();
        return ResponseEntity.created(loc).body(assembler.toModel(resp));
    }

    @Operation(summary = "Consulta una compra por su ID",
            responses = {@ApiResponse(responseCode = "200", description = "Compra encontrada", content = @Content(schema = @Schema(implementation = PurchaseResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Compra no encontrada")
            }
    )
    @GetMapping("/purchase/{id}")
    public ResponseEntity<EntityModel<PurchaseResponse>> getById(@PathVariable Long id) {
        return getPurchaseUc.getById(id)
                .map(p -> ResponseEntity.ok(assembler.toModel(assembler.toResponse(p))))
                .orElse(ResponseEntity.notFound().build());
    }
}
