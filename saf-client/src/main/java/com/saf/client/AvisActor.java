package com.saf.client;

import com.saf.core1.Actor;
import com.saf.core1.ActorContext;
import com.saf.core1.Message;

/**
 * Acteur qui collecte les avis clients.
 */
public class AvisActor implements Actor {

    @Override
    public void onReceive(ActorContext ctx, Message msg) {
        if (msg instanceof ClientMessages.Avis avis) {
            System.out.printf("[AvisActor] avis pour commande %s : note %d - %s%n",
                    avis.commandeId(), avis.note(), avis.commentaire());
        } else {
            System.out.printf("[AvisActor] message ignoré: %s%n", msg.type());
        }
    }
}
