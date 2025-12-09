package com.saf.restaurant.model;

import java.math.BigDecimal;

public record TreasurySummary(
        BigDecimal totalRevenue,
        int settledOrders
) {
}
