package com.saf.core1.router;

import com.saf.core1.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class RoundRobinPool implements Actor {
    private final Logger logger = LoggerFactory.getLogger(RoundRobinPool.class);

    private final Supplier<Actor> workerFactory; // La recette pour créer un clone
    private final int initialSize;
    private final List<ActorRef> workers = new ArrayList<>();
    private int nextWorkerIndex = 0;

    public RoundRobinPool(Supplier<Actor> workerFactory, int initialSize) {
        this.workerFactory = workerFactory;
        this.initialSize = initialSize;
    }

    @Override
    public void onStart() {
        logger.info("Starting Router Pool with {} initial workers.", initialSize);
        // Création initiale des workers
        // Note: Ici 'null' est passé pour le contexte car onStart n'a pas ctx, 
        // MAIS dans ta structure actuelle, on doit attendre le premier message ou tricher.
        // Pour faire simple avec ton interface Actor actuelle :
        // La création se fera dynamiquement via le contexte lors du premier message ou on modifie Actor pour avoir onStart(ActorContext ctx).
        // ADAPTATION : On va gérer la création via une méthode interne helper appelée avec le contexte.
    }

    @Override
    public void onReceive(ActorContext ctx, Message msg) {
        // 1. Initialisation paresseuse (si la liste est vide au début)
        if (workers.isEmpty() && initialSize > 0) {
            addWorkers(ctx, initialSize);
        }

        // 2. Gestion de l'élasticité (Commandes Système)
        if (msg instanceof RouterMessages.ScaleUp cmd) {
            logger.info("Scalability: Scaling UP by {}", cmd.quantity());
            addWorkers(ctx, cmd.quantity());
            return;
        } 
        else if (msg instanceof RouterMessages.ScaleDown cmd) {
            logger.info("Scalability: Scaling DOWN by {}", cmd.quantity());
            removeWorkers(ctx, cmd.quantity());
            return;
        }

        // 3. Routage du message métier (Round Robin)
        if (workers.isEmpty()) {
            logger.warn("No workers available to process message!");
            return;
        }

        ActorRef selectedWorker = workers.get(nextWorkerIndex);
        // Logique tourniquet
        nextWorkerIndex = (nextWorkerIndex + 1) % workers.size();

        logger.debug("Routing message [{}] to worker {}", msg.type(), selectedWorker.id());
        
        // On transfère le message au worker
        selectedWorker.tell(msg);
    }

    // --- Méthodes privées de gestion ---

    private void addWorkers(ActorContext ctx, int count) {
        for (int i = 0; i < count; i++) {
            // Le Router supervise ses enfants. Si un worker plante, le Router gère.
            ActorRef worker = ctx.spawn("worker-" + workers.size(), workerFactory, SupervisionStrategy.RESTART);
            workers.add(worker);
        }
        logger.info("Pool size is now: {}", workers.size());
    }

    private void removeWorkers(ActorContext ctx, int count) {
        int toRemove = Math.min(count, workers.size());
        for (int i = 0; i < toRemove; i++) {
            // On enlève toujours le dernier pour simplifier
            ActorRef worker = workers.remove(workers.size() - 1);
            ctx.stop(worker);
        }
        // Réinitialiser l'index pour éviter un OutOfBounds
        nextWorkerIndex = 0;
        logger.info("Pool size is now: {}", workers.size());
    }
}