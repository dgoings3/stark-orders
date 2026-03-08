package com.starknakedpoultry.starkorders;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double chicksCost = 0.0;
    private Double feedCost = 0.0;
    private Double beddingCost = 0.0;
    private Double laborCost = 0.0;
    private Double miscCost = 0.0;

    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    public void onSave() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }

    public Double getChicksCost() { return chicksCost; }
    public void setChicksCost(Double chicksCost) { this.chicksCost = chicksCost; }

    public Double getFeedCost() { return feedCost; }
    public void setFeedCost(Double feedCost) { this.feedCost = feedCost; }

    public Double getBeddingCost() { return beddingCost; }
    public void setBeddingCost(Double beddingCost) { this.beddingCost = beddingCost; }

    public Double getLaborCost() { return laborCost; }
    public void setLaborCost(Double laborCost) { this.laborCost = laborCost; }

    public Double getMiscCost() { return miscCost; }
    public void setMiscCost(Double miscCost) { this.miscCost = miscCost; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    @Transient
    public double getTotalExpenses() {
        return safe(chicksCost) + safe(feedCost) + safe(beddingCost) + safe(laborCost) + safe(miscCost);
    }

    private double safe(Double value) {
        return value == null ? 0.0 : value;
    }
}