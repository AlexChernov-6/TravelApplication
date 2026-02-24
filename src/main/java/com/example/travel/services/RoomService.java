package com.example.travel.services;

import com.example.travel.models.Room;
import org.hibernate.Session;

public class RoomService extends BaseService<Room> {
    public RoomService() {
        super(Room.class);
    }

    public double getMinRoomPriceByHotelId(int hotelId) {
        String hql = "select roomPrice from Room where hotel.idHotel = :HOTEL_ID order by roomPrice asc limit 1";

        try(Session session = BaseService.sessionFactory.openSession()) {
            return session.createQuery(hql, Double.class).setParameter("HOTEL_ID", hotelId).uniqueResult();
        }
    }

    public double getMaxRoomPriceByHotelId(int hotelId) {
        String hql = "select roomPrice from Room where hotel.idHotel = :HOTEL_ID order by roomPrice desc limit 1";

        try(Session session = BaseService.sessionFactory.openSession()) {
            return session.createQuery(hql, Double.class).setParameter("HOTEL_ID", hotelId).uniqueResult();
        }
    }
}
