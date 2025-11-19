package com.saf.restaurant.controller;

import com.saf.restaurant.model.OrderAcknowledgement;
import com.saf.restaurant.model.OrderRequest;
import com.saf.restaurant.service.RestaurantCommandService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final RestaurantCommandService service;

    public OrderController(RestaurantCommandService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<OrderAcknowledgement> placeOrder(@Valid @RequestBody OrderRequest request) {
        OrderAcknowledgement acknowledgement = service.placeOrder(request);
        return ResponseEntity.ok(acknowledgement);
    }
}
