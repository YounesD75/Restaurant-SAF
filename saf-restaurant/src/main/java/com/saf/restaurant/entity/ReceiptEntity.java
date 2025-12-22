package com.saf.restaurant.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "receipts")
public class ReceiptEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false, unique = true)
    private Long orderId;

    @Column(name = "client_name")
    private String clientName;

    @Column(name = "table_number")
    private String tableNumber;

    @Column(name = "total", precision = 19, scale = 2)
    private BigDecimal total;

    @Column(name = "issued_at", nullable = false)
    private Instant issuedAt;

    @Column(name = "content", columnDefinition = "text")
    private String content;

    @ElementCollection
    @CollectionTable(name = "receipt_items", joinColumns = @JoinColumn(name = "receipt_id"))
    private List<ReceiptItemEmbeddable> items = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Instant getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(Instant issuedAt) {
        this.issuedAt = issuedAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<ReceiptItemEmbeddable> getItems() {
        return items;
    }

    public void setItems(List<ReceiptItemEmbeddable> items) {
        this.items = items;
    }

    public void addItem(ReceiptItemEmbeddable item) {
        this.items.add(item);
    }
}
