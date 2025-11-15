package com.InventoryService.InventoryService.repository;


import java.util.Optional;
import com.InventoryService.InventoryService.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    Optional<Recipe> findByDishName(String dishName);
}

