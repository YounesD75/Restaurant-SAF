package com.InventoryService.InventoryService.client;


import com.InventoryService.InventoryService.dto.StockCheckResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class InventoryCallbackClient {

    private final RestTemplate restTemplate;
    private final String callbackUrl;

    public InventoryCallbackClient(RestTemplate restTemplate,
                                   @Value("${saf.restaurant.url:http://localhost:8080}") String restaurantBaseUrl,
                                   @Value("${saf.restaurant.stock-callback-path:/events/stock-result}") String callbackPath) {
        this.restTemplate = restTemplate;
        this.callbackUrl = restaurantBaseUrl + callbackPath;
    }

    public void sendStockResult(StockCheckResponse response) {
        StockResultCallbackPayload payload = toPayload(response);

        try {
            restTemplate.postForEntity(callbackUrl, payload, Void.class);
            System.out.println("📨 [InventoryCallbackClient] Callback envoyé vers " + callbackUrl + " pour order " + payload.orderId());
        } catch (Exception e) {
            System.err.println("❌ [InventoryCallbackClient] Erreur callback : " + e.getMessage());
        }
    }

    private StockResultCallbackPayload toPayload(StockCheckResponse response) {
        boolean available = response.isSuccess();
        List<String> missing = available
                ? List.of()
                : (response.getMessage() == null ? List.of() : List.of(response.getMessage()));
        String orderId = response.getOrderId() == null ? null : String.valueOf(response.getOrderId());
        return new StockResultCallbackPayload(orderId, available, missing);
    }

    private record StockResultCallbackPayload(String orderId, boolean available, List<String> missingItems) {}
}
