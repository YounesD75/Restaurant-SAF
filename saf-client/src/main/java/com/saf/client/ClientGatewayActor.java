package com.saf.client;

import com.saf.core1.Actor;
import com.saf.core1.ActorContext;
import com.saf.core1.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientGatewayActor implements Actor {

    private static final Logger log = LoggerFactory.getLogger(ClientGatewayActor.class);

    @Override
    public void onReceive(ActorContext ctx, Message msg) {
        if (msg instanceof ClientMessages.ProxyCall call) {
            log.info("Proxy call received: {}", call.payload());
        } else {
            log.debug("Ignored message type: {}", msg.type());
        }
    }
}
