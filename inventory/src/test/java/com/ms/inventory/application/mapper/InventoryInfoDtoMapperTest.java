package com.ms.inventory.application.mapper;

import com.ms.inventory.domain.model.Inventory;
import com.ms.inventory.domain.model.InventoryInfo;
import com.ms.inventory.domain.model.Product;
import com.ms.inventory.application.dto.response.InventoryProductResponse;
import com.ms.inventory.application.dto.response.InventoryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class InventoryInfoDtoMapperTest {


    private InventoryInfoDtoMapper mapper;
    private Inventory sampleInv;
    private Product sampleProd;
    private InventoryInfo info;

    @BeforeEach
    void setUp() {
        mapper = new InventoryInfoDtoMapper();

        Instant now = Instant.now();
        // dominio
        sampleInv = Inventory.builder()
                .productId(5L)
                .quantity(20L)
                .updatedAt(now)
                .build();
        sampleProd = Product.builder()
                .id(5L)
                .name("Widget")
                .price(BigDecimal.valueOf(123.45))
                .description("A fine widget")
                .createdAt(now.minusSeconds(60))
                .updatedAt(now)
                .build();
        info = InventoryInfo.builder()
                .inventory(sampleInv)
                .product(sampleProd)
                .build();
    }

    @Test
    void toDto_mapsAllFieldsCorrectly() {
        InventoryResponse dto = mapper.toDto(info);

        // campos de Inventario
        assertEquals(sampleInv.getProductId(), dto.getProductId());
        assertEquals(sampleInv.getQuantity(), dto.getQuantity());
        assertEquals(sampleInv.getUpdatedAt(), dto.getUpdatedAt());

        // anidado InventoryProductResponse
        InventoryProductResponse prodDto = dto.getProduct();
        assertNotNull(prodDto);
        assertEquals(sampleProd.getId(), prodDto.getId());
        assertEquals(sampleProd.getName(), prodDto.getName());
        assertEquals(sampleProd.getPrice(), prodDto.getPrice());
        assertEquals(sampleProd.getDescription(), prodDto.getDescription());
        assertEquals(sampleProd.getCreatedAt(), prodDto.getCreatedAt());
        assertEquals(sampleProd.getUpdatedAt(), prodDto.getUpdatedAt());
    }

    @Test
    void toDto_handlesMissingProductOrInventory() {
        // Inventario nulo dentro de info
        InventoryInfo infoNoInv = InventoryInfo.builder()
                .inventory(null)
                .product(sampleProd)
                .build();
        assertThrows(NullPointerException.class, () -> mapper.toDto(infoNoInv));

        // Producto nulo dentro de info
        InventoryInfo infoNoProd = InventoryInfo.builder()
                .inventory(sampleInv)
                .product(null)
                .build();
        assertThrows(NullPointerException.class, () -> mapper.toDto(infoNoProd));
    }
}