package com.saf.core1;

public record ActorSystemMetrics(int activeActors,
                                 int createdActors,
                                 int stoppedActors,
                                 int restartedActors,
                                 int errorCount) {}
