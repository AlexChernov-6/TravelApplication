package com.example.travel.models;

import jakarta.persistence.*;

@Entity
@Table(name = "room_feature_relations", schema = "public")
public class RoomFeatureRelation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_room_feature_relation")
    private long idRoomFeatureRelation;
    @ManyToOne @JoinColumn(name = "id_rooms")
    private Room room;
    @ManyToOne @JoinColumn(name = "id_room_feature")
    private RoomFeature roomFeature;

    public RoomFeatureRelation() {}

    public RoomFeatureRelation(long idRoomFeatureRelation, Room room, RoomFeature roomFeature) {
        this.idRoomFeatureRelation = idRoomFeatureRelation;
        this.room = room;
        this.roomFeature = roomFeature;
    }

    public long getIdRoomFeatureRelation() {
        return idRoomFeatureRelation;
    }

    public void setIdRoomFeatureRelation(long idRoomFeatureRelation) {
        this.idRoomFeatureRelation = idRoomFeatureRelation;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public RoomFeature getRoomFeature() {
        return roomFeature;
    }

    public void setRoomFeature(RoomFeature roomFeature) {
        this.roomFeature = roomFeature;
    }
}
