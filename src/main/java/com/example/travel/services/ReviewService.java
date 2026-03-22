package com.example.travel.services;

import com.example.travel.models.Review;
import org.hibernate.Session;

import java.util.List;

public class ReviewService extends BaseService<Review> {
    public ReviewService() {
        super(Review.class);
    }

    public Review getBestReview(int idHotel) {
        String queryHQL = "from Review where hotel.idHotel = :ID_HOTEL order by rating desc limit 1";

        try (Session session = BaseService.sessionFactory.openSession()) {
            return session.createQuery(queryHQL, Review.class).setParameter("ID_HOTEL", idHotel).uniqueResult();
        }
    }

    public List<Review> getAllReviewByHotelId(int idHotel) {
        String queryHQL = "from Review where hotel.idHotel = :ID_HOTEL";

        try (Session session = BaseService.sessionFactory.openSession()) {
            return session.createQuery(queryHQL, Review.class).setParameter("ID_HOTEL", idHotel).list();
        }
    }
}
