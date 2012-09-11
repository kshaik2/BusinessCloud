package com.infor.cloudsuite.platform.jpa.hibernate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;

import org.hibernate.Cache;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.ejb.HibernateEntityManagerFactory;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.internal.StatelessSessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Operates on a Hibernate StatelessSession which bybasses all session caching entity objects.
 * This is most useful for bulk operations where caching of objects has no usefulness.
 *
 * When using the 2nd Level cache, remember to evict any updated entities or collections from
 * the cache. Failing to do so may result in cache inconsistency, possibly stretching across
 * clusters.
 *
 * The close method must be called on an instance of this class even when used through sping,
 * due to creation of a new statelessSession within its init method.
 *
 * Use this class with extreme caution. This class is a performance class for bulk operations.
 * By design, it does not interact normally with the session cache or the second level cache.
 * Also, it creates resources for its own(statelessSession) use that are not managed by the
 * container.
 *
 * Users: bcrow
 * Date: Jun 18, 2010 3:06:10 PM
 */
@Repository
@Scope(value = "prototype")
@SuppressWarnings({"unchecked"})
@Transactional(propagation = Propagation.SUPPORTS)
public class BulkOperator {
    private static final Logger log = LoggerFactory.getLogger(BulkOperator.class);
    
    @PersistenceContext
    private EntityManager manager;
    private StatelessSession statelessSession;
    private org.hibernate.Cache cache;

    public BulkOperator() {
    }

    /**
     * Constructor for use outside of Spring or for configuring with a
     * different EntityManager.
     * Init must be called manually after construction.
     * @param manager entity manager to use.
     */
    public BulkOperator(EntityManager manager) {
        this.manager = manager;
    }

    /**
     * Initialization of the stateless session needed for this class.
     */
    @PostConstruct
    public void init() {
        log.trace("BulkOperator -- initialize.");
        EntityManagerFactory emf = manager.getEntityManagerFactory();
        if (emf instanceof HibernateEntityManagerFactory) {
            HibernateEntityManagerFactory hemf = (HibernateEntityManagerFactory) emf;
//            Session session = hemf.getSessionFactory().getCurrentSession();
            final SessionFactory sessionFactory = hemf.getSessionFactory();
            statelessSession = sessionFactory.openStatelessSession();
            cache = sessionFactory.getCache();
            SessionFactoryImpl sfi = (SessionFactoryImpl) ((HibernateEntityManagerFactory) emf).getSessionFactory();
            log.debug("batch size = " + sfi.getSettings().getJdbcBatchSize());
            log.debug("fetch size = " + sfi.getSettings().getJdbcFetchSize());
        }
        //maybe throw exception if not a HibernateEntityManagerFactory.
    }

    /**
     * Retrieves on Entity of the given type with the passed in id.
     * Does not cache the retrieved entity.
     * @param entityClass Entity class
     * @param id the id of the entity to retrieve
     * @param <T> Entity Type
     * @return the Retrieved entity.
     */
    public <T>T getEntity(Class<T> entityClass, Serializable id) {
        return (T) statelessSession.get(entityClass, id);
    }

    /**
     * Retrieves on Entity of the given type with the passed in id.
     * Does not cache the retrieved entity.
     * @param entityClass Entity class
     * @param id the id of the entity to retrieve
     * @param lockMode Lock mode to use for retrieving.
     * @param <T> Entity Type
     * @return the Retrieved entity.
     */
    public <T> T getEntity(Class<T> entityClass, Serializable id, LockMode lockMode) {
        return (T) statelessSession.get(entityClass, id, lockMode);
    }

    /**
     * Retrieves on Entity of the given type with the passed in id.
     * Does not cache the retrieved entity.
     * Gets the Entity with a WriteLock on the entity.
     * @param entityClass Entity class
     * @param id the id of the entity to retrieve
     * @param <T> Entity Type
     * @return the Retrieved entity.
     */
    public <T> T getEntityWriteLock(Class<T> entityClass, Serializable id) {
        return (T) statelessSession.get(entityClass, id, LockMode.PESSIMISTIC_WRITE);
    }

