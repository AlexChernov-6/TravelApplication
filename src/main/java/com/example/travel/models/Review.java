package com.example.travel.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews", schema = "public")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_review")
    private long idReview;
    @ManyToOne @JoinColumn(name = "id_hotel")
    private Hotel hotel;
    @ManyToOne @JoinColumn(name = "id_user")
    private User user;
    @Column(name = "rating")
    private short rating;
    @Column(name = "comment")
    private String comment;
    @Column(name = "created_at")
    private LocalDateTime cratedAt;

    public Review() {}

    public Review(long idReview, Hotel hotel, User user, short rating, String comment, LocalDateTime cratedAt) {
        this.idReview = idReview;
        this.hotel = hotel;
        this.user = user;
        this.rating = rating;
        this.comment = comment;
        this.cratedAt = cratedAt;
    }

    public long getIdReview() {
        return idReview;
    }

    public void setIdReview(long idReview) {
        this.idReview = idReview;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public short getRating() {
        return rating;
    }

    public void setRating(short rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCratedAt() {
        return cratedAt;
    }

    public void setCratedAt(LocalDateTime cratedAt) {
        this.cratedAt = cratedAt;
    }
}
