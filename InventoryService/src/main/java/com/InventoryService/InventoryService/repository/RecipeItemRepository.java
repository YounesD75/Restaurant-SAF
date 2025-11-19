package com.InventoryService.InventoryService.repository;


import com.InventoryService.InventoryService.entity.RecipeItem;
import org.springframework.data.jpa.repository.JpaRepository;
import com.InventoryService.InventoryService.entity.Recipe;

import java.util.List;

public interface RecipeItemRepository extends JpaRepository<RecipeItem, Long> {
    List<RecipeItem> findByRecipe(Recipe recipe);
}
