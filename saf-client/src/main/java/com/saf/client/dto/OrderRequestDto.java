package com.saf.client.dto;

import java.util.List;

public record OrderRequestDto(
        String clientName,
        String tableNumber,
        String instructions,
        List<OrderItemDto> items
) {}
