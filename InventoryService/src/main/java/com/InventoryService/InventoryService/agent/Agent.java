package com.InventoryService.InventoryService.agent;

public interface Agent {
    String getName();
    void receive(Message message);
}

