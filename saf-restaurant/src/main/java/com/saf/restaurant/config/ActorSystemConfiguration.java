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
            new MenuItem("1", "Pizza Margherita", BigDecimal.valueOf(12.50), "Tomate, Mozza, Basilic"),
            new MenuItem("2", "Burger Classique", BigDecimal.valueOf(15.00), "Boeuf, Cheddar, Oignons"),
            new MenuItem("3", "Salade César", BigDecimal.valueOf(10.00), "Poulet, Parmesan, Croutons")
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
                                       ActorRef receiptActorRef) {
        return system.spawn("restaurant",
                () -> new RestaurantActor(menuActorRef, treasuryActorRef, receiptActorRef),
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