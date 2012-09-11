package com.infor.cloudsuite.platform;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

/**
 * User: bcrow
 * Date: 11/10/11 3:51 PM
 */
public class ExtJpaRepositoryFactoryBean<T extends JpaRepository<S, ID>, S, ID extends Serializable>
                extends JpaRepositoryFactoryBean<T, S, ID> {

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        return new ExtJpaRepositoryFactory(entityManager);
    }
}
