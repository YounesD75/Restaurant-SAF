package com.InventoryService.InventoryService.dto;


import lombok.*;
import java.time.Instant;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class IngredientRequest {

    private String name;
    private Integer quantity;
    private String unit;
    private Integer threshold;
}
