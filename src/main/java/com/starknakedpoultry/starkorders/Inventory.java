package com.starknakedpoultry.starkorders;

import jakarta.persistence.*;

@Entity
public class Inventory {

    @Id
    private Long id = 1L; // single row only

    private int birdsAvailable; // total you have ready
    private int birdsLost;

    public Long getId() { return id; }

    public int getBirdsAvailable() { return birdsAvailable; }
    public void setBirdsAvailable(int birdsAvailable) { this.birdsAvailable = birdsAvailable; }

    public int getBirdsLost() { return birdsLost; }
    public void setBirdsLost(int birdsLost) { this.birdsLost = birdsLost; }

    @Transient
    public int getBirdsRemaining() {
        return Math.max(0, birdsAvailable - birdsLost);
    }
}