package com.saf.core1;

public record SupervisionConfig(int maxRestarts, long restartBackoffMillis, long restartWindowMillis) {

    public static SupervisionConfig defaultConfig() {
        return new SupervisionConfig(3, 200, 10_000);
    }
}
