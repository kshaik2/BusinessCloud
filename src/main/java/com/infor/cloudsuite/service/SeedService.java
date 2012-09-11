package com.infor.cloudsuite.service;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.infor.cloudsuite.dao.UserDao;
import com.infor.cloudsuite.entity.ProductVersion;
import com.infor.cloudsuite.entity.Region;
import com.infor.cloudsuite.platform.components.SettingsProvider;
import com.infor.cloudsuite.service.component.ImportExportComponent;
import com.infor.cloudsuite.service.component.ImportFileTypeEnum;

/**
 * User: bcrow
 * Date: 10/27/11 9:13 AM
 */
@Service
@Transactional(propagation = Propagation.REQUIRED)
@Path("/seedDB")
public class SeedService {
    private static final Logger logger = LoggerFactory.getLogger(SeedService.class);
    public static final String LINE = System.getProperty("line.separator");

    @Resource
    private UserDao userDao;
    @Resource
    private ImportExportComponent importExportComponent;
    @Resource
    private SettingsProvider settingsProvider;
    
    
    @GET
    public Response seedDatabase(@Context HttpServletRequest request) {
        final String remoteAddr = request.getRemoteAddr();
        logger.debug("Remote address: {}", remoteAddr);
        if (!"127.0.0.1".equals(remoteAddr)) {
            logger.debug("Remote address used: ", remoteAddr);
            return Response.ok("Cannot execute from a remote machine.").build();
        }

        seedDatabase(false);

        return Response.ok().build();

    }


    
    public void seedDatabase() {
    	seedDatabase(true);
    }
    
    public void seedDatabase(boolean check) {
    	
    	boolean forceUpdate=settingsProvider.isForceUpdateWithSeedData();
    	
    	if (check && userDao.count()>0 && !forceUpdate)
    	{
    		logger.info("SKIPPING the re-seeding of the existing (non-empty) database ...");
    		return;
    	}

    	
    	logger.info("Seeding database..."+(forceUpdate?"(forced update!)":""));
    	String outputFromImport=null;
    	try {
    		outputFromImport=importExportComponent.importFromJsonFile(settingsProvider.getSeedFileName(), ImportFileTypeEnum.IN_ARCHIVE, null);
    	} catch (Exception e) {
    		logger.error("Encountered exception seeding...",e);
  
    	}
    	logger.info("Output from seeding:\n"+outputFromImport);
    }

    public String seedTrialEnvironments() {
    
    	return importExportComponent.importFromJsonFile(settingsProvider.getSeedFileName(), ImportFileTypeEnum.IN_ARCHIVE, null,"TRIAL_ENVIRONMENTS");
    }



	public void seedProductTrials(ProductVersion currProductVersion, Region region) {
		
		importExportComponent.markEnvironmentsAvailable(currProductVersion,region);
		
		logger.info("seedProductTrials called for productVersion:"+currProductVersion.getName()+" (Product short name:"+currProductVersion.getProduct().getShortName()+") and region:"+region.getShortName()+"((NOT ACTUALLY RE-SEEDED!!))");
		
	}
    

}
