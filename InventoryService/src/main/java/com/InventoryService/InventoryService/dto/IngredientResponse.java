package com.InventoryService.InventoryService.dto;

import lombok.*;
import java.time.Instant;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class IngredientResponse {

    private Long id;
    private String name;
    private Integer quantity;
    private String unit;
    private Integer threshold;
    private Instant createdAt;
    private Instant updatedAt;
}
