package com.saf.restaurant.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record OrderItem(
        @NotBlank(message = "sku is required")
        String sku,

        @Positive(message = "quantity must be positive")
        int quantity
) {
}
