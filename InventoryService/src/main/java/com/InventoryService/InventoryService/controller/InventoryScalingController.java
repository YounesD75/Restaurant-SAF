package com.InventoryService.InventoryService.controller;

import com.InventoryService.InventoryService.service.InventoryActorRegistry;
import com.saf.core1.router.RouterMessages;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/scaling")
public class InventoryScalingController {

    private final InventoryActorRegistry registry;

    public InventoryScalingController(InventoryActorRegistry registry) {
        this.registry = registry;
    }

    @PostMapping("/stock/up")
    public String scaleUp(@RequestParam(defaultValue = "1") int count) {
        registry.stockRouter().tell(new RouterMessages.ScaleUp(count));
        return "Demande envoyée : Ajout de " + count + " workers stock.";
    }

    @PostMapping("/stock/down")
    public String scaleDown(@RequestParam(defaultValue = "1") int count) {
        registry.stockRouter().tell(new RouterMessages.ScaleDown(count));
        return "Demande envoyée : Suppression de " + count + " workers stock.";
    }
}
