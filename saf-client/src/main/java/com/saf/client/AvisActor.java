package com.saf.client;

import com.saf.core1.Actor;
import com.saf.core1.ActorContext;
import com.saf.core1.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Acteur qui collecte les avis clients.
 */
public class AvisActor implements Actor {

    private static final Logger log = LoggerFactory.getLogger(AvisActor.class);

    @Override
    public void onReceive(ActorContext ctx, Message msg) {
        if (msg instanceof ClientMessages.Avis avis) {
            log.info("Avis pour commande {} : note {} - {}",
                    avis.commandeId(), avis.note(), avis.commentaire());
        } else {
            log.debug("Message ignore: {}", msg.type());
        }
    }
}
