package com.ms.inventory.infrastructure.adapters.in.web.purchase;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ms.inventory.application.dto.request.PurchaseRequest;
import com.ms.inventory.application.dto.response.PurchaseResponse;
import com.ms.inventory.domain.model.Purchase;
import com.ms.inventory.domain.ports.in.GetPurchaseUseCase;
import com.ms.inventory.domain.ports.in.PerformPurchaseUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.hateoas.EntityModel;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static com.toedter.spring.hateoas.jsonapi.MediaTypes.JSON_API_VALUE;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PurchaseController.class)
@AutoConfigureMockMvc(addFilters = false)  // deshabilita seguridad en tests
class PurchaseControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @MockitoBean PerformPurchaseUseCase purchaseUc;
    @MockitoBean GetPurchaseUseCase   getPurchaseUc;
    @MockitoBean PurchaseRepresentationModelAssembler assembler;

    private static final Long PRODUCT_ID  = 5L;
    private static final Long PURCHASE_ID = 77L;

    @Test
    @DisplayName("POST /inventory/{productId}/purchase → 201 y Location")
    void purchaseSuccess() throws Exception {
        // 1) Construyo el request JSON-API
        PurchaseRequest req = new PurchaseRequest();
        req.setData(new PurchaseRequest.Data());
        req.getData().setAttributes(new PurchaseRequest.Attributes());
        req.getData().getAttributes().setQuantity(3L);

        // 2) Preparo el Purchase que devuelve el use-case
        Instant now = Instant.now();
        Purchase domain = Purchase.builder()
                .id(PURCHASE_ID)
                .productId(PRODUCT_ID)
                .quantity(3L)
                .priceUnitSnapshot(BigDecimal.valueOf(10.00))
                .totalAmount(BigDecimal.valueOf(30.00))
                .purchasedAt(now)
                .build();

        // 3) DTO y modelo HATEOAS
        PurchaseResponse dto = PurchaseResponse.builder()
                .id(PURCHASE_ID)
                .productId(PRODUCT_ID)
                .quantity(3L)
                .unitPriceSnapshot(domain.getPriceUnitSnapshot())
                .totalAmount(domain.getTotalAmount())
                .purchasedAt(now)
                .build();
        EntityModel<PurchaseResponse> model = EntityModel.of(dto,
                org.springframework.hateoas.Link.of("/inventory/purchase/" + PURCHASE_ID).withSelfRel()
        );

        // 4) Mocks
        when(purchaseUc.purchase(PRODUCT_ID, 3L)).thenReturn(domain);
        when(assembler.toResponse(domain)).thenReturn(dto);
        when(assembler.toModel(dto)).thenReturn(model);

        // 5) Petición y aserciones
        mvc.perform(post("/inventory/{productId}/purchase", PRODUCT_ID)
                        .contentType(JSON_API_VALUE)
                        .accept(JSON_API_VALUE)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/inventory/purchase/" + PURCHASE_ID))
                .andExpect(jsonPath("$.quantity").value(3))
                .andExpect(jsonPath("$.totalAmount").value(30.00));

        verify(purchaseUc).purchase(PRODUCT_ID, 3L);
        verify(assembler, times(2)).toModel(dto);  // se llama una para Location, otra para body
    }

    @Test
    @DisplayName("GET /inventory/purchase/{id} → 200 cuando existe")
    void getByIdFound() throws Exception {
        Purchase domain = Purchase.builder().id(PURCHASE_ID).quantity(7L).build();
        PurchaseResponse dto = PurchaseResponse.builder().id(PURCHASE_ID).quantity(7L).build();
        EntityModel<PurchaseResponse> model = EntityModel.of(dto,
                org.springframework.hateoas.Link.of("/inventory/purchase/" + PURCHASE_ID).withSelfRel()
        );

        when(getPurchaseUc.getById(PURCHASE_ID)).thenReturn(Optional.of(domain));
        when(assembler.toResponse(domain)).thenReturn(dto);
        when(assembler.toModel(dto)).thenReturn(model);

        mvc.perform(get("/inventory/purchase/{id}", PURCHASE_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_API_VALUE))
                .andExpect(jsonPath("$.quantity").value(7))
                .andExpect(jsonPath("$.links[0].href").value("/inventory/purchase/" + PURCHASE_ID));

        verify(getPurchaseUc).getById(PURCHASE_ID);
        verify(assembler).toResponse(domain);
        verify(assembler).toModel(dto);
    }

    @Test
    @DisplayName("GET /inventory/purchase/{id} → 404 cuando no existe")
    void getByIdNotFound() throws Exception {
        when(getPurchaseUc.getById(PURCHASE_ID)).thenReturn(Optional.empty());

        mvc.perform(get("/inventory/purchase/{id}", PURCHASE_ID))
                .andExpect(status().isNotFound());

        verify(getPurchaseUc).getById(PURCHASE_ID);
        verifyNoMoreInteractions(assembler);
    }
}