package com.saf.client;

import com.saf.core1.ActorRef;
import com.saf.core1.Message;

import java.math.BigDecimal;
import java.util.List;

/**
 * Ensemble de messages simples échangés entre les acteurs du client.
 */
public final class ClientMessages {
    private ClientMessages() {}

    public record VoirMenu(ActorRef replyTo) implements Message {
        @Override public String type() { return "VoirMenu"; }
        @Override public Object payload() { return replyTo; }
    }

    public record MenuDisponible(List<String> plats) implements Message {
        @Override public String type() { return "MenuDisponible"; }
        @Override public Object payload() { return plats; }
    }

    public record Commander(String description, ActorRef replyTo) implements Message {
        @Override public String type() { return "Commander"; }
        @Override public Object payload() { return description; }
    }

    public record CommandeConfirmee(String numero) implements Message {
        @Override public String type() { return "CommandeConfirmee"; }
        @Override public Object payload() { return numero; }
    }

    public record DemandePaiement(String commandeId, BigDecimal montant) implements Message {
        @Override public String type() { return "DemandePaiement"; }
        @Override public Object payload() { return montant; }
    }

    public record PaiementEffectue(String commandeId, String transactionId) implements Message {
        @Override public String type() { return "PaiementEffectue"; }
        @Override public Object payload() { return transactionId; }
    }

    public record Avis(String commandeId, int note, String commentaire) implements Message {
        @Override public String type() { return "Avis"; }
        @Override public Object payload() { return commentaire; }
    }
}
