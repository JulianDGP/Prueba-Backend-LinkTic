package com.ms.inventory.domain.ports.in;

import com.ms.inventory.domain.model.Inventory;

public interface AdjustInventoryUseCase {

    /**
     * Ajusta la cantidad de stock para el producto dado.
     * @param productId Id del producto.
     * @param newQuantity nueva cantidad (>=0).
     * @return entidad Inventory actualizada.
     */
    Inventory adjustQuantity(Long productId, Long newQuantity);
}
