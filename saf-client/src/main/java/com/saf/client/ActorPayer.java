package com.saf.client;


import java.util.Scanner;

public class ActorPayer {
    private float price;
    public ActorPayer() {
    }

    public ActorPayer(float price) {
        this.price = price;
    }


    public void pay(){
        System.out.println("Veuillez payez votre commande d'un montant de $$");
        Scanner  sc = new Scanner(System.in);
        this.setPrice(sc.nextFloat());
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getPrice() {
        return price;
    }
}
