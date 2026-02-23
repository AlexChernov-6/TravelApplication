package com.example.travel.models;

import jakarta.persistence.*;

@Entity
@Table(name = "hotel_feature_relations", schema = "public")
public class HotelFeatureRelation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_hotel_feature_relation")
    private Long idHotelFeatureRelation;
    @ManyToOne @JoinColumn(name = "id_hotel")
    private Hotel hotel;
    @ManyToOne @JoinColumn(name = "id_hotel_feature")
    private HotelFeature hotelFeature;

    public HotelFeatureRelation() {}

    public HotelFeatureRelation(Long idHotelFeatureRelation, Hotel hotel, HotelFeature hotelFeature) {
        this.idHotelFeatureRelation = idHotelFeatureRelation;
        this.hotel = hotel;
        this.hotelFeature = hotelFeature;
    }

    public Long getIdHotelFeatureRelation() {
        return idHotelFeatureRelation;
    }

    public void setIdHotelFeatureRelation(Long idHotelFeatureRelation) {
        this.idHotelFeatureRelation = idHotelFeatureRelation;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public HotelFeature getHotelFeatures() {
        return hotelFeature;
    }

    public void setHotelFeatures(HotelFeature hotelFeature) {
        this.hotelFeature = hotelFeature;
    }
}
