package com.InventoryService.InventoryService.service;

import com.InventoryService.InventoryService.dto.RecipeItemRequest;
import com.InventoryService.InventoryService.dto.RecipeItemResponse;

import java.util.List;

public interface RecipeItemService {

    RecipeItemResponse addItem(RecipeItemRequest request);
    List<RecipeItemResponse> getItemsByRecipe(Long recipeId);
}

