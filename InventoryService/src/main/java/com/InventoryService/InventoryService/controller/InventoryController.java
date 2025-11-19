package com.InventoryService.InventoryService.controller;

import com.InventoryService.InventoryService.agent.AgentManager;
import com.InventoryService.InventoryService.agent.Message;
import com.InventoryService.InventoryService.dto.StockCheckRequest;
import com.InventoryService.InventoryService.dto.StockCheckResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final AgentManager manager;

    public InventoryController(AgentManager manager) {
        this.manager = manager;
    }

    @PostMapping("/check")
    public StockCheckResponse checkStock(@RequestBody StockCheckRequest request) {

        // Envoyer vers StockAgent
        manager.send("StockAgent", new Message("VERIFY_STOCK", request));

        // Option 1 : On veut un résultat immédiat (synchrone)
        //       → On appelle directement InventoryCheckService
        //       → Comme tu faisais avant
        //
        // MAIS pour être purement microservice agent-based,
        // Option 2 : On renvoie juste "Processing" don asyncrone

        return StockCheckResponse.builder()
                .success(true)
                .message("Stock verification started")
                .build();
    }
}

