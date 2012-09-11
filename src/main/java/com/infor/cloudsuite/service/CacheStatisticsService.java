package com.infor.cloudsuite.service;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import com.infor.cloudsuite.dto.EvictionDto;
import com.infor.cloudsuite.platform.CSWebApplicationException;
import com.infor.cloudsuite.platform.jpa.hibernate.CacheOperator;
import com.infor.cloudsuite.platform.jpa.hibernate.StatisticsOperator;

@Path("/cachestats")
@Service
public class CacheStatisticsService {

	@Resource
	CacheOperator cacheOperator;
	
	@Resource
	StatisticsOperator statisticsOperator;
	
	
    @POST
    @Path("/evictAll")
    @Secured(StringDefs.ROLE_ADMIN)
    public void evictAll() {
    	
    	cacheOperator.evictAll();
    	
    	
    }
    
    @POST
    @Path("/clearStats")
    @Secured(StringDefs.ROLE_ADMIN)
    public void clearStats() {
    	statisticsOperator.clear();
    }
    
    @POST
    @Path("/evictWithClass")
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured("ROLE_ADMIN")
    public void evictWithClass(EvictionDto evictionDto) {
    	
    	try {
    		cacheOperator.evict(evictionDto);
    	} catch (ClassNotFoundException cnfe) {
    		throw new CSWebApplicationException(cnfe,StringDefs.GENERAL_ERROR_CODE,"Invalid class name specified for clear");
    	}
    	
    }

}