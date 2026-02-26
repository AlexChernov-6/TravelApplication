package com.example.travel.services;

import com.example.travel.models.Room;
import org.hibernate.Session;

public class RoomService extends BaseService<Room> {
    public RoomService() {
        super(Room.class);
    }

    public double getMinRoomPriceByHotelId(int hotelId) {
        String hql = "select min(roomPrice) from Room where hotel.idHotel = :HOTEL_ID";

        try(Session session = BaseService.sessionFactory.openSession()) {
            return session.createQuery(hql, Double.class).setParameter("HOTEL_ID", hotelId).uniqueResult();
        }
    }
}
