package com.infor.cloudsuite.platform.jpa.hibernate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.hibernate.stat.Statistics;
import org.springframework.stereotype.Repository;

@Repository
public class StatisticsOperator {

    @PersistenceContext
    private EntityManager manager;
 
    private Statistics getStatistics() {

        Statistics statistics = manager.unwrap(Session.class).getSessionFactory().getStatistics();

        if (statistics == null) {
    		throw new IllegalStateException("Statistics manager null!");
    	}
    	
    	return statistics;
    }
    
    public void clear() {
    	getStatistics().clear();
    }
    
    public long getSecondLevelCacheHitCount() {
    	return getStatistics().getSecondLevelCacheHitCount();
    }

    public long getSecondLevelCacheMissCount() {
    	return getStatistics().getSecondLevelCacheMissCount();
    	
    }
    
    public long getSecondLevelCachePutCount() {
    	return getStatistics().getSecondLevelCachePutCount();
    }
    
    public long getQueryCacheHits() {
    	return getStatistics().getQueryCacheHitCount();
    }
    
    public String[] getSecondLevelCacheRegionNames() {
    	return getStatistics().getSecondLevelCacheRegionNames();
    }
    
    public long getRegionHitCount(String regionName) {
    	
    	return getStatistics().getSecondLevelCacheStatistics(regionName).getHitCount();
    	
    }
    
    public long getRegionMissCount(String regionName){
    	
    	return getStatistics().getSecondLevelCacheStatistics(regionName).getMissCount();
    }
    
    public long getRegionSizeInMemory(String regionName) {
    	return getStatistics().getSecondLevelCacheStatistics(regionName).getSizeInMemory();
    }
    

    public long getEntityDeleteCount(String entityName) {
    	return getStatistics().getEntityStatistics(entityName).getDeleteCount();
    }
    
    public long getEntityUpdateCount(String entityName) {
    	return getStatistics().getEntityStatistics(entityName).getUpdateCount();
    }
    
    public long getEntityInsertCount(String entityName) {
    	return getStatistics().getEntityStatistics(entityName).getInsertCount();
    }
    
    public long getEntityFetchCount(String entityName) {
    	return getStatistics().getEntityStatistics(entityName).getFetchCount();
    }
    
    public long getEntityLoadCount(String entityName) {
    	
    	return getStatistics().getEntityStatistics(entityName).getLoadCount();

    }
    
    public long getEntityOptimisticFailureCount(String entityName) {
    	
    	return getStatistics().getEntityStatistics(entityName).getOptimisticFailureCount();
    }
    
    public void log() {
    	getStatistics().logSummary();
    }

    public long getQueryCacheMisses() {
        return getStatistics().getQueryCacheMissCount();
    }

    public long getQueryCachePuts() {
        return getStatistics().getQueryCachePutCount();
    }
}
