package com.InventoryService.InventoryService.service.impl;

import com.InventoryService.InventoryService.dto.RecipeRequest;
import com.InventoryService.InventoryService.dto.RecipeResponse;
import com.InventoryService.InventoryService.entity.Recipe;
import com.InventoryService.InventoryService.repository.RecipeRepository;
import com.InventoryService.InventoryService.service.RecipeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository repo;

    public RecipeServiceImpl(RecipeRepository repo) {
        this.repo = repo;
    }

    @Override
    public RecipeResponse create(RecipeRequest request) {
        Recipe recipe = Recipe.builder()
                .dishName(request.getDishName())
                .build();

        return mapToResponse(repo.save(recipe));
    }

    @Override
    public List<RecipeResponse> getAll() {
        return repo.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RecipeResponse getById(Long id) {
        Recipe r = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));
        return mapToResponse(r);
    }


    private RecipeResponse mapToResponse(Recipe recipe) {
        return RecipeResponse.builder()
                .id(recipe.getId())
                .dishName(recipe.getDishName())
                .build();
    }
}

