package com.InventoryService.InventoryService.service;

import com.InventoryService.InventoryService.actors.InventoryStockActor;
import com.InventoryService.InventoryService.client.InventoryCallbackClient;
import com.saf.core1.ActorRef;
import com.saf.core1.LocalActorSystem;
import com.saf.core1.SupervisionStrategy;

public class InventoryActorRegistry {

    private final LocalActorSystem actorSystem;
    private final ActorRef stockActor;

    public InventoryActorRegistry(LocalActorSystem actorSystem,
                                  InventoryCheckService checkService,
                                  InventoryCallbackClient callbackClient) {
        this.actorSystem = actorSystem;
        this.stockActor = actorSystem.spawn("stock",
                () -> new InventoryStockActor(checkService, callbackClient),
                SupervisionStrategy.RESTART);
    }

    public ActorRef stockActor() {
        return stockActor;
    }

    public LocalActorSystem actorSystem() {
        return actorSystem;
    }
}
