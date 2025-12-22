package com.saf.core1;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LocalActorSupervisionTest {

    @Test
    void should_restart_actor_and_process_next_message() throws Exception {
        SupervisionConfig config = new SupervisionConfig(2, 10, 1_000);
        LocalActorSystem system = new LocalActorSystem(1, null, config);

        AtomicInteger attempts = new AtomicInteger(0);
        AtomicInteger processed = new AtomicInteger(0);

        ActorRef ref = system.spawn("flaky", () -> (ctx, msg) -> {
            if (attempts.getAndIncrement() == 0) {
                throw new IllegalStateException("boom");
            }
            processed.incrementAndGet();
        }, SupervisionStrategy.RESTART, config);

        ref.tell(new TestMessage());
        Thread.sleep(100);
        ref.tell(new TestMessage());

        Thread.sleep(200);
        assertEquals(1, processed.get());
    }

    private record TestMessage() implements Message {
        @Override
        public String type() {
            return "TestMessage";
        }

        @Override
        public Object payload() {
            return null;
        }
    }
}
