package com.saf.core1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class LocalActorSystem implements ActorSystem {

    // Logger système global
    private static final Logger sysLogger = LoggerFactory.getLogger(LocalActorSystem.class);

    private final ConcurrentMap<String, InternalActorCell> registry = new ConcurrentHashMap<>();
    private final ExecutorService executor;
    private final SupervisionConfig defaultSupervisionConfig;
    
    // Métriques
    private final AtomicInteger createdCount = new AtomicInteger(0);
    private final AtomicInteger stoppedCount = new AtomicInteger(0);
    private final AtomicInteger restartedCount = new AtomicInteger(0);
    private final AtomicInteger errorCount = new AtomicInteger(0);

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
        
        sysLogger.info("SAF ActorSystem started with {} threads.", threads);
    }

    // ... (Les méthodes spawn/stop de ActorSystem restent identiques) ...
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
        cell.start(); 
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
    // === CLASSE INTERNE : InternalActorCell (MODIFIÉE) ====
    // ======================================================

    private final class InternalActorCell implements ActorContext {

        final String id;
        final Supplier<Actor> factory;
        final SupervisionStrategy strategy;
        final SupervisionConfig config;
        final BlockingQueue<Message> mailbox = new LinkedBlockingQueue<>();
        final Deque<Long> restartTimestamps = new ArrayDeque<>();
        
        // Logger spécifique pour les cellules (permettra de filtrer les logs d'acteurs)
        final Logger actorLogger = LoggerFactory.getLogger("com.saf.core1.ActorCell");

        volatile Actor instance;
        final AtomicBoolean running = new AtomicBoolean(false);

        // Implémentation de la référence (C'est ici qu'on log l'ENVOI)
        final ActorRef ref = new ActorRef() {
            @Override
            public void tell(Message msg) {
                // Log de l'envoi (Trace)
                // Note : On ne peut pas facilement savoir "qui" envoie ici car tell() est appelé de l'extérieur
                // Mais on sait "vers qui" (this.id) le message va.
                actorLogger.trace("[Target: {}] Enqueuing message type: {}", id, msg.type());
                mailbox.offer(msg);
            }

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
            try { 
                // Contextualisation du log pour le démarrage
                MDC.put("actorId", id);
                actorLogger.debug("Starting actor...");
                instance.onStart(); 
            } catch (Exception e) {
                actorLogger.error("Error starting actor", e);
            } finally {
                MDC.remove("actorId");
            }
            executor.execute(this::runLoop);
        }

        void stop() {
            running.set(false);
            try { 
                MDC.put("actorId", id);
                actorLogger.debug("Stopping actor...");
                if (instance != null) instance.onStop(); 
            } catch (Exception e) {
                actorLogger.error("Error stopping actor", e);
            } finally {
                MDC.remove("actorId");
            }
        }

        // --- CŒUR DU LOGGING DE TRAITEMENT ---
        void runLoop() {
            // Configuration du MDC pour ce Thread
            MDC.put("actorId", id);
            
            try {
                while (running.get()) {
                    try {
                        Message m = mailbox.poll(100, TimeUnit.MILLISECONDS);
                        if (m == null) continue;

                        // 1. LOG RÉCEPTION
                        actorLogger.info("RECEIVED message [{}] Payload: {}", m.type(), m.payload());

                        long start = System.nanoTime();
                        
                        // 2. EXÉCUTION
                        instance.onReceive(this, m);
                        
                        // 3. LOG SUCCÈS (Optionnel: avec temps d'exécution)
                        long duration = (System.nanoTime() - start) / 1000;
                        actorLogger.debug("PROCESSED message [{}] in {} µs", m.type(), duration);

                    } catch (Throwable t) {
                        // 4. LOG ERREUR
                        errorCount.incrementAndGet();
                        actorLogger.error("ERROR processing message", t);

                        // Gestion supervision...
                        handleSupervision(t);
                    }
                }
            } finally {
                MDC.remove("actorId");
            }
        }

        private void handleSupervision(Throwable t) {
            if (supervisorRef != null) {
                supervisorRef.tell(new SystemMessages.ActorError(id, t));
            }

            if (strategy == SupervisionStrategy.RESTART) {
                if (!recordRestartAttempt()) {
                    actorLogger.warn("Max restarts exceeded. Stopping.");
                    terminate("max restarts exceeded");
                    return;
                }
                actorLogger.warn("Restarting actor due to error...");
                backoff();
                restart();
                if (supervisorRef != null) {
                    supervisorRef.tell(new SystemMessages.ActorRestarted(id, restartTimestamps.size(), config.maxRestarts()));
                }
                restartedCount.incrementAndGet();
            } else if (strategy == SupervisionStrategy.STOP) {
                actorLogger.info("Strategy STOP applied.");
                terminate("stopped on error");
            }
        }

        @Override public ActorRef self() { return ref; }
        @Override public ActorRef spawn(String name, Supplier<Actor> factory, SupervisionStrategy strategy) {
             return LocalActorSystem.this.spawn(name, factory, strategy);
        }
        @Override public void stop(ActorRef ref) { LocalActorSystem.this.stop(ref); }
        @Override public Executor executor() { return executor; }

        private void restart() {
            try { if (instance != null) instance.onStop(); } catch (Exception ignored) {}
            instance = factory.get();
            try { instance.onStart(); } catch (Exception ignored) {}
        }

        private void backoff() {
             long backoffMillis = config.restartBackoffMillis();
            if (backoffMillis <= 0) return;
            try { Thread.sleep(backoffMillis); } 
            catch (InterruptedException e) { Thread.currentThread().interrupt(); }
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