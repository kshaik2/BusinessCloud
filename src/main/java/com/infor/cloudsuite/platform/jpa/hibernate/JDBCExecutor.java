package com.infor.cloudsuite.platform.jpa.hibernate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.infor.cloudsuite.platform.jpa.CSDataAccessException;

/**
 * User: bcrow
 * Date: Feb 18, 2011 8:51:44 AM
 */
@Component
@Transactional(propagation = Propagation.SUPPORTS)
public class JDBCExecutor {

    @PersistenceContext
    private EntityManager em;

    /**
     * Execute work allows access to the underlying JDBC connection through the Work
     * Object. Wrapping some functionality in a work object and passing to this method
     * can be a performance optimization while still allowing the EnitityManager to handle
     * the Connection lifecycle.
     * @param work Object containing some code to run against a JDBC connection.
     */
    public void executeQuery(Work work) {
        try {
            em.unwrap(Session.class).doWork(work);
        } catch (HibernateException | PersistenceException e) {
            throw new CSDataAccessException("Persistence Exception", e);
        }

    }
}
