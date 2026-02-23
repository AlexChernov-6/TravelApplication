package com.example.travel.services;

import com.example.travel.models.Hotel;
import org.hibernate.Session;

import java.util.List;

public class HotelService extends BaseService<Hotel> {
    public HotelService() {
        super(Hotel.class);
    }

    public List<Hotel> getAllHotelsByDirectionID(int directionID) {
        String hql = "from Hotel where direction.idDirection = :DIRECTION_ID";

        try(Session session = BaseService.sessionFactory.openSession()) {
            return session.createQuery(hql, Hotel.class)
                    .setParameter("DIRECTION_ID", directionID)
                    .list();
        }
    }
}
