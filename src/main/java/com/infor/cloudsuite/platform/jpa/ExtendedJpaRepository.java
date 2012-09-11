package com.infor.cloudsuite.platform.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * Implementation of {@link ExtJpaRepository}. Adds some more useful and commonly used
 * {@link EntityManager} methods.
 * User: bcrow
 * Date: 11/10/11 1:47 PM
 */
@Transactional(readOnly = true)
public class ExtendedJpaRepository<T, ID extends java.io.Serializable>
        extends SimpleJpaRepository<T, ID>
        implements ExtJpaRepository<T, ID> {

    private static final Logger logger = LoggerFactory.getLogger(ExtendedJpaRepository.class);

    private final JpaEntityInformation<T, ?> entityInformation;
    private final EntityManager entityManager;

    public ExtendedJpaRepository(Class<T> domainClass, EntityManager em) {
        this(JpaEntityInformationSupport.getMetadata(domainClass, em), em);
    }

    public ExtendedJpaRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {

        super(entityInformation, entityManager);
        this.entityInformation = entityInformation;
        this.entityManager = entityManager;
    }

    public T getReference(ID id) {
        Assert.notNull(id, "The given id must not be null!");
        T t = null;
        try {
            t = entityManager.getReference(entityInformation.getJavaType(), id);
        } catch (EntityNotFoundException enfx) {
            if (logger.isDebugEnabled()) {
                logger.debug("No results found for [{}]", id.toString(), enfx);
            } else {
                logger.info("No results found for [{}]", id.toString());
            }
        }
        return t;
    }

    @Override
    public T findById(ID id) {
        return findOne(id);
    }
    
    @Override
    public void clear() {
    	entityManager.unwrap(Session.class).clear();
    }
    
    @Override
    public void detach(T entity) {
    	entityManager.detach(entity);
    }
}
