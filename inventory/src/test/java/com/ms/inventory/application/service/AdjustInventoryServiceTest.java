package com.ms.inventory.application.service;

import com.ms.inventory.domain.model.Inventory;
import com.ms.inventory.domain.ports.out.InventoryRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdjustInventoryServiceTest {
    @Mock
    private InventoryRepositoryPort inventoryRepo;

    @InjectMocks
    private AdjustInventoryService service;

    @Captor
    private ArgumentCaptor<Inventory> invCaptor;

    private static final Long PRODUCT_ID  = 55L;
    private Inventory existingInv;

    @BeforeEach
    void setUp() {
        Instant now = Instant.now();
        existingInv = Inventory.builder()
                .productId(PRODUCT_ID )
                .quantity(20L)
                .updatedAt(now)
                .build();
    }

    @Test
    void whenNewQuantityNegative_thenThrowsIllegalArgument() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.adjustQuantity(PRODUCT_ID , -5L)
        );
        assertEquals("Inventory quantity must be >= 0", ex.getMessage());
        verifyNoInteractions(inventoryRepo);
    }

    @Test
    void whenNoInventoryFound_thenThrowsNoSuchElement() {
        when(inventoryRepo.findByProductId(PRODUCT_ID )).thenReturn(Optional.empty());

        NoSuchElementException ex = assertThrows(
                NoSuchElementException.class,
                () -> service.adjustQuantity(PRODUCT_ID , 5L)
        );
        assertEquals("No inventory found for product " + PRODUCT_ID , ex.getMessage());
        verify(inventoryRepo).findByProductId(PRODUCT_ID );
        verify(inventoryRepo, never()).save(any());
    }

    @Test
    void whenValidAdjustment_thenUpdatesQuantityAndReturnsSaved() {
        when(inventoryRepo.findByProductId(PRODUCT_ID )).thenReturn(Optional.of(existingInv));
        // simula que el save retorna el mismo inv
        when(inventoryRepo.save(any())).thenAnswer(inv -> inv.getArgument(0, Inventory.class));

        long newQty = 42L;
        Inventory result = service.adjustQuantity(PRODUCT_ID , newQty);

        // verificación de llamadas
        verify(inventoryRepo).findByProductId(PRODUCT_ID );
        verify(inventoryRepo).save(invCaptor.capture());

        // comprueba que el objeto pasó por el repo con los cambios
        Inventory toSave = invCaptor.getValue();
        assertEquals(newQty, toSave.getQuantity());
        assertNotNull(toSave.getUpdatedAt());
        assertFalse(toSave.getUpdatedAt().isAfter(Instant.now()));

        // y que el servicio devuelve exactamente lo que el repo devolvió
        assertSame(toSave, result);
    }

}