package com.saf.client;

import com.saf.core1.Actor;
import com.saf.core1.ActorRef;
import com.saf.core1.LocalActorSystem;
import com.saf.core1.SupervisionStrategy;
import com.saf.core1.router.RoundRobinPool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Supplier;

@Configuration
public class ClientActorConfig {

    @Bean
    public LocalActorSystem localActorSystem() {
        int threads = Math.max(4, Runtime.getRuntime().availableProcessors());
        return new LocalActorSystem(threads);
    }

    @Bean(name = "clientRouter")
    public ActorRef clientRouter(LocalActorSystem system) {
        Supplier<Actor> workerFactory = ClientGatewayActor::new;
        return system.spawn(
                "client-router",
                () -> new RoundRobinPool(workerFactory, 3),
                SupervisionStrategy.RESTART
        );
    }
}
