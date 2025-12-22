package com.saf.restaurant.actors;

import com.saf.core1.Actor;
import com.saf.core1.ActorContext;
import com.saf.core1.ActorRef;
import com.saf.core1.Message;
import com.saf.restaurant.model.OrderAcknowledgement;
import com.saf.restaurant.model.OrderRequest;
import com.saf.restaurant.model.OrderStatus;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientSessionActor implements Actor {

    public record BeginOrder() implements Message {
        @Override
        public String type() {
            return "BeginOrder";
        }

        @Override
        public Object payload() {
            return this;
        }
    }

    private final Long orderId;
    private final OrderRequest orderRequest;
    private final ActorRef restaurantActor;
    private final CompletableFuture<OrderAcknowledgement> responseFuture;
    private final AtomicBoolean started = new AtomicBoolean(false);
    private final AtomicBoolean acknowledged = new AtomicBoolean(false);

    public ClientSessionActor(Long orderId,
                              OrderRequest orderRequest,
                              ActorRef restaurantActor,
                              CompletableFuture<OrderAcknowledgement> responseFuture) {
        this.orderId = orderId;
        this.orderRequest = orderRequest;
        this.restaurantActor = restaurantActor;
        this.responseFuture = responseFuture;
    }

    @Override
    public void onReceive(ActorContext ctx, Message msg) {
        if (msg instanceof BeginOrder) {
            if (started.compareAndSet(false, true)) {
                restaurantActor.tell(new RestaurantMessages.OrderPlaced(orderId, orderRequest, ctx.self()));
            }
        } else if (msg instanceof RestaurantMessages.AcknowledgeOrder acknowledgeOrder) {
            if (acknowledged.compareAndSet(false, true)) {
                responseFuture.complete(acknowledgeOrder.acknowledgement());
            }
            OrderStatus status = acknowledgeOrder.acknowledgement().status();
            if (status != OrderStatus.PENDING_STOCK) {
                ctx.stop(ctx.self());
            }
        }
    }
}
