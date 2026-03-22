package com.example.travel.util;

import com.example.travel.models.*;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class HibernateConnection {
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if(sessionFactory == null) {
            try (FileInputStream stream = new FileInputStream("config.properties")) {
                Properties properties = new Properties();
                properties.load(stream);
                sessionFactory = new Configuration().setProperties(properties)
                        .addAnnotatedClass(Direction.class)
                        .addAnnotatedClass(Hotel.class)
                        .addAnnotatedClass(HotelFeature.class)
                        .addAnnotatedClass(HotelFeatureRelation.class)
                        .addAnnotatedClass(MealPlan.class)
                        .addAnnotatedClass(RefundPolicy.class)
                        .addAnnotatedClass(PaymentMethod.class)
                        .addAnnotatedClass(Room.class)
                        .addAnnotatedClass(RoomFeature.class)
                        .addAnnotatedClass(RoomFeatureRelation.class)
                        .addAnnotatedClass(User.class)
                        .addAnnotatedClass(Review.class)
                        .buildSessionFactory();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
        return sessionFactory;
    }
}
