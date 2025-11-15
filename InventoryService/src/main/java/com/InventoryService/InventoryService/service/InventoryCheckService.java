package com.InventoryService.InventoryService.service;


import com.InventoryService.InventoryService.dto.StockCheckRequest;
import com.InventoryService.InventoryService.dto.StockCheckResponse;

public interface InventoryCheckService {
    StockCheckResponse verifyAndConsumeStock(StockCheckRequest request);
}

