package com.ms.inventory.infrastructure.adapters.in.web.purchase;

import com.ms.inventory.application.dto.response.PurchaseResponse;
import com.ms.inventory.domain.model.Purchase;
import org.springframework.lang.NonNull;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PurchaseRepresentationModelAssembler  implements RepresentationModelAssembler<PurchaseResponse, EntityModel<PurchaseResponse>> {


    @Override
    public @NonNull EntityModel<PurchaseResponse> toModel(@NonNull PurchaseResponse resp) {
        return EntityModel.of(resp,
                linkTo(methodOn(PurchaseController.class).getById(resp.getId())).withSelfRel()
        );
    }

    @Override
    public @NonNull CollectionModel<EntityModel<PurchaseResponse>> toCollectionModel(@NonNull Iterable<? extends PurchaseResponse> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities);
    }

    public PurchaseResponse toResponse(Purchase p) {
        return PurchaseResponse.builder()
                .id(p.getId())
                .productId(p.getProductId())
                .quantity(p.getQuantity())
                .unitPriceSnapshot(p.getPriceUnitSnapshot())
                .totalAmount(p.getTotalAmount())
                .purchasedAt(p.getPurchasedAt())
                .build();
    }
}
