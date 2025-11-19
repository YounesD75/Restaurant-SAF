package com.InventoryService.InventoryService.client;


import com.InventoryService.InventoryService.dto.StockCheckResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class InventoryCallbackClient {

    private final RestTemplate restTemplate;
    private final String callbackUrl = "http://localhost:8081/api/orders/stock/callback";

    public InventoryCallbackClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendStockResult(StockCheckResponse response) {
        try {
            restTemplate.postForEntity(callbackUrl, response, Void.class);
            System.out.println("📨 [InventoryCallbackClient] Callback envoyé pour order " + response.getOrderId());
        } catch (Exception e) {
            System.err.println("❌ [InventoryCallbackClient] Erreur callback : " + e.getMessage());
        }
    }
}

