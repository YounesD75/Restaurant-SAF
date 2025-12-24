package com.saf.client;

import com.saf.core1.Actor;
import com.saf.core1.ActorContext;
import com.saf.core1.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Acteur chargé du paiement.
 */
public class PayerActor implements Actor {

    private static final Logger log = LoggerFactory.getLogger(PayerActor.class);

    @Override
    public void onReceive(ActorContext ctx, Message msg) {
        if (msg instanceof ClientMessages.DemandePaiement demande) {
            String transaction = UUID.randomUUID().toString();
            log.info("Paiement {} recu pour commande {} ({} EUR)",
                    transaction, demande.commandeId(), demande.payload());
            ctx.self().tell(new ClientMessages.PaiementEffectue(
                    demande.commandeId(), transaction));
        } else {
            log.debug("Message ignore: {}", msg.type());
        }
    }
}
