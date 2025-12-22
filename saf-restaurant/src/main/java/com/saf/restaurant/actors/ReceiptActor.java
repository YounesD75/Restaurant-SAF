package com.saf.restaurant.actors;

import com.saf.core1.Actor;
import com.saf.core1.ActorContext;
import com.saf.core1.Message;
import com.saf.restaurant.entity.ReceiptEntity;
import com.saf.restaurant.entity.ReceiptItemEmbeddable;
import com.saf.restaurant.model.OrderItem;
import com.saf.restaurant.model.OrderRequest;
import com.saf.restaurant.model.ReceiptDocument;
import com.saf.restaurant.repository.ReceiptRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReceiptActor implements Actor {

    private final ReceiptRepository receiptRepository;

    public ReceiptActor(ReceiptRepository receiptRepository) {
        this.receiptRepository = receiptRepository;
    }

    @Override
    public void onReceive(ActorContext ctx, Message msg) {
        if (msg instanceof RestaurantMessages.GenerateReceipt generateReceipt) {
            ReceiptDocument document = buildReceipt(generateReceipt);
            persist(generateReceipt.orderId(), document);
            generateReceipt.replyTo().tell(new RestaurantMessages.ReceiptReady(generateReceipt.orderId(), document));
        }
    }

    private ReceiptDocument buildReceipt(RestaurantMessages.GenerateReceipt generateReceipt) {
        OrderRequest order = generateReceipt.order();
        BigDecimal total = generateReceipt.total() == null ? BigDecimal.ZERO : generateReceipt.total();

        List<OrderItem> items = order.items() == null ? List.of() : order.items();

        String itemsLines = items.stream()
                .map(this::formatItem)
                .collect(Collectors.joining("\n"));
        String content = """
                === SAF RESTAURANT RECEIPT ===
                Client: %s
                Table: %s
                Total: %s €
                Items:
                %s
                """.formatted(
                nullToEmpty(order.clientName()),
                nullToEmpty(order.tableNumber()),
                total,
                itemsLines
        );

        return new ReceiptDocument(
                generateReceipt.orderId(),
                order.clientName(),
                order.tableNumber(),
                total,
                items,
                Instant.now(),
                content
        );
    }

    private void persist(Long orderId, ReceiptDocument document) {
        ReceiptEntity entity = receiptRepository.findByOrderId(orderId).orElseGet(ReceiptEntity::new);
        entity.setOrderId(orderId);
        entity.setClientName(document.clientName());
        entity.setTableNumber(document.tableNumber());
        entity.setTotal(document.total());
        entity.setIssuedAt(document.issuedAt());
        entity.setContent(document.content());
        List<OrderItem> docItems = document.items() == null ? List.of() : document.items();
        entity.setItems(new ArrayList<>(toEmbeddables(docItems)));
        receiptRepository.save(entity);
    }

    private String formatItem(OrderItem item) {
        return "- %s x %d".formatted(item.dishName(), item.quantity());
    }

    private List<ReceiptItemEmbeddable> toEmbeddables(List<OrderItem> items) {
        return items.stream()
                .map(item -> new ReceiptItemEmbeddable(item.dishName(), item.quantity()))
                .collect(Collectors.toList());
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
