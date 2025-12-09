package com.saf.restaurant.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record ReceiptDocument(
        String orderId,
        String clientName,
        String tableNumber,
        BigDecimal total,
        List<OrderItem> items,
        Instant issuedAt,
        String content
) {
}
