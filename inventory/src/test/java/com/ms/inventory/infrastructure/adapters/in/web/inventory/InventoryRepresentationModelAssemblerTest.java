package com.ms.inventory.infrastructure.adapters.in.web.inventory;

import static org.junit.jupiter.api.Assertions.*;
import com.ms.inventory.application.dto.response.InventoryResponse;
import org.junit.jupiter.api.*;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

class InventoryRepresentationModelAssemblerTest {

    private InventoryRepresentationModelAssembler assembler;

    @BeforeEach
    void setUp() {
        assembler = new InventoryRepresentationModelAssembler();

        /*  WebMvcLinkBuilder necesita un request activo para
            construir las URIs.  Registramos uno simulado.           */
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServerName("localhost");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void toModel_addsSelfLink() {
        InventoryResponse entity = InventoryResponse.builder()
                .productId(77L)
                .quantity(10L)
                .build();

        EntityModel<InventoryResponse> model = assembler.toModel(entity);

        // el mismo objeto queda como contenido
        assertSame(entity, model.getContent());

        assertTrue(model.hasLink("self"));
        assertTrue(model.getRequiredLink("self").getHref()
                .endsWith("/inventory/77"));
    }

    @Test
    void toCollectionModel_wrapsEachEntity() {
        InventoryResponse e1 = InventoryResponse.builder().productId(1L).build();
        InventoryResponse e2 = InventoryResponse.builder().productId(2L).build();

        CollectionModel<EntityModel<InventoryResponse>> collection =
                assembler.toCollectionModel(Arrays.asList(e1, e2));

        // hay dos modelos en la colección
        assertEquals(2, collection.getContent().size());

        // cada uno lleva su link self correcto
        collection.getContent().forEach(model -> {
            assertNotNull(model.getContent());
            Long id = model.getContent().getProductId();
            assertTrue(model.getRequiredLink("self").getHref()
                    .endsWith("/inventory/" + id));
        });
    }
}