package com.example.travel.models;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private long orderID;
    @ManyToOne @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "order_date")
    private LocalDateTime orderDate;
    @ManyToOne @JoinColumn(name = "room_id_order")
    private Room room;
    @Column(name = "date_start")
    private LocalDate dateStart;
    @Column(name = "date_end")
    private LocalDate dateEnd;
    @Column(name = "order_cost")
    private double orderCost;
    @Column(name = "is_paid")
    private boolean isPaid;

    public Order() {}

    public Order(long orderID, User user, LocalDateTime orderDate, Room room, LocalDate dateStart, LocalDate dateEnd, double orderCost, boolean isPaid) {
        this.orderID = orderID;
        this.user = user;
        this.orderDate = orderDate;
        this.room = room;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.orderCost = orderCost;
        this.isPaid = isPaid;
    }

    public long getOrderID() {
        return orderID;
    }

    public void setOrderID(long orderID) {
        this.orderID = orderID;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public LocalDate getDateStart() {
        return dateStart;
    }

    public void setDateStart(LocalDate dateStart) {
        this.dateStart = dateStart;
    }

    public LocalDate getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(LocalDate dateEnd) {
        this.dateEnd = dateEnd;
    }

    public double getOrderCost() {
        return orderCost;
    }

    public void setOrderCost(double orderCost) {
        this.orderCost = orderCost;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }
}
