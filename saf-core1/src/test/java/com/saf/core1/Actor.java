package com.saf.core1;

public interface Actor {
    default void onStart() {}                                   // appelé à la création
    void onReceive(ActorContext ctx, Message msg) throws Exception; // appelé à chaque message reçu
    default void onStop() {}                                    // appelé à la destruction
}
