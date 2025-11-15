package com.InventoryService.InventoryService.dto;

import lombok.*;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class OrderResponse {
    private boolean success;
    private String message;
    private List<IngredientResponse> updatedIngredients;
}
