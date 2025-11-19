package com.saf.restaurant.model;

import java.math.BigDecimal;

public record OrderAcknowledgement(
        String orderId,
        OrderStatus status,
        String message,
        BigDecimal estimatedTotal,
        String receipt
) {
}
