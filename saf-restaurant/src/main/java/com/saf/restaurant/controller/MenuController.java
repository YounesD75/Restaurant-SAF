package com.saf.restaurant.controller;

import com.saf.restaurant.model.MenuItem;
import com.saf.restaurant.service.RestaurantCommandService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/menu")
public class MenuController {

    private final RestaurantCommandService service;

    public MenuController(RestaurantCommandService service) {
        this.service = service;
    }

    @GetMapping
    public List<MenuItem> listMenu() {
        return service.listMenu();
    }
}
