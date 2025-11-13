package com.saf.core1;

public interface ActorRef {
    void tell(Message msg);   // envoie un message
    String id();              // identifiant unique de l’acteur
}
