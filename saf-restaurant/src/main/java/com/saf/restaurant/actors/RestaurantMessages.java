package com.saf.restaurant.actors;

import com.saf.core1.Message;
import com.saf.core1.ActorRef;
import com.saf.restaurant.model.MenuItem;
import com.saf.restaurant.model.OrderAcknowledgement;
import com.saf.restaurant.model.OrderRequest;
import com.saf.restaurant.model.ReceiptDocument;

import java.math.BigDecimal;
import java.util.List;

public final class RestaurantMessages {
    private RestaurantMessages() {
    }

    public record OrderPlaced(Long orderId, OrderRequest order, ActorRef clientSession) implements Message {
        @Override
        public String type() {
            return "OrderPlaced";
        }

        @Override
        public Object payload() {
            return order;
        }
    }

    public record AcknowledgeOrder(OrderAcknowledgement acknowledgement) implements Message {
        @Override
        public String type() {
            return "AcknowledgeOrder";
        }

        @Override
        public Object payload() {
            return acknowledgement;
        }
    }

    public record ListMenu(ActorRef replyTo) implements Message {
        @Override
        public String type() {
            return "ListMenu";
        }

        @Override
        public Object payload() {
            return replyTo;
        }
    }

    public record MenuListed(List<MenuItem> items) implements Message {
        @Override
        public String type() {
            return "MenuListed";
        }

        @Override
        public Object payload() {
            return items;
        }
    }

    public record CalculatePrice(Long orderId, OrderRequest order, ActorRef replyTo) implements Message {
        @Override
        public String type() {
            return "CalculatePrice";
        }

        @Override
        public Object payload() {
            return order;
        }
    }

    public record PriceCalculated(Long orderId, boolean valid, BigDecimal total, List<String> missingItems) implements Message {
        @Override
        public String type() {
            return "PriceCalculated";
        }

        @Override
        public Object payload() {
            return this;
        }
    }

    public record StockResult(Long orderId, boolean available, List<String> missingItems) implements Message {
        @Override
        public String type() {
            return "StockResult";
        }

        @Override
        public Object payload() {
            return this;
        }
    }

    public record RecordPayment(Long orderId, BigDecimal amount) implements Message {
        @Override
        public String type() {
            return "RecordPayment";
        }

        @Override
        public Object payload() {
            return amount;
        }
    }

    public record TreasurySnapshotRequest(ActorRef replyTo) implements Message {
        @Override
        public String type() {
            return "TreasurySnapshotRequest";
        }

        @Override
        public Object payload() {
            return replyTo;
        }
    }

    public record TreasurySnapshot(BigDecimal totalRevenue, int ordersSettled) implements Message {
        @Override
        public String type() {
            return "TreasurySnapshot";
        }

        @Override
        public Object payload() {
            return this;
        }
    }

    public record GenerateReceipt(Long orderId, OrderRequest order, BigDecimal total, ActorRef replyTo) implements Message {
        @Override
        public String type() {
            return "GenerateReceipt";
        }

        @Override
        public Object payload() {
            return order;
        }
    }

    public record ReceiptReady(Long orderId, ReceiptDocument document) implements Message {
        @Override
        public String type() {
            return "ReceiptReady";
        }

        @Override
        public Object payload() {
            return document;
        }
    }
}
