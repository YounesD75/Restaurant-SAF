package com.InventoryService.InventoryService.client;


import com.InventoryService.InventoryService.dto.StockCheckResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class InventoryCallbackClient {

    private static final Logger log = LoggerFactory.getLogger(InventoryCallbackClient.class);

    private final RestTemplate restTemplate;
    private final String callbackUrl;

    public InventoryCallbackClient(RestTemplate restTemplate,
                                   @Value("${saf.restaurant.url:http://saf-restaurant}") String restaurantBaseUrl,
                                   @Value("${saf.restaurant.stock-callback-path:/events/stock-result}") String callbackPath) {
        this.restTemplate = restTemplate;
        this.callbackUrl = restaurantBaseUrl + callbackPath;
    }

    @CircuitBreaker(name = "restaurant", fallbackMethod = "callbackFallback")
    @Retry(name = "restaurant")
    public void sendStockResult(StockCheckResponse response) {
        StockResultCallbackPayload payload = toPayload(response);

        try {
            restTemplate.postForEntity(callbackUrl, payload, Void.class);
            log.info("Callback envoyé vers {} pour order {}", callbackUrl, payload.orderId());
        } catch (Exception e) {
            log.warn("Erreur callback vers {}: {}", callbackUrl, e.getMessage());
        }
    }

    private StockResultCallbackPayload toPayload(StockCheckResponse response) {
        boolean available = response.isSuccess();
        List<String> missing = available
                ? List.of()
                : (response.getMessage() == null ? List.of() : List.of(response.getMessage()));
        Long orderId = response.getOrderId();
        return new StockResultCallbackPayload(orderId, available, missing);
    }

    private record StockResultCallbackPayload(Long orderId, boolean available, List<String> missingItems) {}

    private void callbackFallback(StockCheckResponse response, Throwable cause) {
        log.warn("Callback indisponible pour order {}: {}", response.getOrderId(), cause.getMessage());
    }
}
