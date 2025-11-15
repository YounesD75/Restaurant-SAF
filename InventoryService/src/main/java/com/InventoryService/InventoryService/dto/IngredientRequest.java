package com.InventoryService.InventoryService.dto;


import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class IngredientRequest {

    private String name;
    private Integer quantity;
    private String unit;
    private Integer threshold;
}
