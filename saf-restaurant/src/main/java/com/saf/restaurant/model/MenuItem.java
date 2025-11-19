package com.saf.restaurant.model;

import java.math.BigDecimal;

public record MenuItem(
        String sku,
        String label,
        BigDecimal price,
        String description
) {
}
