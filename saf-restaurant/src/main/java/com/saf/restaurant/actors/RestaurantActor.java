package com.saf.restaurant.actors;

import com.saf.core1.Actor;
import com.saf.core1.ActorContext;
import com.saf.core1.ActorRef;
import com.saf.core1.Message;
import com.saf.restaurant.model.OrderAcknowledgement;
import com.saf.restaurant.model.OrderRequest;
import com.saf.restaurant.model.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestaurantActor implements Actor {

    private static final Logger log = LoggerFactory.getLogger(RestaurantActor.class);

    private final ActorRef menuActor;
    private final ActorRef treasuryActor;
    private final ActorRef receiptActor;
    private final Map<String, PendingOrder> pendingOrders = new HashMap<>();

    public RestaurantActor(ActorRef menuActor, ActorRef treasuryActor, ActorRef receiptActor) {
        this.menuActor = menuActor;
        this.treasuryActor = treasuryActor;
        this.receiptActor = receiptActor;
    }

    @Override
    public void onReceive(ActorContext ctx, Message msg) {
        if (msg instanceof RestaurantMessages.OrderPlaced orderPlaced) {
            handleOrderPlaced(ctx, orderPlaced);
        } else if (msg instanceof RestaurantMessages.PriceCalculated priceCalculated) {
            handlePriceCalculated(priceCalculated);
        } else if (msg instanceof RestaurantMessages.StockResult stockResult) {
            handleStockResult(ctx, stockResult);
        } else if (msg instanceof RestaurantMessages.ReceiptReady receiptReady) {
            handleReceiptReady(receiptReady);
        }
    }

    private void handleOrderPlaced(ActorContext ctx, RestaurantMessages.OrderPlaced orderPlaced) {
        log.info("Received order {} from {}", orderPlaced.orderId(), orderPlaced.order().clientName());
        pendingOrders.put(orderPlaced.orderId(), new PendingOrder(orderPlaced.orderId(), orderPlaced.order(), orderPlaced.clientSession(), null, List.of()));
        menuActor.tell(new RestaurantMessages.CalculatePrice(orderPlaced.orderId(), orderPlaced.order(), ctx.self()));
    }

    private void handlePriceCalculated(RestaurantMessages.PriceCalculated priceCalculated) {
        PendingOrder order = pendingOrders.get(priceCalculated.orderId());
        if (order == null) {
            return;
        }

        if (!priceCalculated.valid()) {
            String message = "Articles indisponibles: " + String.join(", ", priceCalculated.missingItems());
            sendAcknowledgement(order, OrderStatus.REJECTED, message, BigDecimal.ZERO, null);
            pendingOrders.remove(priceCalculated.orderId());
            return;
        }

        PendingOrder updated = order.withPricing(priceCalculated.total(), priceCalculated.missingItems());
        pendingOrders.put(priceCalculated.orderId(), updated);

        String message = "Commande validée. Vérification du stock en cours.";
        if (priceCalculated.missingItems() != null && !priceCalculated.missingItems().isEmpty()) {
            message = "Commande en attente: articles inconnus (" + missingToString(priceCalculated.missingItems()) + ").";
        }

        sendAcknowledgement(updated, OrderStatus.PENDING_STOCK,
                message,
                priceCalculated.total(),
                null);
    }

    private void handleStockResult(ActorContext ctx, RestaurantMessages.StockResult stockResult) {
        PendingOrder order = pendingOrders.get(stockResult.orderId());
        if (order == null) {
            log.warn("Stock result received for unknown order {}", stockResult.orderId());
            return;
        }

        if (order.hasMissingItems()) {
            String message = "Articles inconnus: " + missingToString(order.missingItems());
            sendAcknowledgement(order, OrderStatus.REJECTED, message, order.total(), null);
            pendingOrders.remove(stockResult.orderId());
            return;
        }

        if (!stockResult.available()) {
            String message = "Stock insuffisant pour " + missingToString(stockResult.missingItems());
            sendAcknowledgement(order, OrderStatus.REJECTED, message, order.total(), null);
            pendingOrders.remove(stockResult.orderId());
            return;
        }

        BigDecimal amount = order.total() == null ? BigDecimal.ZERO : order.total();
        treasuryActor.tell(new RestaurantMessages.RecordPayment(order.orderId(), amount));
        receiptActor.tell(new RestaurantMessages.GenerateReceipt(order.orderId(), order.order(), amount, ctx.self()));
    }

    private void handleReceiptReady(RestaurantMessages.ReceiptReady receiptReady) {
        PendingOrder order = pendingOrders.remove(receiptReady.orderId());
        if (order == null) {
            return;
        }
        sendAcknowledgement(order, OrderStatus.COMPLETED,
                "Commande servie et ticket généré.",
                receiptReady.document().total(),
                receiptReady.document().content());
    }

    private void sendAcknowledgement(PendingOrder order,
                                     OrderStatus status,
                                     String message,
                                     BigDecimal amount,
                                     String receipt) {
        OrderAcknowledgement acknowledgement = new OrderAcknowledgement(
                order.orderId(),
                status,
                message,
                amount,
                receipt
        );
        order.clientSession().tell(new RestaurantMessages.AcknowledgeOrder(acknowledgement));
    }

    private String missingToString(List<String> missingItems) {
        return (missingItems == null || missingItems.isEmpty()) ? "items demandés" : String.join(", ", missingItems);
    }

    private record PendingOrder(String orderId, OrderRequest order, ActorRef clientSession, BigDecimal total,
                                List<String> missingItems) {
        PendingOrder withPricing(BigDecimal newTotal, List<String> newMissingItems) {
            List<String> safeMissing = newMissingItems == null ? List.of() : newMissingItems;
            return new PendingOrder(orderId, order, clientSession, newTotal, safeMissing);
        }

        boolean hasMissingItems() {
            return missingItems != null && !missingItems.isEmpty();
        }
    }
}
