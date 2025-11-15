package com.InventoryService.InventoryService.service;


import com.InventoryService.InventoryService.dto.IngredientRequest;
import com.InventoryService.InventoryService.dto.IngredientResponse;
import java.util.List;

public interface IngredientService {

    IngredientResponse create(IngredientRequest request);

    List<IngredientResponse> getAll();

    IngredientResponse getById(Long id);

    IngredientResponse updateQuantity(Long id, Integer newQuantity);

    void delete(Long id);
}
