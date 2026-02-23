package com.example.travel.services;

import com.example.travel.util.HibernateConnection;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class BaseService<T> {
    protected static final SessionFactory sessionFactory = HibernateConnection.getSessionFactory();
    private Class<T> model;

    public BaseService(Class<T> model) {
        this.model = model;
    }

    public List<T> getAllRow() {
        try(Session session = sessionFactory.openSession()) {
            return session.createQuery("from " + model.getSimpleName(), model).list();
        }
    }

    public T getRowById(Long id) {
        try (Session session = sessionFactory.openSession()){
            return session.get(model, id);
        }
    }

    public void saveRow(T entity) {
        try (Session session = sessionFactory.openSession()){
            Transaction transaction = session.beginTransaction();
            session.persist(entity);
            transaction.commit();
        }
    }

    public void updateRow(T entity) {
        try (Session session = sessionFactory.openSession()){
            Transaction transaction = session.beginTransaction();
            session.update(entity);
            transaction.commit();
        }
    }

    public void deleteRow(T entity) {
        try (Session session = sessionFactory.openSession()){
            Transaction transaction = session.beginTransaction();
            session.remove(entity);
            transaction.commit();
        }
    }
}
