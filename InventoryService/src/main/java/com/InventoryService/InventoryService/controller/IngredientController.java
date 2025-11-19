package com.InventoryService.InventoryService.controller;

import com.InventoryService.InventoryService.dto.IngredientRequest;
import com.InventoryService.InventoryService.dto.IngredientResponse;
import com.InventoryService.InventoryService.dto.UpdateQuantityRequest;
import com.InventoryService.InventoryService.service.IngredientService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
public class IngredientController {

    private final IngredientService service;

    public IngredientController(IngredientService service) {
        this.service = service;
    }

    @PostMapping
    public IngredientResponse create(@RequestBody IngredientRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<IngredientResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public IngredientResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PatchMapping("/{id}/quantity")
    public IngredientResponse updateQuantity(
            @PathVariable Long id,
            @RequestBody UpdateQuantityRequest request
    ) {
        return service.updateQuantity(id, request.getNewQuantity());
    }
}

