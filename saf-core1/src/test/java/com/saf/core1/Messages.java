package com.saf.core1;

public final class Messages {
    /** Démarrage de la démo (kickoff du ping initial) */
    public record Kickoff() implements Message {
        public String type() { return "Kickoff"; }
        public Object payload() { return this; }
    }

    /** Ping envoyé au partenaire, on inclut à qui répondre */
    public record Ping(ActorRef replyTo) implements Message {
        public String type() { return "Ping"; }
        public Object payload() { return this; }
    }

    /** Réponse à un Ping */
    public record Pong() implements Message {
        public String type() { return "Pong"; }
        public Object payload() { return this; }
    }

    /** Remontée d’erreur vers le superviseur (utilisé par LocalActorSystem) */
    public record ActorError(String actorId, Throwable error) implements Message {
        public String type() { return "ActorError"; }
        public Object payload() { return error; }
    }
}
