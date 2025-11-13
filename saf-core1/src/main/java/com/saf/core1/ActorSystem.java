package com.saf.core1;

import java.util.function.Supplier;

public interface ActorSystem {
    ActorRef spawn(String name, Supplier<Actor> factory, SupervisionStrategy strategy);
    void stop(ActorRef ref);
}