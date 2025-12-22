package com.saf.client.dto;

import java.math.BigDecimal;

public record OrderAcknowledgementDto(
        Long orderId,
        String status,
        String message,
        BigDecimal amount,
        String receipt
) {}
