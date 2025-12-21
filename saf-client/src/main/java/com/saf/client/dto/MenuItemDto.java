package com.saf.client.dto;

import java.math.BigDecimal;

public record MenuItemDto(String sku, String name, BigDecimal price, String description) {}
