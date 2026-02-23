package com.example.travel.models;

import jakarta.persistence.*;

@Entity
@Table(name = "room_features", schema = "public")
public class RoomFeature {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_room_feature")
    private int idRoomFeature;
    @Column(name = "feature_name")
    private String featureName;

    public RoomFeature() {}

    public RoomFeature(int idRoomFeature, String featureName) {
        this.idRoomFeature = idRoomFeature;
        this.featureName = featureName;
    }

    public int getIdRoomFeature() {
        return idRoomFeature;
    }

    public void setIdRoomFeature(int idRoomFeature) {
        this.idRoomFeature = idRoomFeature;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }
}
