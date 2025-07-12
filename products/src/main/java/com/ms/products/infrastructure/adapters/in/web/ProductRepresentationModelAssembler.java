package com.ms.products.infrastructure.adapters.in.web;

import com.ms.products.application.dto.ProductResponse;
import com.ms.products.domain.model.Product;
import org.springframework.hateoas.*;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Component
public class ProductRepresentationModelAssembler
        implements RepresentationModelAssembler<ProductResponse, EntityModel<ProductResponse>> {

    @Override
    public EntityModel<ProductResponse> toModel(ProductResponse resp) {
        return EntityModel.of(resp,
                linkTo(methodOn(ProductController.class).getById(resp.getId())).withSelfRel()
        );
    }

    // Helper para mapear dominio → DTO
    public ProductResponse toResponse(Product p) {
        return ProductResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .price(p.getPrice())
                .description(p.getDescription())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}
