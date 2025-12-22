package com.saf.restaurant.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record OrderItem(
        @NotBlank(message = "dishName is required")
        String dishName,

        @Positive(message = "quantity must be positive")
        int quantity
) {
}
