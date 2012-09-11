package com.infor.cloudsuite.service;

import java.util.ArrayList;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.infor.cloudsuite.dao.AmazonCredentialsDao;
import com.infor.cloudsuite.dao.TokenStoreDao;
import com.infor.cloudsuite.dao.TrialEnvironmentDao;
import com.infor.cloudsuite.dao.TrialInstanceDao;
import com.infor.cloudsuite.dao.UserDao;
import com.infor.cloudsuite.dao.UserProductDao;
import com.infor.cloudsuite.dao.UserTrackingDao;
import com.infor.cloudsuite.dto.UserIdDto;
import com.infor.cloudsuite.entity.User;
import com.infor.cloudsuite.migration.Migrator;
import com.infor.cloudsuite.platform.CSWebApplicationException;
import com.infor.cloudsuite.platform.components.SettingsProvider;
import com.infor.cloudsuite.service.component.ImportExportComponent;
import com.infor.cloudsuite.service.component.ImportFileTypeEnum;

@Path("/superadmin")
@Service
public class SuperAdminService {
    private static final Logger logger = LoggerFactory.getLogger(SuperAdminService.class);

    @Resource
    private UserDao userDao;
    @Resource
    private UserProductDao userProductDao;
    @Resource
    private UserTrackingDao userTrackingDao;
    @Resource
    private TrialInstanceDao trialInstanceDao;
    @Resource
    private AmazonCredentialsDao amazonCredentialsDao;
    @Resource
    private TrialEnvironmentDao trialEnvironmentDao;
    @Resource
    private TokenStoreDao tokenStoreDao;
    @Resource
    private SeedService seedService;
    @Resource
    private SettingsProvider settingsProvider;
    @Resource
    private ImportExportComponent importExportComponent;
    
    @Path("/resetTrials")
    @PUT
    @Secured(StringDefs.ROLE_ADMIN)
    @Transactional
    public String resetTrials() {
        logger.warn("Resetting trial environments");
        trialEnvironmentDao.deleteAll();
        logger.info(seedService.seedTrialEnvironments());
        return "Done";
    }
	
  
    @Path("/deleteUser")
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)    
    @Secured(StringDefs.ROLE_ADMIN)
    @Transactional
    public void deleteUser(UserIdDto userDto) {
        logger.warn("Attempting to delete a user");
        if (userDto.getUserId() != null) {
            deleteUser(userDto.getUserId());
        } else if (userDto.getUserName() != null) {
            deleteUser(userDto.getUserName());
        } else {
            throw new CSWebApplicationException(Response.Status.BAD_REQUEST, "No user identification provided.");
        }
    }

    private void deleteUser(String userName) {
        logger.info("Deleting user by userName: " + userName);
        userName = userName.toLowerCase();
        User user = userDao.findByUsername(userName);
        deleteUser(user);
    }

    private void deleteUser(Long userId) {
        logger.info("Deleting user by Id: " + userId);
        User user = userDao.findById(userId);
        deleteUser(user);
    }

    private void deleteUser(User user) {
        if (user == null || user.isAnAdmin()) {
            String returnStr = user == null ? StringDefs.DELETE_USER_NOT_FOUND : StringDefs.DELETE_USER_ADMIN;
            throw new CSWebApplicationException(StringDefs.GENERAL_ERROR_CODE, returnStr);
        }

        userProductDao.deleteByUser(user);
        trialInstanceDao.deleteByUser(user);
        amazonCredentialsDao.deleteByUser(user);
        userTrackingDao.deleteDataByUser(user);
        tokenStoreDao.deleteByUser(user);
        userDao.delete(user);
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @Resource(name = "migrators")
    private ArrayList<Migrator> migrators;

    @Path("/runMigrations")
    @POST
    @Secured(StringDefs.ROLE_ADMIN)
    @Transactional
    public void migrateDatabase() {
    	
        if (migrators == null || migrators.size() == 0) {
            logger.info("No database migrations configured.");
        } else {
            logger.info("Executing {} database migrations.", migrators.size());
            for (Migrator migrator : migrators) {
                migrator.migrate();
            }
        }

    }
    
    @Path("/reseedKey")
    @POST
    @Secured(StringDefs.ROLE_ADMIN)
    @Consumes(MediaType.APPLICATION_JSON) 
    @Produces(MediaType.APPLICATION_JSON) 
    
    public String reseedKey(@QueryParam("importKey") String key) {
    
    	return importExportComponent.importFromJsonFile(settingsProvider.getSeedFileName(), ImportFileTypeEnum.IN_ARCHIVE, null,key);

    }
}
