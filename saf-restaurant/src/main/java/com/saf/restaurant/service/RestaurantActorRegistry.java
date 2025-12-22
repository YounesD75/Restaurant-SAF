package com.saf.restaurant.service;

import com.saf.core1.ActorRef;
import com.saf.core1.LocalActorSystem;
import com.saf.core1.SupervisionStrategy;
import com.saf.restaurant.actors.ClientSessionActor;
import com.saf.restaurant.actors.MenuActor;
import com.saf.restaurant.actors.ReceiptActor;
import com.saf.restaurant.actors.RestaurantActor;
import com.saf.restaurant.actors.TreasuryActor;
import com.saf.restaurant.model.MenuItem;
import com.saf.restaurant.model.OrderAcknowledgement;
import com.saf.restaurant.model.OrderRequest;
import com.saf.restaurant.repository.ReceiptRepository;
import com.saf.restaurant.repository.TreasuryRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.CompletableFuture;

public class RestaurantActorRegistry {

    private final LocalActorSystem actorSystem;
    private final ActorRef menuActor;
    private final ActorRef treasuryActor;
    private final ActorRef receiptActor;
    private final ActorRef restaurantActor;
    private final AtomicLong orderSequence = new AtomicLong(System.currentTimeMillis());

    public RestaurantActorRegistry(LocalActorSystem actorSystem,
                                   ReceiptRepository receiptRepository,
                                   TreasuryRepository treasuryRepository) {
        this.actorSystem = actorSystem;
        this.menuActor = actorSystem.spawn("menu", () -> new MenuActor(defaultMenu()), SupervisionStrategy.RESTART);
        this.treasuryActor = actorSystem.spawn("treasury", () -> new TreasuryActor(treasuryRepository), SupervisionStrategy.RESTART);
        this.receiptActor = actorSystem.spawn("receipt", () -> new ReceiptActor(receiptRepository), SupervisionStrategy.RESTART);
        this.restaurantActor = actorSystem.spawn("restaurant",
                () -> new RestaurantActor(menuActor, treasuryActor, receiptActor),
                SupervisionStrategy.RESTART);
    }

    public ActorRef restaurantActor() {
        return restaurantActor;
    }

    public ActorRef menuActor() {
        return menuActor;
    }

    public ActorRef treasuryActor() {
        return treasuryActor;
    }

    public LocalActorSystem actorSystem() {
        return actorSystem;
    }

    public ActorRef spawnClientSession(Long orderId,
                                       OrderRequest order,
                                       CompletableFuture<OrderAcknowledgement> future) {
        ActorRef ref = actorSystem.spawn("client-" + orderId,
                () -> new ClientSessionActor(orderId, order, restaurantActor, future),
                SupervisionStrategy.RESTART);
        ref.tell(new ClientSessionActor.BeginOrder());
        return ref;
    }

    public Long nextOrderId() {
        return orderSequence.incrementAndGet();
    }

    private List<MenuItem> defaultMenu() {
        return List.of(
                new MenuItem("burger", "Burger maison", new BigDecimal("12.50"), "Pain brioché, boeuf, cheddar"),
                new MenuItem("salad", "Salade fraîcheur", new BigDecimal("9.90"), "Légumes croquants"),
                new MenuItem("dessert", "Mousse au chocolat", new BigDecimal("6.50"), "Chocolat noir 70%"),
                new MenuItem("drink", "Limonade artisanale", new BigDecimal("4.00"), "Citron, menthe"),
                new MenuItem("coffee", "Expresso", new BigDecimal("2.50"), "Arabica torréfié")
        );
    }
}
