package com.InventoryService.InventoryService.controller;


import com.InventoryService.InventoryService.dto.RecipeRequest;
import com.InventoryService.InventoryService.dto.RecipeResponse;
import com.InventoryService.InventoryService.service.RecipeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService service;

    public RecipeController(RecipeService service) {
        this.service = service;
    }

    @PostMapping
    public RecipeResponse create(@RequestBody RecipeRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<RecipeResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public RecipeResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }
}

