package com.saf.restaurant.model;

import java.util.List;

public record StockResultPayload(
        Long orderId,
        boolean available,
        List<String> missingItems
) {
}
