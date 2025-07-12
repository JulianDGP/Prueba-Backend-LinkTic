package com.ms.products.infrastructure.adapters.in.web;

import com.ms.products.application.dto.ProductRequest;
import com.ms.products.application.dto.ProductResponse;
import com.ms.products.domain.ports.in.CreateProductUseCase;
import com.ms.products.domain.ports.in.GetProductUseCase;
import com.ms.products.domain.ports.in.ListProductsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static com.toedter.spring.hateoas.jsonapi.MediaTypes.JSON_API_VALUE;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/products", produces = JSON_API_VALUE)
public class ProductController {
    private final CreateProductUseCase createUc;
    private final GetProductUseCase getUc;
    private final ListProductsUseCase listUc;
    private final ProductRepresentationModelAssembler assembler;

    @PostMapping(consumes = JSON_API_VALUE)
    @Operation(summary = "Crear un nuevo producto", requestBody = @RequestBody(description = "Datos del producto a crear", required = true,
            content = @Content(mediaType = JSON_API_VALUE, schema = @Schema(implementation = ProductRequest.class))),
            responses = {@ApiResponse(responseCode = "201", description = "Producto creado", content = @Content(mediaType = JSON_API_VALUE, schema = @Schema(implementation = ProductResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Request inválido")
            }
    )
    public ResponseEntity<EntityModel<ProductResponse>> create(@RequestBody ProductRequest req) {
        var product = createUc.create(req.getName(), req.getPrice(), req.getDescription());
        var resp    = assembler.toResponse(product);
        var resource= assembler.toModel(resp);
        URI location = resource.getRequiredLink("self").toUri();
        return ResponseEntity.created(location).body(resource);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID",
            responses = {@ApiResponse(responseCode = "200", description = "Producto encontrado", content = @Content(mediaType = JSON_API_VALUE, schema = @Schema(implementation = ProductResponse.class))),
                    @ApiResponse(responseCode = "404", description = "No encontrado")
            }
    )
    public ResponseEntity<EntityModel<ProductResponse>> getById(@PathVariable Long id) {
        return getUc.getById(id)
                .map(p -> {
                    var resp = assembler.toResponse(p);
                    return ResponseEntity.ok(assembler.toModel(resp));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Listar todos los productos",
            responses = {@ApiResponse(responseCode = "200", description = "Listado de productos",
                    content = @Content(mediaType = JSON_API_VALUE, array = @ArraySchema(schema = @Schema(implementation = ProductResponse.class))))
            }
    )
    public CollectionModel<EntityModel<ProductResponse>> listAll() {
        return CollectionModel.of(
                listUc.listAll().stream()
                        .map(assembler::toResponse)
                        .map(assembler::toModel)
                        .toList(),
                linkTo(methodOn(ProductController.class).listAll()).withSelfRel()
        );
    }
}
