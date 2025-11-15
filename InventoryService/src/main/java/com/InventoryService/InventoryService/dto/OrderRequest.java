package com.InventoryService.InventoryService.dto;

import lombok.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class OrderRequest {
    @NotNull
    private Long recipeId;

    @Min(1)
    private Integer quantity; // number of dishes ordered
}
