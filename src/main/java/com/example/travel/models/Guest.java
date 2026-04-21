package com.example.travel.models;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "guests")
public class Guest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guest_id")
    private long guestID;
    @ManyToOne @JoinColumn(name = "order_id")
    private Order order;
    @Column(name = "guest_first_name")
    private String guestFirstName;
    @Column(name = "guest_name")
    private String guestName;
    @Column(name = "guest_gender")
    private String guestGender;
    @Column(name = "guest_birthday")
    private LocalDate guestBirthday;
    @Column(name = "guest_citizenship")
    private String guestCitizenship;
    @Column(name = "guest_type_document")
    private String guestTypeDocument;
    @Column(name = "guest_passport")
    private String guestPassport;
    @Column(name = "is_buyer")
    private Boolean isBuyer;
    @Column(name = "buyer_email")
    private String buyerEmail;
    @Column(name = "buyer_phone")
    private String buyerPhone;

    public Guest() {}

    public Guest(long guestID, Order order, String guestFirstName, String guestName, String guestGender, LocalDate guestBirthday, String guestCitizenship, String guestTypeDocument, String guestPassport, Boolean isBuyer, String buyerEmail, String buyerPhone) {
        this.guestID = guestID;
        this.order = order;
        this.guestFirstName = guestFirstName;
        this.guestName = guestName;
        this.guestGender = guestGender;
        this.guestBirthday = guestBirthday;
        this.guestCitizenship = guestCitizenship;
        this.guestTypeDocument = guestTypeDocument;
        this.guestPassport = guestPassport;
        this.isBuyer = isBuyer;
        this.buyerEmail = buyerEmail;
        this.buyerPhone = buyerPhone;
    }

    public long getGuestID() {
        return guestID;
    }

    public void setGuestID(long guestID) {
        this.guestID = guestID;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getGuestFirstName() {
        return guestFirstName;
    }

    public void setGuestFirstName(String guestFirstName) {
        this.guestFirstName = guestFirstName;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getGuestGender() {
        return guestGender;
    }

    public void setGuestGender(String guestGender) {
        this.guestGender = guestGender;
    }

    public LocalDate getGuestBirthday() {
        return guestBirthday;
    }

    public void setGuestBirthday(LocalDate guestBirthday) {
        this.guestBirthday = guestBirthday;
    }

    public String getGuestCitizenship() {
        return guestCitizenship;
    }

    public void setGuestCitizenship(String guestCitizenship) {
        this.guestCitizenship = guestCitizenship;
    }

    public String getGuestTypeDocument() {
        return guestTypeDocument;
    }

    public void setGuestTypeDocument(String guestTypeDocument) {
        this.guestTypeDocument = guestTypeDocument;
    }

    public String getGuestPassport() {
        return guestPassport;
    }

    public void setGuestPassport(String guestPassport) {
        this.guestPassport = guestPassport;
    }

    public Boolean getBuyer() {
        return isBuyer;
    }

    public void setBuyer(Boolean buyer) {
        isBuyer = buyer;
    }

    public String getBuyerEmail() {
        return buyerEmail;
    }

    public void setBuyerEmail(String buyerEmail) {
        this.buyerEmail = buyerEmail;
    }

    public String getBuyerPhone() {
        return buyerPhone;
    }

    public void setBuyerPhone(String buyerPhone) {
        this.buyerPhone = buyerPhone;
    }
}
