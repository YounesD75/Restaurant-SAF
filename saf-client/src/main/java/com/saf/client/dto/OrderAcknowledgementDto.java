package com.saf.client.dto;

import java.math.BigDecimal;

public record OrderAcknowledgementDto(
        String orderId,
        String status,
        String message,
        BigDecimal amount,
        String receipt
) {}
