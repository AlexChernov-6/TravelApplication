package com.example.travel.models;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "payment_methods", schema = "public")
public class PaymentMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_payment_method")
    private int idPaymentMethod;
    @Column(name = "payment_name")
    private String paymentName;

    public PaymentMethod() {}

    public PaymentMethod(int idPaymentMethod, String paymentName) {
        this.idPaymentMethod = idPaymentMethod;
        this.paymentName = paymentName;
    }

    public int getIdPaymentMethod() {
        return idPaymentMethod;
    }

    public void setIdPaymentMethod(int idPaymentMethod) {
        this.idPaymentMethod = idPaymentMethod;
    }

    public String getPaymentName() {
        return paymentName;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;
    }

    @Override
    public String toString() {
        return paymentName;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        PaymentMethod that = (PaymentMethod) object;
        return idPaymentMethod == that.idPaymentMethod && Objects.equals(paymentName, that.paymentName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPaymentMethod, paymentName);
    }
}
