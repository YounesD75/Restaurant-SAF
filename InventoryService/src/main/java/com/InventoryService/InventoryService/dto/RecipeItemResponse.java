package com.InventoryService.InventoryService.dto;


import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class RecipeItemResponse {

    private Long id;
    private Long recipeId;
    private Long ingredientId;
    private String ingredientName;
    private Integer quantityNeeded;
}

