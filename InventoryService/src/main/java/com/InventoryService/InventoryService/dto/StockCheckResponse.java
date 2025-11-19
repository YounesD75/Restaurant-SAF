package com.InventoryService.InventoryService.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class StockCheckResponse {
    private Long orderId;
    private boolean success;
    private String message;
}
