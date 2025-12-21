package com.InventoryService.InventoryService.config;

import com.InventoryService.InventoryService.actors.InventoryStockActor;
import com.InventoryService.InventoryService.client.InventoryCallbackClient;
import com.InventoryService.InventoryService.service.InventoryCheckService;
import com.InventoryService.InventoryService.service.InventoryActorRegistry;
import com.saf.core1.LocalActorSystem;
import com.saf.core1.SupervisionStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActorSystemConfig {

    @Bean
    public LocalActorSystem localActorSystem() {
        int threads = Math.max(4, Runtime.getRuntime().availableProcessors());
        return new LocalActorSystem(threads);
    }

    @Bean
    public InventoryActorRegistry inventoryActorRegistry(LocalActorSystem system,
                                                         InventoryCheckService checkService,
                                                         InventoryCallbackClient callbackClient) {
        return new InventoryActorRegistry(system, checkService, callbackClient);
    }

    @Bean
    public com.saf.core1.ActorRef stockActor(InventoryActorRegistry registry) {
        return registry.stockActor();
    }
}
