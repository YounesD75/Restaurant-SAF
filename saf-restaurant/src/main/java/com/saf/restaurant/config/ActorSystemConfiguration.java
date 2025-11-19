package com.saf.restaurant.config;

import com.saf.core1.LocalActorSystem;
import com.saf.restaurant.repository.ReceiptRepository;
import com.saf.restaurant.repository.TreasuryRepository;
import com.saf.restaurant.service.RestaurantActorRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActorSystemConfiguration {

    @Bean
    public LocalActorSystem localActorSystem() {
        int threads = Math.max(4, Runtime.getRuntime().availableProcessors());
        return new LocalActorSystem(threads);
    }

    @Bean
    public RestaurantActorRegistry restaurantActorRegistry(LocalActorSystem system,
                                                           ReceiptRepository receiptRepository,
                                                           TreasuryRepository treasuryRepository) {
        return new RestaurantActorRegistry(system, receiptRepository, treasuryRepository);
    }
}
