package com.example.travel.models;

import jakarta.persistence.*;

@Entity
@Table(name = "hotel_features", schema = "public")
public class HotelFeature {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_hotel_feature")
    private int idHotelFeature;
    @Column(name = "feature_name")
    private String featureName;

    public HotelFeature() {}

    public HotelFeature(int idHotelFeature, String featureName) {
        this.idHotelFeature = idHotelFeature;
        this.featureName = featureName;
    }

    public int getIdHotelFeature() {
        return idHotelFeature;
    }

    public void setIdHotelFeature(int idHotelFeature) {
        this.idHotelFeature = idHotelFeature;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }
}
