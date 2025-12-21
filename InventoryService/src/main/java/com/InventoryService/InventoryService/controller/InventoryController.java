package com.InventoryService.InventoryService.controller;

import com.InventoryService.InventoryService.dto.StockCheckRequest;
import com.InventoryService.InventoryService.dto.StockCheckResponse;
import com.InventoryService.InventoryService.actors.InventoryMessages;
import com.InventoryService.InventoryService.service.InventoryActorRegistry;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryActorRegistry registry;

    public InventoryController(InventoryActorRegistry registry) {
        this.registry = registry;
    }

    @PostMapping("/check")
    public StockCheckResponse checkStock(@RequestBody StockCheckRequest request) {
        registry.stockActor().tell(new InventoryMessages.VerifyStock(request, null));
        return StockCheckResponse.builder()
                .orderId(request.getOrderId())
                .success(true)
                .message("Stock verification started via actor")
                .build();
    }
}
