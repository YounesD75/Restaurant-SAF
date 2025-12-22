package com.saf.restaurant.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ReceiptItemEmbeddable {

    @Column(name = "dish_name", nullable = false)
    private String dishName;

    @Column(name = "quantity")
    private int quantity;

    protected ReceiptItemEmbeddable() {
        // for JPA
    }

    public ReceiptItemEmbeddable(String dishName, int quantity) {
        this.dishName = dishName;
        this.quantity = quantity;
    }

    public String getDishName() {
        return dishName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
