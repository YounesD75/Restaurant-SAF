package com.InventoryService.InventoryService.repository;


import com.InventoryService.InventoryService.entity.RecipeItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeItemRepository extends JpaRepository<RecipeItem, Long> {
	java.util.List<RecipeItem> findByRecipe_Id(Long recipeId);
}
