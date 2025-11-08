package com.saf.core1;

import java.util.concurrent.atomic.AtomicInteger;

public class Pinger implements Actor {
    private final ActorRef partner;
    private final AtomicInteger pongCount;

    public Pinger(ActorRef partner, AtomicInteger pongCount) {
        this.partner = partner;
        this.pongCount = pongCount;
    }

    @Override
    public void onReceive(ActorContext ctx, Message msg) {
        if (msg instanceof Messages.Kickoff) {
            // premier ping
            partner.tell(new Messages.Ping(ctx.self()));
        } else if (msg instanceof Messages.Pong) {
            // j'ai reçu un Pong → on compte et on renvoie un Ping
            pongCount.incrementAndGet();
            partner.tell(new Messages.Ping(ctx.self()));
        }
    }
}
