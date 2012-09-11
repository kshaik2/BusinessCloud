package com.infor.cloudsuite.platform;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.RepositoryMetadata;

import com.infor.cloudsuite.platform.jpa.ExtendedJpaRepository;

/**
 * User: bcrow
 * Date: 11/10/11 3:03 PM
 */
public class ExtJpaRepositoryFactory extends JpaRepositoryFactory {

    /**
     * Creates a new {@link org.springframework.data.jpa.repository.support.JpaRepositoryFactory}.
     *
     * @param entityManager must not be {@literal null}
     */
    public ExtJpaRepositoryFactory(EntityManager entityManager) {
        super(entityManager);
    }

    @SuppressWarnings({"unchecked"})
    @Override
    protected <T, ID extends Serializable> JpaRepository<?, ?> getTargetRepository(RepositoryMetadata metadata, EntityManager entityManager) {
        JpaEntityInformation entityInformation = getEntityInformation(metadata.getDomainType());
        return new ExtendedJpaRepository(entityInformation, entityManager);
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        return ExtendedJpaRepository.class;
    }

}
