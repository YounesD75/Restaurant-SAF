package com.InventoryService.InventoryService.actors;

import com.InventoryService.InventoryService.client.InventoryCallbackClient;
import com.InventoryService.InventoryService.dto.StockCheckRequest;
import com.InventoryService.InventoryService.dto.StockCheckResponse;
import com.InventoryService.InventoryService.service.InventoryCheckService;
import com.saf.core1.Actor;
import com.saf.core1.ActorContext;
import com.saf.core1.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InventoryStockActor implements Actor {

    private static final Logger log = LoggerFactory.getLogger(InventoryStockActor.class);

    private final InventoryCheckService checkService;
    private final InventoryCallbackClient callbackClient;

    public InventoryStockActor(InventoryCheckService checkService,
                               InventoryCallbackClient callbackClient) {
        this.checkService = checkService;
        this.callbackClient = callbackClient;
    }

    @Override
    public void onReceive(ActorContext ctx, Message msg) {
        if (msg instanceof InventoryMessages.VerifyStock verify) {
            handleVerify(verify.request());
        }
    }

    private void handleVerify(StockCheckRequest request) {
        log.info("Stock check start: orderId={} items={}", request.getOrderId(), request.getItems());
        StockCheckResponse result = checkService.verifyAndConsumeStock(request);
        log.info("Stock check result: orderId={} success={} message={}",
                result.getOrderId(), result.isSuccess(), result.getMessage());
        callbackClient.sendStockResult(result);
    }
}
