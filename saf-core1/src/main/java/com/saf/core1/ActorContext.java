package com.saf.core1;

import java.util.concurrent.Executor;
import java.util.function.Supplier;

public interface ActorContext {
    ActorRef self();   // référence vers soi-même
    ActorRef spawn(String name, Supplier<Actor> factory, SupervisionStrategy strategy); // créer un acteur enfant

    default ActorRef spawn(String name, Supplier<Actor> factory, SupervisionStrategy strategy, SupervisionConfig config) {
        return spawn(name, factory, strategy);
    }

    void stop(ActorRef ref);  // arrêter un acteur
    Executor executor();      // récupérer le pool de threads du système
}
