package com.saf.core1;

public class Ponger implements Actor {
    @Override
    public void onReceive(ActorContext ctx, Message msg) {
        if (msg instanceof Messages.Ping ping) {
            // répondre au "replyTo" fourni dans le Ping
            ping.replyTo().tell(new Messages.Pong());
        }
    }
}
