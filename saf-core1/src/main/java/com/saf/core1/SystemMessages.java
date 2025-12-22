package com.saf.core1;

/**
 * Messages internes au système pour la supervision.
 */
public final class SystemMessages {
    private SystemMessages() {}

    public record ActorError(String actorId, Throwable error) implements Message {
        @Override
        public String type() { return "ActorError"; }

        @Override
        public Object payload() { return error; }
    }

    public record ActorRestarted(String actorId, int attempts, int maxAttempts) implements Message {
        @Override
        public String type() { return "ActorRestarted"; }

        @Override
        public Object payload() { return actorId; }
    }

    public record ActorStopped(String actorId, String reason) implements Message {
        @Override
        public String type() { return "ActorStopped"; }

        @Override
        public Object payload() { return reason; }
    }
}
