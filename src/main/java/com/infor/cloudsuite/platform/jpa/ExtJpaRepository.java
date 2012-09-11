package com.infor.cloudsuite.platform.jpa;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Interface to add some more useful and commonly used
 * {@link javax.persistence.EntityManager} methods.
 * User: bcrow
 * Date: 11/10/11 1:54 PM
 */
public interface ExtJpaRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

    /**
     * Retrives an entity by its primary key.
     * {@link org.springframework.data.repository.CrudRepository#findOne(java.io.Serializable)}
     *
     * @param id Serializable id value.
     * @return the entity with the given primary key or {@code null} if none
     *         found
     * @throws IllegalArgumentException if primaryKey is {@code null}
     */
    T findById(ID id);

    /**
     * Get an instance, whose state may be lazily fetched.
     * If the requested instance does not exist in the database null is returned.
     *
     * @param id primary key
     * @return the found entity instance, null if the entity is not found
     */
    T getReference(ID id);

    /**
     * Completely clear the session. Evict all loaded instances and cancel all pending saves, updates and deletions
     */
    void clear();

    /**
     * Remove the given entity from the persistence context, causing
     * a managed entity to become detached.  Unflushed changes made
     * to the entity if any (including removal of the entity),
     * will not be synchronized to the database.  Entities which
     * previously referenced the detached entity will continue to
     * reference it.
     *
     * @param entity entity instance
     * @throws IllegalArgumentException if the instance is not an
     *                                  entity
     */
    void detach(T entity);
}
