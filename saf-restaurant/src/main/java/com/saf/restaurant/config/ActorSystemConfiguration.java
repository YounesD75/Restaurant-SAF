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

    // --- 1. LE MENU (SCALABLE) ---
    @Bean(name = "menuActorRef")
    public ActorRef menuActorRef(LocalActorSystem system) {
        // Liste factice pour l'exemple (à remplacer par une vraie source si besoin)
        List<MenuItem> initialMenu = List.of(
            new MenuItem("Pizza", BigDecimal.valueOf(12.50)),
            new MenuItem("Burger", BigDecimal.valueOf(15.00)),
            new MenuItem("Salade", BigDecimal.valueOf(10.00))
        );

        // La recette pour créer UN MenuActor
        Supplier<Actor> menuFactory = () -> new MenuActor(initialMenu);

        // ICI LA SCALABILITÉ : On ne crée pas un acteur, mais un POOL de 3 acteurs
        return system.spawn(
            "menu-router",
            () -> new RoundRobinPool(menuFactory, 3), // 3 workers initiaux
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

    // --- 4. LE RESTAURANT (L'ORCHESTRATEUR) ---
    @Bean
    public ActorRef restaurantActorRef(LocalActorSystem system,
                                       ActorRef menuActorRef, // Spring injecte le Router ici !
                                       ActorRef treasuryActorRef,
                                       ActorRef receiptActorRef) {
        return system.spawn("restaurant",
                // Le RestaurantActor croit parler à un seul MenuActor, mais il parle au Router
                () -> new RestaurantActor(menuActorRef, treasuryActorRef, receiptActorRef),
                SupervisionStrategy.RESTART);
    }

    // --- 5. LE REGISTRE (Mise à jour) ---
    @Bean
    public RestaurantActorRegistry restaurantActorRegistry(ActorRef restaurantActorRef, 
                                                           ActorRef menuActorRef) {
        // On passe les références déjà créées au registre
        return new RestaurantActorRegistry(restaurantActorRef, menuActorRef);
    }
}