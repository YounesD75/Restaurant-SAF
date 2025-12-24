package com.saf.restaurant.service;

import com.saf.core1.ActorRef;
import com.saf.core1.LocalActorSystem;
import com.saf.core1.SupervisionStrategy;
import com.saf.restaurant.actors.ClientSessionActor;
import com.saf.restaurant.model.OrderAcknowledgement;
import com.saf.restaurant.model.OrderRequest;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

public class RestaurantActorRegistry {

    private final LocalActorSystem actorSystem;
    private final ActorRef restaurantActor;
    private final ActorRef menuRouter;
    private final ActorRef treasuryActor; // <--- AJOUT
    private final ActorRef receiptActor;  // <--- AJOUT (au cas où)
    
    private final AtomicLong orderSequence = new AtomicLong(System.currentTimeMillis());

    // Constructeur mis à jour pour recevoir tous les acteurs
    public RestaurantActorRegistry(LocalActorSystem actorSystem, 
                                   ActorRef restaurantActor, 
                                   ActorRef menuRouter,
                                   ActorRef treasuryActor, // <--- AJOUT
                                   ActorRef receiptActor) { // <--- AJOUT
        this.actorSystem = actorSystem;
        this.restaurantActor = restaurantActor;
        this.menuRouter = menuRouter;
        this.treasuryActor = treasuryActor;
        this.receiptActor = receiptActor;
    }

    public ActorRef restaurantActor() {
        return restaurantActor;
    }

    public ActorRef menuActor() {
        return menuRouter;
    }
    
    // <--- C'est la méthode qui manquait et causait l'erreur
    public ActorRef treasuryActor() {
        return treasuryActor;
    }

    public ActorRef receiptActor() {
        return receiptActor;
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
}