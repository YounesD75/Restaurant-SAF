package com.saf.client.dto;

import java.math.BigDecimal;

public record MenuItemDto(String dishName, String name, BigDecimal price, String description) {}
