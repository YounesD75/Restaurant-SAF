package com.saf.restaurant.controller;

import com.saf.core1.router.RouterMessages;
import com.saf.restaurant.service.RestaurantActorRegistry;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/scaling")
public class ScalabilityController {

    private final RestaurantActorRegistry registry;

    public ScalabilityController(RestaurantActorRegistry registry) {
        this.registry = registry;
    }

    @PostMapping("/menu/up")
    public String scaleUp(@RequestParam(defaultValue = "1") int count) {
        // On récupère le routeur via le registre et on lui parle
        registry.menuActor().tell(new RouterMessages.ScaleUp(count));
        return "Demande envoyée : Ajout de " + count + " instances de MenuActor.";
    }

    @PostMapping("/menu/down")
    public String scaleDown(@RequestParam(defaultValue = "1") int count) {
        registry.menuActor().tell(new RouterMessages.ScaleDown(count));
        return "Demande envoyée : Suppression de " + count + " instances de MenuActor.";
    }
}