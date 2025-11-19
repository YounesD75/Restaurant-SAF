package com.InventoryService.InventoryService.service.impl;

import com.InventoryService.InventoryService.dto.RecipeItemRequest;
import com.InventoryService.InventoryService.dto.RecipeItemResponse;
import com.InventoryService.InventoryService.entity.Ingredient;
import com.InventoryService.InventoryService.entity.Recipe;
import com.InventoryService.InventoryService.entity.RecipeItem;
import com.InventoryService.InventoryService.repository.IngredientRepository;
import com.InventoryService.InventoryService.repository.RecipeItemRepository;
import com.InventoryService.InventoryService.repository.RecipeRepository;
import com.InventoryService.InventoryService.service.RecipeItemService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecipeItemServiceImpl implements RecipeItemService {

    private final RecipeRepository recipeRepo;
    private final IngredientRepository ingredientRepo;
    private final RecipeItemRepository repo;

    public RecipeItemServiceImpl(
            RecipeRepository recipeRepo,
            IngredientRepository ingredientRepo,
            RecipeItemRepository repo
    ) {
        this.recipeRepo = recipeRepo;
        this.ingredientRepo = ingredientRepo;
        this.repo = repo;
    }

    @Override
    public RecipeItemResponse addItem(RecipeItemRequest request) {

        Recipe recipe = recipeRepo.findById(request.getRecipeId())
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        Ingredient ingredient = ingredientRepo.findById(request.getIngredientId())
                .orElseThrow(() -> new RuntimeException("Ingredient not found"));

        RecipeItem item = RecipeItem.builder()
                .recipe(recipe)
                .ingredient(ingredient)
                .quantityNeeded(request.getQuantityNeeded())
                .build();

        return mapToResponse(repo.save(item));
    }

    @Override
    public List<RecipeItemResponse> getItemsByRecipe(Long recipeId) {
        Recipe recipe = recipeRepo.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        return repo.findByRecipe(recipe)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private RecipeItemResponse mapToResponse(RecipeItem item) {
        return RecipeItemResponse.builder()
                .id(item.getId())
                .recipeId(item.getRecipe().getId())
                .ingredientId(item.getIngredient().getId())
                .ingredientName(item.getIngredient().getName())
                .quantityNeeded(item.getQuantityNeeded())
                .build();
    }
}

