package com.saf.client;

import com.saf.core1.Actor;
import com.saf.core1.ActorContext;
import com.saf.core1.ActorRef;
import com.saf.core1.Message;

import java.util.UUID;

/**
 * Acteur responsable de la partie "commande".
 */
public class CommanderActor implements Actor {

    @Override
    public void onReceive(ActorContext ctx, Message msg) {
        if (msg instanceof ClientMessages.Commander commander) {
            String numero = UUID.randomUUID().toString();
            System.out.printf("[CommanderActor] Commande envoyée: %s -> numéro %s%n",
                    commander.payload(), numero);
            ActorRef replyTo = commander.replyTo();
            if (replyTo != null) {
                replyTo.tell(new ClientMessages.CommandeConfirmee(numero));
            }
        } else {
            System.out.printf("[CommanderActor] message ignoré: %s%n", msg.type());
        }
    }
}
