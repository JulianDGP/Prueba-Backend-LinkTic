package com.ms.inventory.application.service;

import com.ms.inventory.domain.model.Inventory;
import com.ms.inventory.domain.model.InventoryInfo;
import com.ms.inventory.domain.model.Product;
import com.ms.inventory.domain.ports.out.InventoryRepositoryPort;
import com.ms.inventory.domain.ports.out.ProductClientPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetInventoryServiceTest {

    @Mock
    InventoryRepositoryPort inventoryRepo;

    @Mock
    ProductClientPort productClient;

    @InjectMocks
    GetInventoryService service;

    private final Long PRODUCT_ID = 42L;
    private Inventory sampleInv;
    private Product sampleProd;

    @BeforeEach
    void setUp() {
        Instant now = Instant.now();
        sampleInv = Inventory.builder()
                .productId(PRODUCT_ID)
                .quantity(10L)
                .updatedAt(now)
                .build();
        sampleProd = Product.builder()
                .id(PRODUCT_ID)
                .name("Foo")
                .price(BigDecimal.valueOf(99.99))
                .description("Desc")
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    @Test
    void whenNoInventory_thenReturnEmpty() {
        when(inventoryRepo.findByProductId(PRODUCT_ID)).thenReturn(Optional.empty());

        Optional<InventoryInfo> result = service.getByProductId(PRODUCT_ID);

        assertTrue(result.isEmpty(), "Debe devolver Optional.empty() si no hay inventario");
        verify(inventoryRepo).findByProductId(PRODUCT_ID);
        verifyNoMoreInteractions(inventoryRepo, productClient);
    }

    @Test
    void whenInventoryExistsButProductMissing_thenReturnEmpty() {
        when(inventoryRepo.findByProductId(PRODUCT_ID)).thenReturn(Optional.of(sampleInv));
        when(productClient.getProductById(PRODUCT_ID)).thenReturn(Optional.empty());

        Optional<InventoryInfo> result = service.getByProductId(PRODUCT_ID);

        assertTrue(result.isEmpty(), "Debe devolver Optional.empty() si el producto no existe");
        verify(inventoryRepo).findByProductId(PRODUCT_ID);
        verify(productClient).getProductById(PRODUCT_ID);
        verifyNoMoreInteractions(inventoryRepo, productClient);
    }

    @Test
    void whenBothExist_thenReturnInventoryInfo() {
        when(inventoryRepo.findByProductId(PRODUCT_ID)).thenReturn(Optional.of(sampleInv));
        when(productClient.getProductById(PRODUCT_ID)).thenReturn(Optional.of(sampleProd));

        Optional<InventoryInfo> opt = service.getByProductId(PRODUCT_ID);

        assertTrue(opt.isPresent(), "Debe devolver InventoryInfo cuando existe inventario y producto");
        InventoryInfo info = opt.get();
        // comprueba que mapea correctamente
        assertEquals(sampleInv, info.getInventory(),    "InventoryInfo.inventory debe ser sampleInv");
        assertEquals(sampleProd, info.getProduct(),      "InventoryInfo.product debe ser sampleProd");

        verify(inventoryRepo).findByProductId(PRODUCT_ID);
        verify(productClient).getProductById(PRODUCT_ID);
        verifyNoMoreInteractions(inventoryRepo, productClient);
    }
}