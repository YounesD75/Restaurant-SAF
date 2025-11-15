package com.InventoryService.InventoryService.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class RecipeItemRequest {

    private Long recipeId;
    private Long ingredientId;
    private Integer quantityNeeded;
}

