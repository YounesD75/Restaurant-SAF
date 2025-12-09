package com.saf.core1;

/**
 * Interface de base pour tout acteur du framework SAF.
 * Un acteur définit un comportement asynchrone : il reçoit des messages et réagit.
 */
public interface Actor {

    /**
     * Méthode appelée automatiquement à la création de l’acteur.
     * Peut être utilisée pour initialiser des ressources.
     */
    default void onStart() {}

    /**
     * Méthode principale de l’acteur.
     * Appelée à chaque fois qu’un message est reçu dans sa mailbox.
     *
     * @param ctx le contexte d’exécution de l’acteur (pour accéder à self(), spawn(), stop(), etc.)
     * @param msg le message reçu
     * @throws Exception si une erreur se produit lors du traitement
     */
    void onReceive(ActorContext ctx, Message msg) throws Exception;

    /**
     * Méthode appelée automatiquement avant la destruction de l’acteur.
     * Peut être utilisée pour fermer des ressources ou sauvegarder un état.
     */
    default void onStop() {}
}
