package com.InventoryService.InventoryService.controller;

import com.InventoryService.InventoryService.dto.RecipeItemRequest;
import com.InventoryService.InventoryService.dto.RecipeItemResponse;
import com.InventoryService.InventoryService.service.RecipeItemService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipes/items")
public class RecipeItemController {

    private final RecipeItemService service;

    public RecipeItemController(RecipeItemService service) {
        this.service = service;
    }

    @PostMapping
    public RecipeItemResponse addItem(@RequestBody RecipeItemRequest request) {
        return service.addItem(request);
    }

    @GetMapping("/{recipeId}")
    public List<RecipeItemResponse> getItems(@PathVariable Long recipeId) {
        return service.getItemsByRecipe(recipeId);
    }
}

