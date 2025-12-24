package com.saf.client.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record ReceiptDto(
        Long orderId,
        String clientName,
        String tableNumber,
        BigDecimal total,
        List<OrderItemDto> items,
        Instant issuedAt,
        String content
) {}
