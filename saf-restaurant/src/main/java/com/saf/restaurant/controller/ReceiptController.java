package com.saf.restaurant.controller;

import com.saf.restaurant.model.ReceiptDocument;
import com.saf.restaurant.service.RestaurantCommandService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/receipts")
public class ReceiptController {

    private final RestaurantCommandService service;

    public ReceiptController(RestaurantCommandService service) {
        this.service = service;
    }

    @GetMapping
    public List<ReceiptDocument> list() {
        return service.receipts();
    }

    @GetMapping("/{orderId}")
    public ReceiptDocument byOrderId(@PathVariable String orderId) {
        return service.receipt(orderId);
    }
}
