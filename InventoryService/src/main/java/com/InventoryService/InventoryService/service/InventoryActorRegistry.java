package com.InventoryService.InventoryService.service;

import com.InventoryService.InventoryService.actors.InventoryStockActor;
import com.InventoryService.InventoryService.client.InventoryCallbackClient;
import com.saf.core1.ActorRef;
import com.saf.core1.LocalActorSystem;
import com.saf.core1.SupervisionStrategy;
import com.saf.core1.router.RoundRobinPool;

import java.util.function.Supplier;

public class InventoryActorRegistry {

    private final LocalActorSystem actorSystem;
    private final ActorRef stockRouter;

    public InventoryActorRegistry(LocalActorSystem actorSystem,
                                  InventoryCheckService checkService,
                                  InventoryCallbackClient callbackClient) {
        this.actorSystem = actorSystem;
        Supplier<com.saf.core1.Actor> workerFactory =
                () -> new InventoryStockActor(checkService, callbackClient);
        this.stockRouter = actorSystem.spawn(
                "stock-router",
                () -> new RoundRobinPool(workerFactory, 3),
                SupervisionStrategy.RESTART
        );
    }

    public ActorRef stockActor() {
        return stockRouter;
    }

    public ActorRef stockRouter() {
        return stockRouter;
    }

    public LocalActorSystem actorSystem() {
        return actorSystem;
    }
}
