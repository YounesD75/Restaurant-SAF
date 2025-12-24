package com.saf.client;

import com.saf.core1.Actor;
import com.saf.core1.ActorContext;
import com.saf.core1.ActorRef;
import com.saf.core1.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Acteur responsable de la partie "commande".
 */
public class CommanderActor implements Actor {

    private static final Logger log = LoggerFactory.getLogger(CommanderActor.class);

    @Override
    public void onReceive(ActorContext ctx, Message msg) {
        if (msg instanceof ClientMessages.Commander commander) {
            String numero = UUID.randomUUID().toString();
            log.info("Commande envoyee: {} -> numero {}", commander.payload(), numero);
            ActorRef replyTo = commander.replyTo();
            if (replyTo != null) {
                replyTo.tell(new ClientMessages.CommandeConfirmee(numero));
            }
        } else {
            log.debug("Message ignore: {}", msg.type());
        }
    }
}
