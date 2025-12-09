package com.saf.restaurant.util;

import com.saf.core1.Actor;
import com.saf.core1.ActorContext;
import com.saf.core1.ActorRef;
import com.saf.core1.LocalActorSystem;
import com.saf.core1.Message;
import com.saf.core1.SupervisionStrategy;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class AskSupport {

    private AskSupport() {
    }

    public static <T extends Message> T ask(LocalActorSystem system,
                                            Consumer<ActorRef> requestBuilder,
                                            Class<T> expectedType,
                                            Duration timeout) {
        CompletableFuture<T> future = new CompletableFuture<>();
        ActorRef replyRef = system.spawn("ask-" + UUID.randomUUID(),
                () -> new ReplyActor<>(future, expectedType),
                SupervisionStrategy.STOP);

        requestBuilder.accept(replyRef);
        try {
            return future.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Ask interrupted", e);
        } catch (Exception e) {
            throw new IllegalStateException("Ask timed out", e);
        }
    }

    private static final class ReplyActor<T extends Message> implements Actor {
        private final CompletableFuture<T> future;
        private final Class<T> expectedType;

        private ReplyActor(CompletableFuture<T> future, Class<T> expectedType) {
            this.future = future;
            this.expectedType = expectedType;
        }

        @Override
        public void onReceive(ActorContext ctx, Message msg) {
            if (expectedType.isInstance(msg)) {
                future.complete(expectedType.cast(msg));
                ctx.stop(ctx.self());
            }
        }
    }
}
