package com.devsync.repository.Implementations;

import com.devsync.domain.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.util.List;

public class UserRepository {

    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("devin");
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction transaction = entityManager.getTransaction();

    public void save(User user) {

        try {
            transaction.begin();

            entityManager.persist(user);

            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
            if (entityManagerFactory.isOpen()) {
                entityManagerFactory.close();
            }
        }

    }


    public List<User> findAll() {
        return null;
    }



}
