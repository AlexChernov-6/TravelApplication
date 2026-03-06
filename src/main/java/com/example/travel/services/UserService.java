package com.example.travel.services;

import com.example.travel.models.User;
import com.example.travel.util.CryptoConverter;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;

public class UserService extends BaseService<User> {
    public UserService() {
        super(User.class);
    }

    public User findByEmail(String email) {
        byte[] encryptedEmail = encryptEmail(email);
        try (Session session = sessionFactory.openSession()) {
            String sql = "SELECT * FROM users WHERE user_email = ?";
            NativeQuery<User> query = session.createNativeQuery(sql, User.class);
            query.setParameter(1, encryptedEmail);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private byte[] encryptEmail(String email) {
        CryptoConverter converter = new CryptoConverter();
        return converter.convertToDatabaseColumn(email);
    }

}
