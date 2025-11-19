package com.saf.client;

import com.saf.core1.Actor;
import com.saf.core1.ActorContext;
import com.saf.core1.Message;

import java.util.UUID;

/**
 * Acteur chargé du paiement.
 */
public class PayerActor implements Actor {

    @Override
    public void onReceive(ActorContext ctx, Message msg) {
        if (msg instanceof ClientMessages.DemandePaiement demande) {
            String transaction = UUID.randomUUID().toString();
            System.out.printf("[PayerActor] Paiement %s reçu pour commande %s (%s €)%n",
                    transaction, demande.commandeId(), demande.payload());
            ctx.self().tell(new ClientMessages.PaiementEffectue(
                    demande.commandeId(), transaction));
        } else {
            System.out.printf("[PayerActor] message ignoré: %s%n", msg.type());
        }
    }
}
