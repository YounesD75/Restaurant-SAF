package com.saf.restaurant.service;

import com.saf.core1.ActorRef;
import com.saf.core1.LocalActorSystem;
import com.saf.restaurant.actors.RestaurantMessages;
import com.saf.restaurant.entity.ReceiptEntity;
import com.saf.restaurant.entity.ReceiptItemEmbeddable;
import com.saf.restaurant.model.MenuItem;
import com.saf.restaurant.model.OrderAcknowledgement;
import com.saf.restaurant.model.OrderItem;
import com.saf.restaurant.model.OrderRequest;
import com.saf.restaurant.model.ReceiptDocument;
import com.saf.restaurant.model.StockResultPayload;
import com.saf.restaurant.model.TreasurySummary;
import com.saf.restaurant.repository.ReceiptRepository;
import com.saf.restaurant.util.AskSupport;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class RestaurantCommandService {

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(5);

    private final RestaurantActorRegistry registry;
    private final ReceiptRepository receiptRepository;

    public RestaurantCommandService(RestaurantActorRegistry registry, ReceiptRepository receiptRepository) {
        this.registry = registry;
        this.receiptRepository = receiptRepository;
    }

    public OrderAcknowledgement placeOrder(OrderRequest request) {
        String orderId = registry.nextOrderId();
        CompletableFuture<OrderAcknowledgement> future = new CompletableFuture<>();
        registry.spawnClientSession(orderId, request, future);

        try {
            return future.get(DEFAULT_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to place order", e);
        }
    }

    public List<MenuItem> listMenu() {
        LocalActorSystem system = registry.actorSystem();
        ActorRef menuActor = registry.menuActor();
        RestaurantMessages.MenuListed response = AskSupport.ask(system,
                replyTo -> menuActor.tell(new RestaurantMessages.ListMenu(replyTo)),
                RestaurantMessages.MenuListed.class,
                DEFAULT_TIMEOUT);
        return response.items();
    }

    public TreasurySummary treasurySummary() {
        LocalActorSystem system = registry.actorSystem();
        ActorRef treasuryActor = registry.treasuryActor();
        RestaurantMessages.TreasurySnapshot snapshot = AskSupport.ask(system,
                replyTo -> treasuryActor.tell(new RestaurantMessages.TreasurySnapshotRequest(replyTo)),
                RestaurantMessages.TreasurySnapshot.class,
                DEFAULT_TIMEOUT);
        return new TreasurySummary(snapshot.totalRevenue(), snapshot.ordersSettled());
    }

    public void handleStockResult(StockResultPayload payload) {
        registry.restaurantActor().tell(new RestaurantMessages.StockResult(
                payload.orderId(),
                payload.available(),
                payload.missingItems()
        ));
    }

    public List<ReceiptDocument> receipts() {
        return receiptRepository.findAllByOrderByIssuedAtDesc()
                .stream()
                .map(this::toDocument)
                .collect(Collectors.toList());
    }

    public ReceiptDocument receipt(String orderId) {
        ReceiptEntity entity = receiptRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Receipt not found for " + orderId));
        return toDocument(entity);
    }

    private ReceiptDocument toDocument(ReceiptEntity entity) {
        List<ReceiptItemEmbeddable> storedItems = entity.getItems() == null ? List.of() : entity.getItems();
        List<OrderItem> items = storedItems.stream()
                .map(this::toOrderItem)
                .collect(Collectors.toList());
        return new ReceiptDocument(
                entity.getOrderId(),
                entity.getClientName(),
                entity.getTableNumber(),
                entity.getTotal(),
                items,
                entity.getIssuedAt(),
                entity.getContent()
        );
    }

    private OrderItem toOrderItem(ReceiptItemEmbeddable embeddable) {
        return new OrderItem(embeddable.getSku(), embeddable.getQuantity());
    }
}
