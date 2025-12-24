package com.saf.client;

import com.saf.core1.Actor;
import com.saf.core1.ActorContext;
import com.saf.core1.ActorRef;
import com.saf.core1.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Acteur dédié à la consultation des menus.
 */
public class VoirLesMenusActor implements Actor {

    private static final Logger log = LoggerFactory.getLogger(VoirLesMenusActor.class);

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
            log.info("Menu recu: {}", plats);
        } else {
            log.debug("Message ignore: {}", msg.type());
        }
    }
}
