package com.example.travel.services;

import com.example.travel.models.HotelFeature;
import com.example.travel.models.RoomFeature;
import com.example.travel.models.RoomFeatureRelation;
import org.hibernate.Session;

import java.util.List;

public class RoomFeatureRelationService extends BaseService<RoomFeatureRelation> {
    public RoomFeatureRelationService() {
        super(RoomFeatureRelation.class);
    }

    public List<RoomFeature> getAllRoomFeatureByHotelId(int idHotel) {
        String hql = "select roomFeature from RoomFeatureRelation where room.hotel.idHotel = :ID_HOTEL";

        try(Session session = BaseService.sessionFactory.openSession()) {
            return session.createQuery(hql, RoomFeature.class).setParameter("ID_HOTEL", idHotel).list();
        }
    }
}
