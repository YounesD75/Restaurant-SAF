package com.InventoryService.InventoryService.controller;

import com.InventoryService.InventoryService.dto.OrderRequest;
import com.InventoryService.InventoryService.dto.OrderResponse;
import com.InventoryService.InventoryService.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/confirm")
    public ResponseEntity<OrderResponse> confirmOrder(@Valid @RequestBody OrderRequest request) {
        OrderResponse resp = orderService.confirmOrder(request);
        if (resp.isSuccess()) {
            return ResponseEntity.ok(resp);
        }
        return ResponseEntity.badRequest().body(resp);
    }
}
