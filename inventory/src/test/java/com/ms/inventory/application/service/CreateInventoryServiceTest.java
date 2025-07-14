package com.ms.inventory.application.service;

import com.ms.inventory.domain.model.Inventory;
import com.ms.inventory.domain.ports.out.InventoryRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateInventoryServiceTest {
    @Mock
    private InventoryRepositoryPort repo;

    @InjectMocks
    private CreateInventoryService service;

    private final Long productId  = 99L;

    @Captor
    private ArgumentCaptor<Inventory> invCaptor;

    @BeforeEach
    void setUp() {
        // no-op
    }

    @Test
    void whenInitialQuantityNegative_thenThrows() {
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class,
                        () -> service.create(productId , -1L));
        assertEquals("Quantity must be ≥ 0", ex.getMessage());
        verifyNoInteractions(repo);
    }

    @Test
    void whenInventoryAlreadyExists_thenThrows() {
        when(repo.findByProductId(productId))
                .thenReturn(Optional.of(Inventory.builder()
                        .productId(productId)
                        .quantity(5L)
                        .updatedAt(Instant.now())
                        .build()));

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class,
                        () -> service.create(productId , 10L));
        assertTrue(ex.getMessage().contains("Inventory already exists for product " + productId ));
        verify(repo).findByProductId(productId );
        verify(repo, never()).save(any());
    }

    @Test
    void whenValid_thenCreatesAndReturnsSavedInventory() {
        // repo.findByProductId devuelve vacío
        when(repo.findByProductId(productId )).thenReturn(Optional.empty());

        // simulamos que el repo.save(...) retorna exactamente el objeto que recibe
        when(repo.save(any())).thenAnswer(invocation -> invocation.getArgument(0, Inventory.class));

        long qty = 123L;
        Inventory created = service.create(productId, qty);

        // verificamos que hay llamada a findByProductId y a save
        verify(repo).findByProductId(productId );
        verify(repo).save(invCaptor.capture());

        Inventory toSave = invCaptor.getValue();
        assertEquals(productId, toSave.getProductId());
        assertEquals(qty, toSave.getQuantity());
        // updatedAt se pone a Instant.now() — comprobamos que no sea null y sea razonable
        assertNotNull(toSave.getUpdatedAt());
        assertFalse(toSave.getUpdatedAt().isAfter(Instant.now()));

        // y que el servicio devolvió el mismo objeto
        assertSame(toSave, created);
    }
}