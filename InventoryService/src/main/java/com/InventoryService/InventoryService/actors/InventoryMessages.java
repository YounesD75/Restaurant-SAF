package com.InventoryService.InventoryService.actors;

import com.saf.core1.ActorRef;
import com.saf.core1.Message;
import com.InventoryService.InventoryService.dto.StockCheckRequest;

public final class InventoryMessages {
    private InventoryMessages() {}

    public record VerifyStock(StockCheckRequest request, ActorRef replyTo) implements Message {
        @Override public String type() { return "VerifyStock"; }
        @Override public Object payload() { return request; }
    }
}
