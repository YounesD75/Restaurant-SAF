package com.saf.restaurant.client;

import java.util.List;

public record InventoryCheckRequest(Long orderId, List<Item> items) {

    public record Item(String dishName, int quantity) {}
}
