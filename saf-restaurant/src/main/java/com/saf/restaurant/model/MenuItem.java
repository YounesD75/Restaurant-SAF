package com.saf.restaurant.model;

import java.math.BigDecimal;

public record MenuItem(
        String dishName,
        String name,
        BigDecimal price,
        String description
) {
}
