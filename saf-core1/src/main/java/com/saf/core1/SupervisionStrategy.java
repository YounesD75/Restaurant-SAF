package com.saf.core1;

public enum SupervisionStrategy {
    RESTART,  // redémarre l’acteur
    STOP,     // arrête l’acteur
    RESUME    // ignore l’erreur et continue
}
