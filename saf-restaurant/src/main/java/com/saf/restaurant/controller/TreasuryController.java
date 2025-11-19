package com.saf.restaurant.controller;

import com.saf.restaurant.model.TreasurySummary;
import com.saf.restaurant.service.RestaurantCommandService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/treasury")
public class TreasuryController {

    private final RestaurantCommandService service;

    public TreasuryController(RestaurantCommandService service) {
        this.service = service;
    }

    @GetMapping
    public TreasurySummary summary() {
        return service.treasurySummary();
    }
}
