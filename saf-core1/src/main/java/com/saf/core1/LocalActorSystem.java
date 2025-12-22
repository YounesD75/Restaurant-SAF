package com.saf.core1;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class LocalActorSystem implements ActorSystem {

    // Registre de tous les acteurs (par ID)
    private final ConcurrentMap<String, InternalActorCell> registry = new ConcurrentHashMap<>();

    // Pool de threads pour exécuter les acteurs
    private final ExecutorService executor;
    private final SupervisionConfig defaultSupervisionConfig;
    private final AtomicInteger createdCount = new AtomicInteger(0);
    private final AtomicInteger stoppedCount = new AtomicInteger(0);
    private final AtomicInteger restartedCount = new AtomicInteger(0);
    private final AtomicInteger errorCount = new AtomicInteger(0);

    // Superviseur optionnel
    private final ActorRef supervisorRef;

    public LocalActorSystem(int threads) {
        this(threads, null, SupervisionConfig.defaultConfig());
    }

    public LocalActorSystem(int threads, ActorRef supervisorRef) {
        this(threads, supervisorRef, SupervisionConfig.defaultConfig());
    }

    public LocalActorSystem(int threads, ActorRef supervisorRef, SupervisionConfig defaultSupervisionConfig) {
        this.executor = Executors.newFixedThreadPool(threads);
        this.supervisorRef = supervisorRef;
        this.defaultSupervisionConfig = defaultSupervisionConfig == null
                ? SupervisionConfig.defaultConfig()
                : defaultSupervisionConfig;
    }

    // ======================================================
    // === MÉTHODES DE L'INTERFACE ActorSystem ==============
    // ======================================================

    @Override
    public ActorRef spawn(String name, Supplier<Actor> factory, SupervisionStrategy strategy) {
        return spawn(name, factory, strategy, defaultSupervisionConfig);
    }

    @Override
    public ActorRef spawn(String name, Supplier<Actor> factory, SupervisionStrategy strategy, SupervisionConfig config) {
        String id = (name == null || name.isBlank())
                ? UUID.randomUUID().toString()
                : name + "-" + UUID.randomUUID();

        SupervisionConfig resolvedConfig = config == null ? defaultSupervisionConfig : config;
        InternalActorCell cell = new InternalActorCell(id, factory, strategy, resolvedConfig);
        if (registry.putIfAbsent(id, cell) != null) {
            throw new IllegalStateException("Actor id already exists: " + id);
        }
        createdCount.incrementAndGet();
        cell.start(); // démarre la boucle de traitement
        return cell.ref;
    }

    @Override
    public void stop(ActorRef ref) {
        if (ref == null) return;
        InternalActorCell cell = registry.remove(ref.id());
        if (cell != null) {
            cell.stop();
            stoppedCount.incrementAndGet();
        }
    }

    public ActorSystemMetrics metrics() {
        return new ActorSystemMetrics(
                registry.size(),
                createdCount.get(),
                stoppedCount.get(),
                restartedCount.get(),
                errorCount.get()
        );
    }

    // ======================================================
    // === CLASSE INTERNE : InternalActorCell ===============
    // ======================================================

    private final class InternalActorCell implements ActorContext {

        final String id;
        final Supplier<Actor> factory;
        final SupervisionStrategy strategy;
        final SupervisionConfig config;
        final BlockingQueue<Message> mailbox = new LinkedBlockingQueue<>();
        final Deque<Long> restartTimestamps = new ArrayDeque<>();

        volatile Actor instance;
        final AtomicBoolean running = new AtomicBoolean(false);

        final ActorRef ref = new ActorRef() {
            @Override public void tell(Message msg) { mailbox.offer(msg); }
            @Override public String id() { return id; }
            @Override public String toString() { return "ActorRef(" + id + ")"; }
        };

        InternalActorCell(String id, Supplier<Actor> factory, SupervisionStrategy strategy, SupervisionConfig config) {
            this.id = id;
            this.factory = factory;
            this.strategy = strategy;
            this.config = config;
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
                    errorCount.incrementAndGet();
                    // Notifie le superviseur si présent
                    if (supervisorRef != null) {
                        supervisorRef.tell(new SystemMessages.ActorError(id, t));
                    }
                    // Applique la stratégie de supervision
                    if (strategy == SupervisionStrategy.RESTART) {
                        if (!recordRestartAttempt()) {
                            terminate("max restarts exceeded");
                            break;
                        }
                        backoff();
                        restart();
                        if (supervisorRef != null) {
                            supervisorRef.tell(new SystemMessages.ActorRestarted(
                                    id,
                                    restartTimestamps.size(),
                                    config.maxRestarts()
                            ));
                        }
                        restartedCount.incrementAndGet();
                    } else if (strategy == SupervisionStrategy.STOP) {
                        terminate("stopped on error");
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

        private void restart() {
            try { if (instance != null) instance.onStop(); } catch (Exception ignored) {}
            instance = factory.get();
            try { instance.onStart(); } catch (Exception ignored) {}
        }

        private void backoff() {
            long backoffMillis = config.restartBackoffMillis();
            if (backoffMillis <= 0) {
                return;
            }
            try {
                Thread.sleep(backoffMillis);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }

        private boolean recordRestartAttempt() {
            long now = System.currentTimeMillis();
            long window = config.restartWindowMillis();
            if (window > 0) {
                while (!restartTimestamps.isEmpty() && now - restartTimestamps.peekFirst() > window) {
                    restartTimestamps.pollFirst();
                }
            } else {
                restartTimestamps.clear();
            }
            restartTimestamps.addLast(now);
            return config.maxRestarts() <= 0 || restartTimestamps.size() <= config.maxRestarts();
        }

        private void terminate(String reason) {
            if (supervisorRef != null) {
                supervisorRef.tell(new SystemMessages.ActorStopped(id, reason));
            }
            LocalActorSystem.this.stop(ref);
        }
    }
}
