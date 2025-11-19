package com.InventoryService.InventoryService.dto;

import lombok.*;
import java.util.List;

//Représente une commande envoyée par OrderService :
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class StockCheckRequest {

    private Long orderId;

    private List<Item> items;

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    public static class Item {
        private String dishName;
        private Integer quantity;
    }
}
