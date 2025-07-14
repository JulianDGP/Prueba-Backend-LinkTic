package com.ms.inventory.infrastructure.adapters.in.web.inventory;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ms.inventory.application.dto.request.InventoryCreateRequest;
import com.ms.inventory.application.dto.request.InventoryUpdateRequest;
import com.ms.inventory.application.dto.response.InventoryResponse;
import com.ms.inventory.application.mapper.InventoryInfoDtoMapper;
import com.ms.inventory.application.mapper.InventoryResponseMapper;
import com.ms.inventory.domain.model.Inventory;
import com.ms.inventory.domain.ports.in.AdjustInventoryUseCase;
import com.ms.inventory.domain.ports.in.CreateInventoryUseCase;
import com.ms.inventory.domain.ports.in.GetInventoryUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.hateoas.EntityModel;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import java.time.Instant;
import java.util.Optional;

import static com.toedter.spring.hateoas.jsonapi.MediaTypes.JSON_API_VALUE;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = InventoryController.class)
@AutoConfigureMockMvc(addFilters = false)   // <— deshabilita seguridad porque son tests
class InventoryControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @MockitoBean
    GetInventoryUseCase getInventoryUc;
    @MockitoBean
    CreateInventoryUseCase createUc;
    @MockitoBean
    AdjustInventoryUseCase adjustInventoryUc;
    @MockitoBean
    InventoryRepresentationModelAssembler assembler;
    @MockitoBean
    InventoryInfoDtoMapper mapper;
    @MockitoBean
    InventoryResponseMapper responseMapper;

    private static final Long ID = 7L;

    @Test
    @DisplayName("GET /inventory/{id} → 200 cuando existe")
    void getByIdFound() throws Exception {
        // preparamos el DTO y el EntityModel
        InventoryResponse dto = InventoryResponse.builder()
                .productId(ID).quantity(11L).updatedAt(Instant.now()).build();
        EntityModel<InventoryResponse> model = EntityModel.of(dto,
                // simulamos el link "self"
                org.springframework.hateoas.Link.of("/inventory/" + ID).withSelfRel()
        );

        when(getInventoryUc.getByProductId(ID)).thenReturn(Optional.of(mock(com.ms.inventory.domain.model.InventoryInfo.class)));
        when(mapper.toDto(any())).thenReturn(dto);
        when(assembler.toModel(dto)).thenReturn(model);

        mvc.perform(get("/inventory/{id}", ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_API_VALUE))
                // ahora quantity al root
                .andExpect(jsonPath("$.quantity").value(11))
                // y el link en _links.self.href
                .andExpect(jsonPath("$.links[0].href").value("/inventory/" + ID));

        verify(getInventoryUc).getByProductId(ID);
        verify(mapper).toDto(any());
        verify(assembler).toModel(dto);
    }

    @Test
    @DisplayName("GET /inventory/{id} → 404 cuando no existe")
    void getByIdNotFound() throws Exception {
        when(getInventoryUc.getByProductId(ID)).thenReturn(Optional.empty());

        mvc.perform(get("/inventory/{id}", ID))
                .andExpect(status().isNotFound());

        verify(getInventoryUc).getByProductId(ID);
        verifyNoMoreInteractions(mapper, assembler);
    }

    @Test
    @DisplayName("POST /inventory → 201 y Location")
    void createInventory() throws Exception {
        // montamos el request JSON API
        InventoryCreateRequest req = new InventoryCreateRequest();
        req.setData(new InventoryCreateRequest.InventoryCreateData());  // <— inicializa
        req.getData().setAttributes(new InventoryCreateRequest.Attributes());
        req.getData().setId(ID);
        req.getData().getAttributes().setQuantity(42L);

        Inventory inv = Inventory.builder()
                .productId(ID).quantity(42L).updatedAt(Instant.now()).build();
        InventoryResponse respDto = InventoryResponse.builder()
                .productId(ID).quantity(42L).updatedAt(inv.getUpdatedAt()).build();
        EntityModel<InventoryResponse> model = EntityModel.of(respDto,
                org.springframework.hateoas.Link.of("/inventory/" + ID).withSelfRel()
        );

        when(createUc.create(ID, 42L)).thenReturn(inv);
        when(responseMapper.toDto(inv)).thenReturn(respDto);
        when(assembler.toModel(respDto)).thenReturn(model);

        mvc.perform(post("/inventory")
                        .contentType(JSON_API_VALUE)
                        .accept(JSON_API_VALUE)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/inventory/" + ID))
                .andExpect(jsonPath("$.quantity").value(42));

        verify(createUc).create(ID, 42L);
        verify(responseMapper).toDto(inv);
        verify(assembler).toModel(respDto);
    }

    @Test
    @DisplayName("PATCH /inventory/{id} → 400 on ID mismatch")
    void patchIdMismatch() throws Exception {
        InventoryUpdateRequest req = new InventoryUpdateRequest();
        req.setData(new InventoryUpdateRequest.Data());  // <— inicializa
        req.getData().setAttributes(new InventoryUpdateRequest.Attributes());
        req.getData().setId(ID + 1);
        req.getData().getAttributes().setQuantity(10L);

        mvc.perform(patch("/inventory/{id}", ID)
                        .contentType(JSON_API_VALUE)
                        .accept(JSON_API_VALUE)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].detail")
                        .value("ID in path and body must match"));

        // no llama al use case
        verifyNoInteractions(adjustInventoryUc);
    }

    @Test
    @DisplayName("PATCH /inventory/{id} → 200 on éxito")
    void patchSuccess() throws Exception {
        InventoryUpdateRequest req = new InventoryUpdateRequest();
        req.setData(new InventoryUpdateRequest.Data());
        req.getData().setAttributes(new InventoryUpdateRequest.Attributes());

        req.getData().setId(ID);
        req.getData().getAttributes().setQuantity(99L);

        Inventory inv = Inventory.builder()
                .productId(ID).quantity(99L).updatedAt(Instant.now()).build();
        InventoryResponse respDto = InventoryResponse.builder()
                .productId(ID).quantity(99L).updatedAt(inv.getUpdatedAt()).build();
        EntityModel<InventoryResponse> model = EntityModel.of(respDto,
                org.springframework.hateoas.Link.of("/inventory/" + ID).withSelfRel()
        );

        when(adjustInventoryUc.adjustQuantity(ID, 99L)).thenReturn(inv);
        when(responseMapper.toDto(inv)).thenReturn(respDto);
        when(assembler.toModel(respDto)).thenReturn(model);

        mvc.perform(patch("/inventory/{id}", ID)
                        .contentType(JSON_API_VALUE)
                        .accept(JSON_API_VALUE)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(99))
                .andExpect(jsonPath("$.links[0].href").value("/inventory/" + ID));

        verify(adjustInventoryUc).adjustQuantity(ID, 99L);
        verify(responseMapper).toDto(inv);
        verify(assembler).toModel(respDto);
    }
}