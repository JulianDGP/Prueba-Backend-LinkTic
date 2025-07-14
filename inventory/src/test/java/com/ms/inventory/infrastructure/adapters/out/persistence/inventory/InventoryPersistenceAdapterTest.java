package com.ms.inventory.infrastructure.adapters.out.persistence.inventory;

import static org.junit.jupiter.api.Assertions.*;
import com.ms.inventory.domain.model.Inventory;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.time.Instant;
import java.util.Optional;

import static org.mockito.Mockito.*;
class InventoryPersistenceAdapterTest {

    @Mock  InventoryJpaRepository repo;
    AutoCloseable mocks;
    InventoryPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        adapter = new InventoryPersistenceAdapter(repo);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    // ---------- save -------------------------------------------------------

    @Test
    void save_mapsDomainToEntity_andReturnsMappedDomain() {
        Instant now = Instant.now();

        Inventory in = Inventory.builder()
                .productId(11L)
                .quantity(25L)
                .updatedAt(now)
                .build();

        InventoryEntity persisted = InventoryEntity.builder()
                .productId(in.getProductId())
                .quantity(in.getQuantity().intValue())
                .updatedAt(now)
                .build();

        when(repo.save(any(InventoryEntity.class))).thenReturn(persisted);

        Inventory out = adapter.save(in);

        // resultado devuelto
        assertEquals(in.getProductId(), out.getProductId());
        assertEquals(in.getQuantity(), out.getQuantity());
        assertEquals(in.getUpdatedAt(), out.getUpdatedAt());

        // entidad que viajó al repositorio
        ArgumentCaptor<InventoryEntity> captor =
                ArgumentCaptor.forClass(InventoryEntity.class);
        verify(repo).save(captor.capture());
        InventoryEntity sent = captor.getValue();

        assertEquals(in.getProductId(), sent.getProductId());
        assertEquals(in.getQuantity().intValue(), sent.getQuantity());
        assertEquals(in.getUpdatedAt(), sent.getUpdatedAt());
    }

    // ---------- findByProductId -------------------------------------------

    @Test
    void findByProductId_whenPresent_returnsMappedDomain() {
        Instant when = Instant.EPOCH;

        InventoryEntity entity = InventoryEntity.builder()
                .productId(99L)
                .quantity(7)
                .updatedAt(when)
                .build();

        when(repo.findById(99L)).thenReturn(Optional.of(entity));

        Optional<Inventory> outOpt = adapter.findByProductId(99L);

        assertTrue(outOpt.isPresent());
        Inventory out = outOpt.get();
        assertEquals(entity.getProductId(), out.getProductId());
        assertEquals(entity.getQuantity().longValue(), out.getQuantity());
        assertEquals(entity.getUpdatedAt(), out.getUpdatedAt());

        verify(repo).findById(99L);
    }

    @Test
    void findByProductId_whenMissing_returnsEmpty() {
        when(repo.findById(123L)).thenReturn(Optional.empty());

        Optional<Inventory> out = adapter.findByProductId(123L);

        assertTrue(out.isEmpty());
        verify(repo).findById(123L);
    }
}