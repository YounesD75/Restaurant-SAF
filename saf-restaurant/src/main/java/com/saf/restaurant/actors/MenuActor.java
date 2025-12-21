package com.saf.restaurant.actors;

import com.saf.core1.Actor;
import com.saf.core1.ActorContext;
import com.saf.core1.Message;
import com.saf.restaurant.model.MenuItem;
import com.saf.restaurant.model.OrderItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MenuActor implements Actor {

    private final Map<String, MenuItem> catalog = new LinkedHashMap<>();

    public MenuActor(List<MenuItem> initialItems) {
        initialItems.forEach(item -> catalog.put(item.sku(), item));
    }

    @Override
    public void onReceive(ActorContext ctx, Message msg) {
        if (msg instanceof RestaurantMessages.ListMenu listMenu) {
            listMenu.replyTo().tell(new RestaurantMessages.MenuListed(List.copyOf(catalog.values())));
        } else if (msg instanceof RestaurantMessages.CalculatePrice calculatePrice) {
            handlePriceRequest(calculatePrice);
        }
    }

    private void handlePriceRequest(RestaurantMessages.CalculatePrice calculatePrice) {
        List<OrderItem> orderItems = calculatePrice.order().items();
        if (orderItems == null || orderItems.isEmpty()) {
            calculatePrice.replyTo().tell(new RestaurantMessages.PriceCalculated(
                    calculatePrice.orderId(), false, BigDecimal.ZERO, List.of("EMPTY_ORDER")));
            return;
        }

        List<String> missingItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (OrderItem item : orderItems) {
            MenuItem menuItem = catalog.get(item.sku());
            if (menuItem == null) {
                missingItems.add(item.sku());
                continue;
            }
            BigDecimal lineTotal = menuItem.price().multiply(BigDecimal.valueOf(item.quantity()));
            total = total.add(lineTotal);
        }

        boolean valid = true;
        calculatePrice.replyTo().tell(new RestaurantMessages.PriceCalculated(
                calculatePrice.orderId(), valid, total, missingItems));
    }
}
