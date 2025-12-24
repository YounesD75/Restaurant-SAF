package com.saf.restaurant.config;

import com.saf.core1.Actor;
import com.saf.core1.ActorRef;
import com.saf.core1.LocalActorSystem;
import com.saf.core1.SupervisionStrategy;
import com.saf.core1.router.RoundRobinPool;
import com.saf.restaurant.actors.MenuActor;
import com.saf.restaurant.actors.ReceiptActor;
import com.saf.restaurant.actors.RestaurantActor;
import com.saf.restaurant.actors.TreasuryActor;
import com.saf.restaurant.client.InventoryClient;
import com.saf.restaurant.model.MenuItem;
import com.saf.restaurant.repository.ReceiptRepository;
import com.saf.restaurant.repository.TreasuryRepository;
import com.saf.restaurant.service.RestaurantActorRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Supplier;

@Configuration
public class ActorSystemConfiguration {

    @Bean
    public LocalActorSystem localActorSystem() {
        int threads = Math.max(4, Runtime.getRuntime().availableProcessors());
        return new LocalActorSystem(threads);
    }

    // --- 1. MENU (ROUTER) ---
    @Bean(name = "menuActorRef")
    public ActorRef menuActorRef(LocalActorSystem system) {
        List<MenuItem> initialMenu = List.of(
            new MenuItem("burger", "Burger maison", BigDecimal.valueOf(12.50), "Pain brioché, boeuf, cheddar"),
            new MenuItem("salad", "Salade fraîcheur", BigDecimal.valueOf(9.90), "Légumes croquants"),
            new MenuItem("dessert", "Mousse au chocolat", BigDecimal.valueOf(6.50), "Chocolat noir 70%"),
            new MenuItem("drink", "Limonade artisanale", BigDecimal.valueOf(4.00), "Citron, menthe"),
            new MenuItem("coffee", "Expresso", BigDecimal.valueOf(2.50), "Arabica torréfié")
        );

        Supplier<Actor> menuFactory = () -> new MenuActor(initialMenu);

        return system.spawn(
            "menu-router",
            () -> new RoundRobinPool(menuFactory, 3),
            SupervisionStrategy.RESTART
        );
    }

    // --- 2. TRÉSORERIE ---
    @Bean
    public ActorRef treasuryActorRef(LocalActorSystem system, TreasuryRepository repo) {
        return system.spawn("treasury", 
            () -> new TreasuryActor(repo), 
            SupervisionStrategy.RESTART);
    }

    // --- 3. REÇUS ---
    @Bean
    public ActorRef receiptActorRef(LocalActorSystem system, ReceiptRepository repo) {
        return system.spawn("receipt", 
            () -> new ReceiptActor(repo), 
            SupervisionStrategy.RESTART);
    }

    // --- 4. RESTAURANT ---
    @Bean
    public ActorRef restaurantActorRef(LocalActorSystem system,
                                       ActorRef menuActorRef,
                                       ActorRef treasuryActorRef,
                                       ActorRef receiptActorRef,
                                       InventoryClient inventoryClient) {
        return system.spawn("restaurant",
                () -> new RestaurantActor(menuActorRef, treasuryActorRef, receiptActorRef, inventoryClient),
                SupervisionStrategy.RESTART);
    }

    // --- 5. REGISTRE (Mise à jour pour inclure Treasury et Receipt) ---
    @Bean
    public RestaurantActorRegistry restaurantActorRegistry(LocalActorSystem system, 
                                                           ActorRef restaurantActorRef, 
                                                           ActorRef menuActorRef,
                                                           ActorRef treasuryActorRef, // Injecté
                                                           ActorRef receiptActorRef) { // Injecté
        // On passe TOUS les acteurs au registre
        return new RestaurantActorRegistry(
            system, 
            restaurantActorRef, 
            menuActorRef, 
            treasuryActorRef, 
            receiptActorRef
        );
    }
}
