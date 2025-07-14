package com.ms.inventory.application.service;

import com.ms.inventory.domain.exception.NotFoundException;
import com.ms.inventory.domain.model.Inventory;
import com.ms.inventory.domain.model.Product;
import com.ms.inventory.domain.model.Purchase;
import com.ms.inventory.domain.ports.out.InventoryRepositoryPort;
import com.ms.inventory.domain.ports.out.ProductClientPort;
import com.ms.inventory.domain.ports.out.PurchaseRepositoryPort;
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
class PerformPurchaseServiceTest {

    @Mock InventoryRepositoryPort invRepo;
    @Mock ProductClientPort    prodClient;
    @Mock PurchaseRepositoryPort purchaseRepo;
    @InjectMocks PerformPurchaseService service;

    private static final Long PRODUCT_ID  = 10L;
    private static final Long PURCHASE_ID = 99L;
    private Product sampleProd;
    private Inventory sampleInv;

    @BeforeEach
    void setUp() {
        Instant now = Instant.now();
        sampleProd = Product.builder()
                .id(PRODUCT_ID)
                .name("X")
                .price(BigDecimal.valueOf(2.50))
                .description("D")
                .createdAt(now)
                .updatedAt(now)
                .build();
        sampleInv = Inventory.builder()
                .productId(PRODUCT_ID)
                .quantity(5L)
                .updatedAt(now)
                .build();
    }

    @Test
    void purchase_withNonPositiveQuantity_throws() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.purchase(PRODUCT_ID, 0L)
        );
        assertEquals("Quantity must be positive", ex.getMessage());
        verifyNoInteractions(prodClient, invRepo, purchaseRepo);
    }

    @Test
    void purchase_productNotFound_throws() {
        when(prodClient.getProductById(PRODUCT_ID)).thenReturn(Optional.empty());
        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> service.purchase(PRODUCT_ID, 1L)
        );
        assertEquals("Product not found: " + PRODUCT_ID, ex.getMessage());
        verify(prodClient).getProductById(PRODUCT_ID);
        verifyNoMoreInteractions(invRepo, purchaseRepo);
    }

    @Test
    void purchase_inventoryNotFound_throws() {
        when(prodClient.getProductById(PRODUCT_ID)).thenReturn(Optional.of(sampleProd));
        when(invRepo.findByProductId(PRODUCT_ID)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> service.purchase(PRODUCT_ID, 1L)
        );
        assertEquals("Inventory not found for product: " + PRODUCT_ID, ex.getMessage());
        verify(prodClient).getProductById(PRODUCT_ID);
        verify(invRepo).findByProductId(PRODUCT_ID);
        verifyNoMoreInteractions(purchaseRepo);
    }

    @Test
    void purchase_insufficientStock_throws() {
        when(prodClient.getProductById(PRODUCT_ID)).thenReturn(Optional.of(sampleProd));
        // inventario con menos stock que la cantidad solicitada
        when(invRepo.findByProductId(PRODUCT_ID)).thenReturn(Optional.of(sampleInv));

        long tooMuch = sampleInv.getQuantity() + 1;
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.purchase(PRODUCT_ID, tooMuch)
        );
        assertEquals("Insufficient stock", ex.getMessage());
        verify(invRepo).findByProductId(PRODUCT_ID);
        verifyNoMoreInteractions(purchaseRepo);
    }

    @Test
    void purchase_success_updatesInventoryAndSavesPurchase() {
        when(prodClient.getProductById(PRODUCT_ID)).thenReturn(Optional.of(sampleProd));
        when(invRepo.findByProductId(PRODUCT_ID)).thenReturn(Optional.of(sampleInv));
        // simula que save devuelve el mismo objeto
        when(invRepo.save(any())).thenAnswer(a -> a.getArgument(0));
        when(purchaseRepo.save(any())).thenAnswer(a -> {
            Purchase pArg = a.getArgument(0);
            // asigna un id simulado
            return Purchase.builder()
                    .id(PURCHASE_ID)
                    .productId(pArg.getProductId())
                    .quantity(pArg.getQuantity())
                    .priceUnitSnapshot(pArg.getPriceUnitSnapshot())
                    .totalAmount(pArg.getTotalAmount())
                    .purchasedAt(pArg.getPurchasedAt())
                    .build();
        });

        long qty = 3L;
        Purchase result = service.purchase(PRODUCT_ID, qty);

        // la purchase devuelve la cantidad comprada
        assertEquals(qty, result.getQuantity());
        // chequea links de llamadas
        verify(invRepo).save(argThat(inv -> inv.getQuantity() == 5L - qty));

        // purchase guardada con total correcto
        assertEquals(BigDecimal.valueOf(2.50 * qty), result.getTotalAmount());
        assertEquals(PURCHASE_ID, result.getId());
    }

    @Test
    void getById_delegatesToRepo() {
        Purchase p = Purchase.builder().id(PURCHASE_ID).build();
        when(purchaseRepo.findById(PURCHASE_ID)).thenReturn(Optional.of(p));

        Optional<Purchase> opt = service.getById(PURCHASE_ID);
        assertTrue(opt.isPresent());
        assertSame(p, opt.get());

        when(purchaseRepo.findById(PURCHASE_ID)).thenReturn(Optional.empty());
        assertTrue(service.getById(PURCHASE_ID).isEmpty());
    }
}