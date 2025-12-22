package com.saf.core1;

import java.util.function.Supplier;

public interface ActorSystem {
    ActorRef spawn(String name, Supplier<Actor> factory, SupervisionStrategy strategy);

    default ActorRef spawn(String name, Supplier<Actor> factory, SupervisionStrategy strategy, SupervisionConfig config) {
        return spawn(name, factory, strategy);
    }

    void stop(ActorRef ref);
}
