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
}
