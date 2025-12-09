package com.saf.restaurant.model;

import java.util.List;

public record StockResultPayload(
        String orderId,
        boolean available,
        List<String> missingItems
) {
}
