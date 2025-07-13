package com.ms.inventory.infrastructure.adapters.in.web.inventory;

import com.ms.inventory.application.dto.response.InventoryResponse;
import org.springframework.lang.NonNull;
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
    public @NonNull EntityModel<InventoryResponse> toModel(@NonNull InventoryResponse entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(InventoryController.class).getByProductId(entity.getProductId()))
                        .withSelfRel()
        );
    }


    @Override
    public @NonNull  CollectionModel<EntityModel<InventoryResponse>> toCollectionModel(@NonNull Iterable<? extends InventoryResponse> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities);
    }
}
