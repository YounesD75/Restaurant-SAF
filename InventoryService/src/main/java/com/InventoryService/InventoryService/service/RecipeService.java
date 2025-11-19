package com.InventoryService.InventoryService.service;

import com.InventoryService.InventoryService.dto.RecipeRequest;
import com.InventoryService.InventoryService.dto.RecipeResponse;

import java.util.List;

public interface RecipeService {
    RecipeResponse create(RecipeRequest request);
    List<RecipeResponse> getAll();
    RecipeResponse getById(Long id);
}
