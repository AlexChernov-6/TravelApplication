package com.example.travel.services;

import com.example.travel.models.HotelFeature;
import com.example.travel.models.HotelFeatureRelation;
import org.hibernate.Session;

import java.util.List;

public class HotelFeatureRelationService extends BaseService<HotelFeatureRelation>{
    public HotelFeatureRelationService() {
        super(HotelFeatureRelation.class);
    }

    public List<HotelFeature> getAllHotelFeatureByHotelId(int idHotel) {
        String hql = "select hotelFeature from HotelFeatureRelation where hotel.idHotel = :ID_HOTEL";

        try(Session session = BaseService.sessionFactory.openSession()) {
            return session.createQuery(hql, HotelFeature.class).setParameter("ID_HOTEL", idHotel).list();
        }
    }
}
