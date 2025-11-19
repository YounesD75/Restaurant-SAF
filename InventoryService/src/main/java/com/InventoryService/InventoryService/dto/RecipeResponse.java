package com.InventoryService.InventoryService.dto;


import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class RecipeResponse {
    private Long id;
    private String dishName;
}