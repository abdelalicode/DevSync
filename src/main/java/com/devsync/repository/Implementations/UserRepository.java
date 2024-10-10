package com.devsync.repository.Implementations;

import com.devsync.domain.entity.User;
import jakarta.persistence.*;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class UserRepository {

    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("devin");
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction transaction = entityManager.getTransaction();


    public boolean save(User user) {
        boolean isSaved = false;
        try {
            transaction.begin();

            entityManager.persist(user);

            transaction.commit();
            isSaved = true;
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
        return isSaved;

    }



    public List<User> findAll() {

        return entityManager.createQuery("SELECT u FROM User u", User.class).getResultList();
    }

    public User findById(int id) {
        return entityManager.find(User.class, id);
    }

    public Optional<User> findByEmail(String email) {
        try {
            User user = entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email)
                .getSingleResult();
            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public void delete(int id) {
        try {
            transaction.begin();
            User user = entityManager.find(User.class, id);
            if (user != null) {
                entityManager.remove(user);
            }
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

    public void update(User user) {
        try {
            transaction.begin();
            entityManager.merge(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        }


    }

}
