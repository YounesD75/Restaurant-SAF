package com.saf.restaurant.model;

import java.math.BigDecimal;

public record OrderAcknowledgement(
        Long orderId,
        OrderStatus status,
        String message,
        BigDecimal estimatedTotal,
        String receipt
) {
}
