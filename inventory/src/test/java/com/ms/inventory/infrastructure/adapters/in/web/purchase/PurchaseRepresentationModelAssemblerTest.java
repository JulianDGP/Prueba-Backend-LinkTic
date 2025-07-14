package com.ms.inventory.infrastructure.adapters.in.web.purchase;

import static org.junit.jupiter.api.Assertions.*;
import com.ms.inventory.application.dto.response.PurchaseResponse;
import com.ms.inventory.domain.model.Purchase;
import org.junit.jupiter.api.*;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;

class PurchaseRepresentationModelAssemblerTest {

    private PurchaseRepresentationModelAssembler assembler;

    @BeforeEach
    void setUp() {
        assembler = new PurchaseRepresentationModelAssembler();

        /*  Necesario para que WebMvcLinkBuilder construya las URI. */
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServerName("localhost");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    // ---------- toModel ----------------------------------------------------

    @Test
    void toModel_addsSelfLink() {
        PurchaseResponse resp = PurchaseResponse.builder()
                .id(9L)
                .productId(3L)
                .quantity(2L)
                .unitPriceSnapshot(BigDecimal.valueOf(4.50))
                .totalAmount(BigDecimal.valueOf(9.00))
                .purchasedAt(Instant.now())
                .build();

        EntityModel<PurchaseResponse> model = assembler.toModel(resp);

        assertSame(resp, model.getContent());
        assertTrue(model.hasLink("self"));
        assertTrue(model.getRequiredLink("self").getHref()
                .endsWith("/inventory/purchase/9"));
    }

    // ---------- toCollectionModel -----------------------------------------

    @Test
    void toCollectionModel_wrapsEachEntity() {
        PurchaseResponse r1 = PurchaseResponse.builder().id(1L).build();
        PurchaseResponse r2 = PurchaseResponse.builder().id(2L).build();

        CollectionModel<EntityModel<PurchaseResponse>> col =
                assembler.toCollectionModel(Arrays.asList(r1, r2));

        assertEquals(2, col.getContent().size());
        col.getContent().forEach(model -> {
            assertNotNull(model.getContent());
            long id = model.getContent().getId();
            assertTrue(model.getRequiredLink("self").getHref()
                    .endsWith("/inventory/purchase/" + id));
        });
    }

    // ---------- toResponse -------------------------------------------------

    @Test
    void toResponse_mapsDomainToDto() {
        Purchase p = Purchase.builder()
                .id(7L)
                .productId(5L)
                .quantity(4L)
                .priceUnitSnapshot(BigDecimal.valueOf(2.25))
                .totalAmount(BigDecimal.valueOf(9.00))
                .purchasedAt(Instant.EPOCH)
                .build();

        PurchaseResponse resp = assembler.toResponse(p);

        assertEquals(p.getId(), resp.getId());
        assertEquals(p.getProductId(), resp.getProductId());
        assertEquals(p.getQuantity(), resp.getQuantity());
        assertEquals(p.getPriceUnitSnapshot(), resp.getUnitPriceSnapshot());
        assertEquals(p.getTotalAmount(), resp.getTotalAmount());
        assertEquals(p.getPurchasedAt(), resp.getPurchasedAt());
    }
}