package com.ms.inventory.infrastructure.adapters.out.persistence.purchase;

import static org.junit.jupiter.api.Assertions.*;

import com.ms.inventory.domain.model.Purchase;
import org.junit.jupiter.api.*;
import org.mockito.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.mockito.Mockito.*;
class PurchasePersistenceAdapterTest {

    @Mock  PurchaseJpaRepository repo;
    AutoCloseable mocks;
    PurchasePersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        adapter = new PurchasePersistenceAdapter(repo);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    // ---------- save -------------------------------------------------------

    @Test
    void save_mapsDomainToEntity_andReturnsMappedDomain() {
        Instant now = Instant.now();

        // dominio sin ID (nuevo registro)
        Purchase in = Purchase.builder()
                .productId(5L)
                .quantity(3L)
                .priceUnitSnapshot(BigDecimal.valueOf(1.50))
                .totalAmount(BigDecimal.valueOf(4.50))
                .purchasedAt(now)
                .build();

        // entidad que simula la base de datos — ya con ID
        PurchaseEntity persisted = PurchaseEntity.builder()
                .id(10L)
                .productId(in.getProductId())
                .quantity(in.getQuantity().intValue())
                .priceUnitSnapshot(in.getPriceUnitSnapshot())
                .totalAmount(in.getTotalAmount())
                .purchasedAt(now)
                .build();

        when(repo.save(any(PurchaseEntity.class))).thenReturn(persisted);

        Purchase out = adapter.save(in);

        // ---- assert resultado ----
        assertEquals(persisted.getId(), out.getId());
        assertEquals(in.getProductId(), out.getProductId());
        assertEquals(in.getQuantity(), out.getQuantity());
        assertEquals(in.getPriceUnitSnapshot(), out.getPriceUnitSnapshot());
        assertEquals(in.getTotalAmount(), out.getTotalAmount());
        assertEquals(in.getPurchasedAt(), out.getPurchasedAt());

        // ---- capturamos la entidad que se envió a JPA ----
        ArgumentCaptor<PurchaseEntity> captor =
                ArgumentCaptor.forClass(PurchaseEntity.class);
        verify(repo).save(captor.capture());
        PurchaseEntity sent = captor.getValue();

        assertNull(sent.getId());                       // todavía sin ID
        assertEquals(in.getProductId(), sent.getProductId());
        assertEquals(in.getQuantity().intValue(), sent.getQuantity());
    }

    // ---------- findById ---------------------------------------------------

    @Test
    void findById_whenEntityPresent_returnsMappedDomain() {
        Instant when = Instant.EPOCH;

        PurchaseEntity entity = PurchaseEntity.builder()
                .id(77L)
                .productId(8L)
                .quantity(4)
                .priceUnitSnapshot(BigDecimal.valueOf(2.25))
                .totalAmount(BigDecimal.valueOf(9.00))
                .purchasedAt(when)
                .build();

        when(repo.findById(77L)).thenReturn(Optional.of(entity));

        Optional<Purchase> outOpt = adapter.findById(77L);

        assertTrue(outOpt.isPresent());
        Purchase out = outOpt.get();
        assertEquals(entity.getId(), out.getId());
        assertEquals(entity.getProductId(), out.getProductId());
        assertEquals(entity.getQuantity().longValue(), out.getQuantity());
        assertEquals(entity.getPriceUnitSnapshot(), out.getPriceUnitSnapshot());
        assertEquals(entity.getTotalAmount(), out.getTotalAmount());
        assertEquals(entity.getPurchasedAt(), out.getPurchasedAt());

        verify(repo).findById(77L);
    }

    @Test
    void findById_whenMissing_returnsEmpty() {
        when(repo.findById(123L)).thenReturn(Optional.empty());

        Optional<Purchase> out = adapter.findById(123L);

        assertTrue(out.isEmpty());
        verify(repo).findById(123L);
    }
}