    /**
     * Inserts an Entity into the database.
     * Performs the id generation for the object if a generator for the object exists.
     * @param entity The Entity to insert
     * @return returns the id of the inserted object.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public <T, ID extends Serializable> ID insert(T entity) {
        return (ID) statelessSession.insert(entity);
    }

    /**
     * Deletes an Entity from the Database.
     * @param entity Entity to delete
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public <T> void delete(T entity) {
        statelessSession.delete(entity);
    }

    /**
     * Retrieve a ScrollableResults set from the statelessSession.
     * Forward Scrolling only.
     * @param queryStr the query string
     * @param params parameters for the query
     * @return a ScrollableResults set.
     */
    public ScrollableResults getBulkResultSet(String queryStr, Object ... params) {
        Query query = statelessSession.createQuery(queryStr);
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                Object param = params[i];
                query.setParameter(i, param);
            }
        }
        return query.scroll(ScrollMode.FORWARD_ONLY);
    }

    /**
     * Get a list of Entity objects from the Database.
     * @param queryStr the Query string
     * @param start start posisition
     * @param size chunck size
     * @param params query parameters
     * @param <T> Entity type
     * @return List of Entity objects
     */
    public <T> List<T> getBulkList(String queryStr, int start, int size,Object ... params) {
        Query query = statelessSession.createQuery(queryStr);
        query.setFirstResult(start);
        if (size > 0) {
            query.setMaxResults(size);
        }

        if (params != null) {
            for (int i = 1; i <= params.length; i++) {
                Object param = params[i];
                query.setParameter(i, param);
            }
        }
        return query.list();
    }

    /**
     * Insert a Collection of Entity objects.
     * @param inserts collection of DataEntities
     * @param <T> Entity type
     * @return number of inserts
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public <T, ID extends Serializable> List<ID> insertBulk(Collection<T> inserts) {
        List<ID> ids = new ArrayList<>(inserts.size());
        for (T insert : inserts) {
            ids.add((ID) insert(insert));
        }
        return ids;
    }

    /**
     * Insert a Collection of Entity objects.
     * @param inserts collection of DataEntities
     * @param ids List of Ids that were inserted.
     * @param <T> Entity type
     * @return number of inserts
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public <T, ID extends Serializable> int insertBulk(Collection<T> inserts, List<ID> ids) {
        int cnt = 0;
        for (T insert : inserts) {
            ids.add((ID) insert(insert));
            cnt++;
        }
        flush();
        return cnt;
    }

    /**
     * Udpate a collection of Entity objects.
     * @param updates Collection of Entity Objects
     * @param <T> Entity Type
     * @return number of Updates.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public <T> int updateBulk(Collection<T> updates) {
        int cnt = 0;
        for (T update : updates) {
            statelessSession.update(update);
            cnt++;
        }
        flush();
        return cnt;
    }

    public void flush() {
        ((StatelessSessionImpl)statelessSession).getTransactionCoordinator().getJdbcCoordinator().executeBatch();
    }

    /**
     * Delete a collection of Entity objects
     * @param deletes collecion of Entity objects
     * @param <T> Entity Type
     * @return number of Deletes
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public <T> int deleteBulk(Collection<T> deletes) {
        int cnt = 0;
        for (T delete : deletes) {
            statelessSession.delete(delete);
            cnt++;
        }
        flush();
        return cnt;
    }

    /**
     * execute an update/delete query.
     * Uses the Hibernate Query engine. (HQL is slightly different than JPQL is some instances.)
     * @param queryStr the query string
     * @param params parameters for the query
     * @return number of updated records.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public int updateByQuery(String queryStr, Object ... params) {
        Query query = statelessSession.createQuery(queryStr);
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                Object param = params[i];
                //Hibernate Queries are numbered from 0.
                query.setParameter(i, param);
            }
        }
        return query.executeUpdate();
    }

    /**
     * Get the Second level cache for manipulation.
     *
     * @return the Second level cache.
     */
    protected Cache getCache() {
        return cache;
    }

    /**
     * Close the underlying statelessSession
     */
    public void close() {
        if (statelessSession != null) {

            statelessSession.close();
        }
    }
}
