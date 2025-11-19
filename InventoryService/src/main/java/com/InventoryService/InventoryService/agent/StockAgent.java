package com.InventoryService.InventoryService.agent;

import com.InventoryService.InventoryService.client.InventoryCallbackClient;
import com.InventoryService.InventoryService.dto.StockCheckRequest;
import com.InventoryService.InventoryService.dto.StockCheckResponse;
import com.InventoryService.InventoryService.service.InventoryCheckService;
import org.springframework.stereotype.Component;

@Component
public class StockAgent extends AbstractAgent {

    private final InventoryCheckService checkService;
    private final AgentManager manager;
    private final InventoryCallbackClient callbackClient;

    public StockAgent(AgentManager manager,
                      InventoryCheckService checkService,
                      InventoryCallbackClient callbackClient) {
        super("StockAgent");
        this.manager = manager;
        this.checkService = checkService;
        this.callbackClient = callbackClient;
        manager.register(this);
    }

    @Override
    protected void onMessage(Message msg) {

        switch (msg.getType()) {

            case "VERIFY_STOCK" -> {
                StockCheckRequest req = (StockCheckRequest) msg.getPayload();
                System.out.println("📦 [StockAgent] Vérification du stock pour la commande " + req.getOrderId());

                // Vérifier & déduire stock
                StockCheckResponse result = checkService.verifyAndConsumeStock(req);

                if (result.isSuccess()) {
                    System.out.println("✅ [StockAgent] Stock OK pour la commande " + req.getOrderId());
                } else {
                    System.out.println("❌ [StockAgent] Stock insuffisant: " + result.getMessage());
                }

                // AJOUT : callback vers OrderService
                StockCheckResponse callbackPayload = StockCheckResponse.builder()
                        .orderId(req.getOrderId())
                        .success(result.isSuccess())
                        .message(result.getMessage())
                        .build();

                callbackClient.sendStockResult(callbackPayload);
            }

            case "LOW_STOCK_ALERT" -> {
                String ingredientName = (String) msg.getPayload();
                System.out.println("⚠️ [ALERTE] Le stock de " + ingredientName + " est bientôt épuisé !");
            }

            default -> {
                System.out.println("⚠️ [StockAgent] Message non reconnu: " + msg.getType());
            }
        }
    }

    @Override
    public String getName() {
        return "StockAgent";
    }
}
