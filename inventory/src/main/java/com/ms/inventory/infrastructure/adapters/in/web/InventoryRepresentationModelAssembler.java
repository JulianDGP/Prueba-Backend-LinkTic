package com.ms.inventory.infrastructure.adapters.in.web;

import com.ms.inventory.application.dto.response.InventoryResponse;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class InventoryRepresentationModelAssembler
        implements RepresentationModelAssembler<InventoryResponse, EntityModel<InventoryResponse>> {


    @Override
    public EntityModel<InventoryResponse> toModel(InventoryResponse entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(InventoryController.class).getByProductId(entity.getProductId()))
                        .withSelfRel()
        );
    }


    @Override
    public CollectionModel<EntityModel<InventoryResponse>> toCollectionModel(Iterable<? extends InventoryResponse> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities);
    }
}
