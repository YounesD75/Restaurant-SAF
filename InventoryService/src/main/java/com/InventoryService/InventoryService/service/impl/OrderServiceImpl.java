package com.InventoryService.InventoryService.service.impl;

import com.InventoryService.InventoryService.dto.*;
import com.InventoryService.InventoryService.entity.Ingredient;
import com.InventoryService.InventoryService.entity.RecipeItem;
import com.InventoryService.InventoryService.repository.IngredientRepository;
import com.InventoryService.InventoryService.repository.RecipeItemRepository;
import com.InventoryService.InventoryService.repository.RecipeRepository;
import com.InventoryService.InventoryService.service.OrderService;
import jakarta.persistence.OptimisticLockException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final RecipeRepository recipeRepo;
    private final RecipeItemRepository recipeItemRepo;
    private final IngredientRepository ingredientRepo;
    private final TransactionTemplate txTemplate;

    public OrderServiceImpl(RecipeRepository recipeRepo,
                            RecipeItemRepository recipeItemRepo,
                            IngredientRepository ingredientRepo,
                            PlatformTransactionManager txManager) {
        this.recipeRepo = recipeRepo;
        this.recipeItemRepo = recipeItemRepo;
        this.ingredientRepo = ingredientRepo;
        this.txTemplate = new TransactionTemplate(txManager);
    }

    @Override
    public OrderResponse confirmOrder(OrderRequest request) {
        Long recipeId = request.getRecipeId();
        int times = request.getQuantity() == null ? 1 : request.getQuantity();

        final int maxRetries = 3;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                OrderResponse resp = txTemplate.execute(status -> {
                    if (!recipeRepo.existsById(recipeId)) {
                        return OrderResponse.builder()
                                .success(false)
                                .message("Recipe not found")
                                .build();
                    }

                    List<RecipeItem> items = recipeItemRepo.findByRecipe_Id(recipeId);

                    // Aggregate required quantity per ingredient id (handle duplicates)
                    Map<Long, Integer> requiredPerIngredient = new HashMap<>();
                    for (RecipeItem item : items) {
                        if (item.getIngredient() == null || item.getIngredient().getId() == null) {
                            return OrderResponse.builder().success(false).message("Recipe item has no ingredient").build();
                        }
                        long needed = (long) item.getQuantityNeeded() * times;
                        requiredPerIngredient.merge(item.getIngredient().getId(), (int) needed, Integer::sum);
                    }

                    // Check availability by loading each ingredient in the transaction (managed entities)
                    for (Map.Entry<Long, Integer> e : requiredPerIngredient.entrySet()) {
                        Long ingId = e.getKey();
                        int required = e.getValue();
                        Ingredient ing = ingredientRepo.findById(ingId)
                                .orElseThrow(() -> new RuntimeException("Ingredient not found"));
                        if (ing.getQuantity() == null || ing.getQuantity() < required) {
                            return OrderResponse.builder()
                                    .success(false)
                                    .message(String.format("Insufficient stock for ingredient '%s' (needs %d, available %d)",
                                            ing.getName(), required, ing.getQuantity() == null ? 0 : ing.getQuantity()))
                                    .build();
                        }
                    }

                    // All available: reduce stock and save
                    List<Ingredient> updated = requiredPerIngredient.entrySet().stream().map(entry -> {
                        Ingredient ing = ingredientRepo.findById(entry.getKey())
                                .orElseThrow(() -> new RuntimeException("Ingredient not found"));
                        int required = entry.getValue();
                        int newQty = ing.getQuantity() - required;
                        if (newQty < 0) {
                            // Should not happen due to previous check, but guard anyway
                            throw new RuntimeException("Negative resulting quantity");
                        }
                        ing.setQuantity(newQty);
                        return ingredientRepo.save(ing);
                    }).collect(Collectors.toList());

                    List<IngredientResponse> updatedDtos = updated.stream().map(ing ->
                            IngredientResponse.builder()
                                    .id(ing.getId())
                                    .name(ing.getName())
                                    .quantity(ing.getQuantity())
                                    .unit(ing.getUnit())
                                    .threshold(ing.getThreshold())
                                    .createdAt(ing.getCreatedAt())
                                    .updatedAt(ing.getUpdatedAt())
                                    .build()
                    ).collect(Collectors.toList());

                    return OrderResponse.builder()
                            .success(true)
                            .message("Order confirmed and stock updated")
                            .updatedIngredients(updatedDtos)
                            .build();
                });

                return resp;
            } catch (OptimisticLockException | org.springframework.dao.OptimisticLockingFailureException ex) {
                // concurrency conflict - retry
                if (attempt == maxRetries) {
                    return OrderResponse.builder().success(false).message("Concurrent update detected, please retry").build();
                }
                // else, loop to retry
            } catch (RuntimeException ex) {
                return OrderResponse.builder().success(false).message("Error: " + ex.getMessage()).build();
            }
        }

        return OrderResponse.builder().success(false).message("Unable to process order").build();
    }
}
