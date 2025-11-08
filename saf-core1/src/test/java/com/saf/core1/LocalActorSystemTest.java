package com.saf.core1;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LocalActorSystemTest {

    @Test
    void should_exchange_ping_pong_messages() throws Exception {
        // Système simple à 2 threads
        LocalActorSystem system = new LocalActorSystem(2);

        // On crée d'abord le Ponger (pas d'état)
        ActorRef ponger = system.spawn("ponger", Ponger::new, SupervisionStrategy.RESUME);

        // Puis le Pinger, avec un compteur partagé pour vérifier le résultat
        AtomicInteger pongCount = new AtomicInteger(0);
        ActorRef pinger = system.spawn("pinger", () -> new Pinger(ponger, pongCount), SupervisionStrategy.RESUME);

        // Démarre la boucle avec un Kickoff
        pinger.tell(new Messages.Kickoff());

        // Laisse tourner un petit moment
        Thread.sleep(500);

        // On doit avoir au moins reçu un Pong
        assertTrue(pongCount.get() >= 1, "Le Pinger devrait avoir reçu au moins un Pong");
    }
}
