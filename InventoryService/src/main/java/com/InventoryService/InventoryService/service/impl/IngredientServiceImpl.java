package com.InventoryService.InventoryService.service.impl;


import com.InventoryService.InventoryService.dto.IngredientRequest;
import com.InventoryService.InventoryService.dto.IngredientResponse;
import com.InventoryService.InventoryService.entity.Ingredient;
import com.InventoryService.InventoryService.repository.IngredientRepository;
import com.InventoryService.InventoryService.service.IngredientService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IngredientServiceImpl implements IngredientService {

    private final IngredientRepository repo;

    public IngredientServiceImpl(IngredientRepository repo) {
        this.repo = repo;
    }

    @Override
    public IngredientResponse create(IngredientRequest request) {
        Ingredient ingredient = Ingredient.builder()
                .name(request.getName())
                .quantity(request.getQuantity())
                .unit(request.getUnit())
                .threshold(request.getThreshold())
                .build();

        Ingredient saved = repo.save(ingredient);
        return mapToResponse(saved);
    }

    @Override
    public List<IngredientResponse> getAll() {
        return repo.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public IngredientResponse getById(Long id) {
        Ingredient ing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingredient not found"));
        return mapToResponse(ing);
    }

    @Override
    public IngredientResponse updateQuantity(Long id, Integer newQuantity) {
        Ingredient ing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingredient not found"));
        if (newQuantity == null || newQuantity < 0) {
            throw new IllegalArgumentException("Quantity must be >= 0");
        }

        ing.setQuantity(newQuantity);
        Ingredient saved = repo.save(ing);

        return mapToResponse(saved);
    }

    @Override
    public void delete(Long id) {
        Ingredient ing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingredient not found"));
        repo.delete(ing);
    }

    // ====================
    // Mapping entity -> DTO
    // ====================
    private IngredientResponse mapToResponse(Ingredient ingredient) {
        return IngredientResponse.builder()
                .id(ingredient.getId())
                .name(ingredient.getName())
                .quantity(ingredient.getQuantity())
                .unit(ingredient.getUnit())
                .threshold(ingredient.getThreshold())
                .createdAt(ingredient.getCreatedAt())
                .updatedAt(ingredient.getUpdatedAt())
                .build();
    }
}

