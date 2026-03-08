package com.starknakedpoultry.starkorders;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;
    private String phone;
    private int quantity;

    private String pickupDate;
    private String pickupTime;

    @Column(length = 500)
    private String notes;

    @Enumerated(EnumType.STRING)
    private OrderSource source;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.NEW;

    private LocalDateTime createdAt;

    // New fields for completed sales
    private Double totalWeight;
    private Double totalPrice;

    @Column(length = 50)
    private String paymentMethod;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getPickupDate() { return pickupDate; }
    public void setPickupDate(String pickupDate) { this.pickupDate = pickupDate; }

    public String getPickupTime() { return pickupTime; }
    public void setPickupTime(String pickupTime) { this.pickupTime = pickupTime; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public OrderSource getSource() { return source; }
    public void setSource(OrderSource source) { this.source = source; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public Double getTotalWeight() { return totalWeight; }
    public void setTotalWeight(Double totalWeight) { this.totalWeight = totalWeight; }

    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    @Transient
    public String getFormattedPickupDate() {
        try {
            LocalDate date = LocalDate.parse(pickupDate);
            return date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        } catch (Exception e) {
            return pickupDate;
        }
    }

    @Transient
    public String getFormattedPickupTime() {
        try {
            LocalTime time = LocalTime.parse(pickupTime);
            return time.format(DateTimeFormatter.ofPattern("h:mm a"));
        } catch (Exception e) {
            return pickupTime;
        }
    }

    @Transient
    public boolean hasNotes() {
        return notes != null && !notes.trim().isEmpty();
    }

    @Transient
    public boolean hasSaleData() {
        return totalWeight != null || totalPrice != null || (paymentMethod != null && !paymentMethod.isBlank());
    }
}