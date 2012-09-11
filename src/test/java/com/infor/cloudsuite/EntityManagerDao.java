package com.infor.cloudsuite;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

/**
 * Provide access and convenience to tests.
 * User: bcrow
 * Date: 10/25/11 1:36 PM
 */
@Repository
public class EntityManagerDao {
    @PersistenceContext
    private EntityManager entityManager;

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void flush() {
        entityManager.flush();
    }

    public void clear() {
        entityManager.clear();
    }
}
