package com.saf.restaurant.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ReceiptItemEmbeddable {

    @Column(name = "sku", nullable = false)
    private String sku;

    @Column(name = "quantity")
    private int quantity;

    protected ReceiptItemEmbeddable() {
        // for JPA
    }

    public ReceiptItemEmbeddable(String sku, int quantity) {
        this.sku = sku;
        this.quantity = quantity;
    }

    public String getSku() {
        return sku;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
