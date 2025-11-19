package com.InventoryService.InventoryService.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class UpdateQuantityRequest {
    private Integer newQuantity;
}

