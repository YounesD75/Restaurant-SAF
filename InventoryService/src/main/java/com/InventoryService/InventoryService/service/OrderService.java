package com.InventoryService.InventoryService.service;

import com.InventoryService.InventoryService.dto.OrderRequest;
import com.InventoryService.InventoryService.dto.OrderResponse;

public interface OrderService {
    OrderResponse confirmOrder(OrderRequest request);
}
