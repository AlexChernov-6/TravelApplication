package com.example.travel.models;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "refund_policies", schema = "public")
public class RefundPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_refund_policy")
    private int idRefundPolicy;
    @Column(name = "refund_name")
    private String refundName;

    public RefundPolicy() {}

    public RefundPolicy(int idRefundPolicy, String refundName) {
        this.idRefundPolicy = idRefundPolicy;
        this.refundName = refundName;
    }

    public int getIdRefundPolicy() {
        return idRefundPolicy;
    }

    public void setIdRefundPolicy(int idRefundPolicy) {
        this.idRefundPolicy = idRefundPolicy;
    }

    public String getRefundName() {
        return refundName;
    }

    public void setRefundName(String refundName) {
        this.refundName = refundName;
    }

    @Override
    public String toString() {
        return refundName;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        RefundPolicy that = (RefundPolicy) object;
        return idRefundPolicy == that.idRefundPolicy && Objects.equals(refundName, that.refundName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idRefundPolicy, refundName);
    }
}
