package com.saf.restaurant.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record OrderRequest(
        @NotBlank(message = "clientName is required")
        String clientName,

        String tableNumber,

        String instructions,

        @NotEmpty(message = "items are required")
        @Valid
        List<OrderItem> items
) {
}
