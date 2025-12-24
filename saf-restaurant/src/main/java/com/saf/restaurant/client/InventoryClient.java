package com.saf.restaurant.client;

import com.saf.restaurant.model.OrderItem;
import com.saf.restaurant.model.OrderRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class InventoryClient {

    private static final Logger log = LoggerFactory.getLogger(InventoryClient.class);

    private final RestTemplate restTemplate;
    private final String checkUrl;
    private final boolean enabled;

    public InventoryClient(RestTemplate restTemplate,
                           @Value("${saf.inventory.url:http://inventory-service}") String inventoryBaseUrl,
                           @Value("${saf.inventory.check-path:/api/inventory/check}") String checkPath,
                           @Value("${saf.inventory.enabled:true}") boolean enabled) {
        this.restTemplate = restTemplate;
        this.checkUrl = inventoryBaseUrl + checkPath;
        this.enabled = enabled;
    }

    public void requestStockCheck(Long orderId, OrderRequest order) {
        if (!enabled) {
            return;
        }
        List<OrderItem> items = order.items();
        if (items == null || items.isEmpty()) {
            return;
        }
        InventoryCheckRequest payload = new InventoryCheckRequest(
                orderId,
                items.stream()
                        .map(item -> new InventoryCheckRequest.Item(item.dishName(), item.quantity()))
                        .toList()
        );
        try {
            restTemplate.postForEntity(checkUrl, payload, Void.class);
            log.info("Stock check request sent to inventory for order {}", orderId);
        } catch (Exception e) {
            log.warn("Inventory stock check failed for order {}: {}", orderId, e.getMessage());
        }
    }
}
