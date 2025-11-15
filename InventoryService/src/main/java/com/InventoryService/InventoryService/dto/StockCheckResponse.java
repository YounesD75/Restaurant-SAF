package com.InventoryService.InventoryService.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class StockCheckResponse {

    private boolean success;
    private String message;
}
