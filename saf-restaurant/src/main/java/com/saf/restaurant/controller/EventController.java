package com.saf.restaurant.controller;

import com.saf.restaurant.model.StockResultPayload;
import com.saf.restaurant.service.RestaurantCommandService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/events")
public class EventController {

    private final RestaurantCommandService service;

    public EventController(RestaurantCommandService service) {
        this.service = service;
    }

    @PostMapping("/stock-result")
    public ResponseEntity<Void> consumeStockResult(@RequestBody StockResultPayload payload) {
        service.handleStockResult(payload);
        return ResponseEntity.accepted().build();
    }
}
