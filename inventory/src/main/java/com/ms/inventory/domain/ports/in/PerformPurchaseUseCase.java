package com.ms.inventory.domain.ports.in;

import com.ms.inventory.domain.model.Purchase;
import com.ms.inventory.domain.exception.NotFoundException;

public interface PerformPurchaseUseCase {
    /**
     * Realiza la compra descontando stock y grabando el histórico.
     * @throws NotFoundException si no existe producto o inventario.
     * @throws IllegalArgumentException si cantidad inválida o stock insuficiente.
     */
    Purchase purchase(Long productId, Long quantity);
}
