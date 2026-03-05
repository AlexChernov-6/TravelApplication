package com.example.travel.services;

import com.example.travel.models.Room;
import org.hibernate.Session;

import java.util.List;

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

    public List<Room> getAllRowByHotelId(int hotelId) {
        String hql = "from Room where hotel.idHotel = :HOTEL_ID";

        try(Session session = BaseService.sessionFactory.openSession()) {
            return session.createQuery(hql, Room.class).setParameter("HOTEL_ID", hotelId).list();
        }
    }

    public short getMaxRoomSleepingPlaces(int hotelId) {
        String hql = "select max(roomSleepingPlaces) from Room where hotel.idHotel = :HOTEL_ID";

        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(hql, Short.class).setParameter("HOTEL_ID", hotelId).uniqueResult();
        }
    }
}
