package com.InventoryService.InventoryService.service.impl;

import com.InventoryService.InventoryService.dto.StockCheckRequest;
import com.InventoryService.InventoryService.dto.StockCheckResponse;
import com.InventoryService.InventoryService.entity.Ingredient;
import com.InventoryService.InventoryService.entity.Recipe;
import com.InventoryService.InventoryService.entity.RecipeItem;
import com.InventoryService.InventoryService.repository.IngredientRepository;
import com.InventoryService.InventoryService.repository.RecipeItemRepository;
import com.InventoryService.InventoryService.repository.RecipeRepository;
import com.InventoryService.InventoryService.service.InventoryCheckService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InventoryCheckServiceImpl implements InventoryCheckService {

    private final RecipeRepository recipeRepo;
    private final RecipeItemRepository recipeItemRepo;
    private final IngredientRepository ingredientRepo;

    public InventoryCheckServiceImpl(
            RecipeRepository recipeRepo,
            RecipeItemRepository recipeItemRepo,
            IngredientRepository ingredientRepo
    ) {
        this.recipeRepo = recipeRepo;
        this.recipeItemRepo = recipeItemRepo;
        this.ingredientRepo = ingredientRepo;
    }

    @Override
    @Transactional
    public StockCheckResponse verifyAndConsumeStock(StockCheckRequest request) {

        for (StockCheckRequest.Item item : request.getItems()) {

            Recipe recipe = recipeRepo.findByDishName(item.getDishName())
                    .orElseThrow(() ->
                            new RuntimeException("Unknown dish: " + item.getDishName()));

            List<RecipeItem> recipeItems = recipeItemRepo.findByRecipe(recipe);

            // Vérification stock
            for (RecipeItem ri : recipeItems) {
                Ingredient ing = ri.getIngredient();

                int required = ri.getQuantityNeeded() * item.getQuantity();

                if (ing.getQuantity() < required) {
                    return StockCheckResponse.builder()
                            .success(false)
                            .message("Not enough " + ing.getName())
                            .build();
                }
            }

            // Déduction stock
            for (RecipeItem ri : recipeItems) {
                Ingredient ing = ri.getIngredient();
                int required = ri.getQuantityNeeded() * item.getQuantity();
                ing.setQuantity(ing.getQuantity() - required);
                ingredientRepo.save(ing);
            }
        }

        return StockCheckResponse.builder()
                .success(true)
                .message("Stock reserved successfully")
                .build();
    }
}
