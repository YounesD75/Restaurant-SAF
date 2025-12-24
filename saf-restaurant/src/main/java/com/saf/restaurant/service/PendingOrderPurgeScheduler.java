package com.saf.restaurant.service;

import com.saf.restaurant.actors.RestaurantMessages;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class PendingOrderPurgeScheduler {

    private final RestaurantActorRegistry registry;
    private final Duration timeout;

    public PendingOrderPurgeScheduler(RestaurantActorRegistry registry,
                                      @Value("${saf.restaurant.pending-timeout:30s}") Duration timeout) {
        this.registry = registry;
        this.timeout = timeout;
    }

    @Scheduled(fixedDelayString = "${saf.restaurant.pending-scan-interval:5000}")
    public void purgeStaleOrders() {
        long cutoff = System.currentTimeMillis() - timeout.toMillis();
        registry.restaurantActor().tell(new RestaurantMessages.PurgeStaleOrders(cutoff));
    }
}
