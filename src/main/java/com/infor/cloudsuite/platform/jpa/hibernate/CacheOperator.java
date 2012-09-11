package com.infor.cloudsuite.platform.jpa.hibernate;

import java.io.Serializable;

import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.infor.cloudsuite.dto.EvictionDto;

@Repository
public class CacheOperator {
    private static final Logger log = LoggerFactory.getLogger(CacheOperator.class);
    
    @PersistenceContext
    private EntityManager manager;
    

    private Cache getCache() {
    	Cache cache=manager.getEntityManagerFactory().getCache();
    	if (cache==null) {
    		throw new IllegalStateException("Cache manager null!");
    	}
    	return cache;
    }
    
    //evict of type class
    public void evict(Class cls) {
    	
    	getCache().evict(cls);
    	
    }
    
    //evict all
    public void evictAll() {
    	log.debug("Clearing the Entity cache.");
        getCache().evictAll();
    }

    public void evictAllQueryCache() {
        log.debug("Clearing the query cache.");
        final SessionFactory sessionFactory = manager.unwrap(Session.class).getSessionFactory();
//        sessionFactory.getCache().evictCollectionRegions();
        sessionFactory.getCache().evictDefaultQueryRegion();
        sessionFactory.getCache().evictQueryRegions();
    }

    //evict one
    
    public void evict(Class cls, Object primaryKey) {
    	
    	getCache().evict(cls, primaryKey);
    }
    
    public void evict(EvictionDto evictionDto) throws ClassNotFoundException {
    	
    	Class cls=Class.forName(evictionDto.getClassName());	
	
    	if (evictionDto.getPrimaryKey() != null) {
    		evict(cls,evictionDto.getPrimaryKey());
    	} else {
    		evict(cls);
    	}
   
    }	

    
    public boolean cacheContainsEntity(Class entityClass, Serializable key) {
    	
    	return getCache().contains(entityClass, key);
    	
    }
   
} 
