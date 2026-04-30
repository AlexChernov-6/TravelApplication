package com.example.travel.services;

import com.example.travel.models.Order;
import com.example.travel.util.HibernateConnection;
import org.hibernate.Session;

import java.util.List;

public class OrderService extends BaseService<Order> {
    public OrderService() {
        super(Order.class);
    }

    public List<Order> getAllOrderByUserId(long userId) {
        String queryHQL = "from Order where user.userID = :USER_ID";

        try (Session session = HibernateConnection.getSessionFactory().openSession()) {
            return session.createQuery(queryHQL, Order.class).setParameter("USER_ID", userId).list();
        }
    }
}
