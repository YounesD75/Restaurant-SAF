package com.saf.core1;

import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class LocalActorSystem implements ActorSystem {

    // Registre de tous les acteurs (par ID)
    private final ConcurrentMap<String, InternalActorCell> registry = new ConcurrentHashMap<>();

    // Pool de threads pour exécuter les acteurs
    private final ExecutorService executor;

    // Superviseur optionnel
    private final ActorRef supervisorRef;

    public LocalActorSystem(int threads) {
        this(threads, null);
    }

    public LocalActorSystem(int threads, ActorRef supervisorRef) {
        this.executor = Executors.newFixedThreadPool(threads);
        this.supervisorRef = supervisorRef;
    }

    // ======================================================
    // === MÉTHODES DE L'INTERFACE ActorSystem ==============
    // ======================================================

    @Override
    public ActorRef spawn(String name, Supplier<Actor> factory, SupervisionStrategy strategy) {
        String id = (name == null || name.isBlank())
                ? UUID.randomUUID().toString()
                : name + "-" + UUID.randomUUID();

        InternalActorCell cell = new InternalActorCell(id, factory, strategy);
        if (registry.putIfAbsent(id, cell) != null) {
            throw new IllegalStateException("Actor id already exists: " + id);
        }
        cell.start(); // démarre la boucle de traitement
        return cell.ref;
    }

    @Override
    public void stop(ActorRef ref) {
        if (ref == null) return;
        InternalActorCell cell = registry.remove(ref.id());
        if (cell != null) cell.stop();
    }

    // ======================================================
    // === CLASSE INTERNE : InternalActorCell ===============
    // ======================================================

    private final class InternalActorCell implements ActorContext {

        final String id;
        final Supplier<Actor> factory;
        final SupervisionStrategy strategy;
        final BlockingQueue<Message> mailbox = new LinkedBlockingQueue<>();

        volatile Actor instance;
        final AtomicBoolean running = new AtomicBoolean(false);

        final ActorRef ref = new ActorRef() {
            @Override public void tell(Message msg) { mailbox.offer(msg); }
            @Override public String id() { return id; }
            @Override public String toString() { return "ActorRef(" + id + ")"; }
        };

        InternalActorCell(String id, Supplier<Actor> factory, SupervisionStrategy strategy) {
            this.id = id;
            this.factory = factory;
            this.strategy = strategy;
        }

        void start() {
            if (!running.compareAndSet(false, true)) return;
            this.instance = factory.get();
            try { instance.onStart(); } catch (Exception ignored) {}
            executor.execute(this::runLoop);
        }

        void stop() {
            running.set(false);
            try { if (instance != null) instance.onStop(); } catch (Exception ignored) {}
        }

        void runLoop() {
            while (running.get()) {
                try {
                    Message m = mailbox.poll(100, TimeUnit.MILLISECONDS);
                    if (m == null) continue;
                    instance.onReceive(this, m);
                } catch (Throwable t) {
                    // Notifie le superviseur si présent
                    if (supervisorRef != null) {
                        supervisorRef.tell(new Messages.ActorError(id, t));
                    }
                    // Applique la stratégie de supervision
                    if (strategy == SupervisionStrategy.RESTART) {
                        try { if (instance != null) instance.onStop(); } catch (Exception ignored) {}
                        instance = factory.get();
                        try { instance.onStart(); } catch (Exception ignored) {}
                    } else if (strategy == SupervisionStrategy.STOP) {
                        stop();
                        break;
                    } else {
                        // RESUME : ignore l’erreur et continue
                    }
                }
            }
        }

        // ==================================================
        // === MÉTHODES DE L’INTERFACE ActorContext =========
        // ==================================================
        @Override public ActorRef self() { return ref; }

        @Override
        public ActorRef spawn(String name, Supplier<Actor> factory, SupervisionStrategy strategy) {
            return LocalActorSystem.this.spawn(name, factory, strategy);
        }

        @Override
        public void stop(ActorRef ref) {
            LocalActorSystem.this.stop(ref);
        }

        @Override
        public Executor executor() {
            return executor;
        }
    }
}
