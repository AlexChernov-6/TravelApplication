package com.example.travel.models;

import com.example.travel.util.CryptoConverter;
import jakarta.persistence.*;

@Entity
@Table(name = "guests")
public class Guest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guest_id")
    private long guestID;
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
    @Convert(converter = CryptoConverter.class)
    @Column(name = "guest_first_name", columnDefinition = "bytea")
    private String guestFirstName;
    @Convert(converter = CryptoConverter.class)
    @Column(name = "guest_name", columnDefinition = "bytea")
    private String guestName;
    @Convert(converter = CryptoConverter.class)
    @Column(name = "guest_gender", columnDefinition = "bytea")
    private String guestGender;
    @Convert(converter = CryptoConverter.class)
    @Column(name = "guest_birthday", columnDefinition = "bytea")
    private String guestBirthday;   // ранее был LocalDate
    @Convert(converter = CryptoConverter.class)
    @Column(name = "guest_citizenship", columnDefinition = "bytea")
    private String guestCitizenship;
    @Convert(converter = CryptoConverter.class)
    @Column(name = "guest_type_document", columnDefinition = "bytea")
    private String guestTypeDocument;
    @Convert(converter = CryptoConverter.class)
    @Column(name = "guest_passport", columnDefinition = "bytea")
    private String guestPassport;
    @Convert(converter = CryptoConverter.class)
    @Column(name = "is_buyer", columnDefinition = "bytea")
    private String isBuyer;
    @Convert(converter = CryptoConverter.class)
    @Column(name = "buyer_email", columnDefinition = "bytea")
    private String buyerEmail;
    @Convert(converter = CryptoConverter.class)
    @Column(name = "buyer_phone", columnDefinition = "bytea")
    private String buyerPhone;

    public Guest() {}

    public Guest(Order order, String guestFirstName, String guestName, String guestGender,
                 String guestBirthday, String guestCitizenship, String guestTypeDocument,
                 String guestPassport, String isBuyer, String buyerEmail, String buyerPhone) {
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

    // Геттеры и сеттеры
    public long getGuestID() { return guestID; }
    public void setGuestID(long guestID) { this.guestID = guestID; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public String getGuestFirstName() { return guestFirstName; }
    public void setGuestFirstName(String guestFirstName) { this.guestFirstName = guestFirstName; }

    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }

    public String getGuestGender() { return guestGender; }
    public void setGuestGender(String guestGender) { this.guestGender = guestGender; }

    public String getGuestBirthday() { return guestBirthday; }
    public void setGuestBirthday(String guestBirthday) { this.guestBirthday = guestBirthday; }

    public String getGuestCitizenship() { return guestCitizenship; }
    public void setGuestCitizenship(String guestCitizenship) { this.guestCitizenship = guestCitizenship; }

    public String getGuestTypeDocument() { return guestTypeDocument; }
    public void setGuestTypeDocument(String guestTypeDocument) { this.guestTypeDocument = guestTypeDocument; }

    public String getGuestPassport() { return guestPassport; }
    public void setGuestPassport(String guestPassport) { this.guestPassport = guestPassport; }

    public boolean isBuyerBoolean() {
        return Boolean.parseBoolean(isBuyer);
    }
    public void setBuyerBoolean(boolean buyer) {
        this.isBuyer = String.valueOf(buyer);
    }

    public String getBuyerEmail() { return buyerEmail; }
    public void setBuyerEmail(String buyerEmail) { this.buyerEmail = buyerEmail; }

    public String getBuyerPhone() { return buyerPhone; }
    public void setBuyerPhone(String buyerPhone) { this.buyerPhone = buyerPhone; }
}