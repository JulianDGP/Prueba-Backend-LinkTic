package com.ms.inventory.application.service;

import com.ms.inventory.domain.model.InventoryInfo;
import com.ms.inventory.domain.ports.in.GetInventoryUseCase;
import com.ms.inventory.domain.ports.out.InventoryRepositoryPort;
import com.ms.inventory.domain.ports.out.ProductClientPort;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class GetInventoryService implements GetInventoryUseCase {

    private final InventoryRepositoryPort inventoryRepo;
    private final ProductClientPort productClient;

    @Override
    public Optional<InventoryInfo> getByProductId(Long productId) {
        return inventoryRepo.findByProductId(productId)
                .flatMap(inventory ->
                        productClient.getProductById(productId)
                                .map(prod -> InventoryInfo.builder()
                                        .inventory(inventory)
                                        .product(prod)
                                        .build()
                                )
                );
    }
}
