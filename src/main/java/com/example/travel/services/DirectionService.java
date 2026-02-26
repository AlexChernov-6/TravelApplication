package com.example.travel.services;

import com.example.travel.models.Direction;
import org.hibernate.Session;

public class DirectionService extends BaseService<Direction> {
    public DirectionService() {
        super(Direction.class);
    }

    public Double getMaxRoomPriceByDirectionId(int idDirection) {
        String hql = "select max(roomPrice) from Room where hotel.direction.idDirection = :ID_DIRECTION";

        try(Session session = BaseService.sessionFactory.openSession()) {
            Double result = session.createQuery(hql, Double.class).setParameter("ID_DIRECTION", idDirection).uniqueResult();
            return result == null ? 0.0 : result;
        }
    }
}
