package com.InventoryService.InventoryService.repository;


import com.InventoryService.InventoryService.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
}

