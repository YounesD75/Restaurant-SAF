package com.saf.client;

import com.saf.core1.Actor;
import com.saf.core1.ActorContext;
import com.saf.core1.ActorRef;
import com.saf.core1.Message;

import java.util.List;

/**
 * Acteur dédié à la consultation des menus.
 */
public class VoirLesMenusActor implements Actor {

    private final ActorRef restaurantRef;

    public VoirLesMenusActor(ActorRef restaurantRef) {
        this.restaurantRef = restaurantRef;
    }

    @Override
    public void onStart() {
        if (restaurantRef != null) {
            restaurantRef.tell(new ClientMessages.VoirMenu(null));
        }
    }

    @Override
    public void onReceive(ActorContext ctx, Message msg) {
        if (msg instanceof ClientMessages.MenuDisponible menu) {
            List<String> plats = menu.plats();
            System.out.printf("[VoirLesMenusActor] menu reçu: %s%n", plats);
        } else {
            System.out.printf("[VoirLesMenusActor] message ignoré: %s%n", msg.type());
        }
    }
}
