package com.infor.cloudsuite.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.infor.cloudsuite.dao.DeploymentStackDao;
import com.infor.cloudsuite.dao.DomainBlacklistDao;
import com.infor.cloudsuite.dao.ProductDao;
import com.infor.cloudsuite.dao.TrialInstanceDao;
import com.infor.cloudsuite.dao.UserDao;
import com.infor.cloudsuite.dao.UserTrackingDao;
import com.infor.cloudsuite.dto.AdminUserDto;
import com.infor.cloudsuite.dto.DeploymentStackDto;
import com.infor.cloudsuite.dto.LoginAgg;
import com.infor.cloudsuite.dto.ProductInfoDto;
import com.infor.cloudsuite.dto.UserFlagUpdateDto;
import com.infor.cloudsuite.dto.UserProductUpdateDto;
import com.infor.cloudsuite.dto.UserRoleUpdateDto;
import com.infor.cloudsuite.entity.DeploymentStack;
import com.infor.cloudsuite.entity.DeploymentState;
import com.infor.cloudsuite.entity.DomainBlacklist;
import com.infor.cloudsuite.entity.Role;
import com.infor.cloudsuite.entity.TrialInstance;
import com.infor.cloudsuite.entity.User;
import com.infor.cloudsuite.platform.CSWebApplicationException;
import com.infor.cloudsuite.platform.security.SecurityService;
import com.infor.cloudsuite.service.component.DeploymentServiceComponent;
import com.infor.cloudsuite.service.component.ImportExportComponent;
import com.infor.cloudsuite.service.component.ImportFileTypeEnum;
import com.infor.cloudsuite.service.component.RemoteV1ExportComponent;
import com.infor.cloudsuite.service.component.UserServiceComponent;
/**
 * User: bcrow
 * Date: 11/7/11 4:00 PM
 */
@Path("/admin")
@Service
public class AdminService {
    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);
  	
    @Resource
    private ProductDao productDao;
    @Resource
    private UserDao userDao;
	@Resource
    private TrialInstanceDao trialInstanceDao;
	@Resource
    private DeploymentStackDao deploymentStackDao;  
    @Resource
    private DomainBlacklistDao blacklistDao;
    @Resource
    private UserTrackingDao userTrackingDao;

    @Resource
    private SecurityService securityService;

    @Resource
    private UserServiceComponent userServiceComponent;
    
    @Resource
    private ImportExportComponent importExportComponent;
    
    @Resource
    private RemoteV1ExportComponent remoteV1ExportComponent;
    @Resource
    private DeploymentServiceComponent deploymentServiceComponent;
    
    private final Sort userSort = new Sort(Sort.Direction.ASC, "lastName", "firstName", "username");
    
    @GET
    @Path("/getUsers")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    @Secured(StringDefs.ROLE_SALES)
    public List<AdminUserDto> getAllUsers(@QueryParam("pageNum") Integer pageNum,
                                             @QueryParam("numPerPage") Integer numPerPage) {
        final PageRequest pageRequest = new PageRequest(pageNum, numPerPage, userSort);
        boolean isAdmin=securityService.getCurrentUser().isAdmin();
        Page<User> page = userDao.findAll(pageRequest);
        List<User> users = page.getContent();
       
        List<AdminUserDto> dtos = new ArrayList<>();
        logger.debug("Users: " + users.size());
        for (User user : users) {
        
        	if (!isAdmin && (user.isAnAdmin() || user.isSalesRole())) {
        		continue;
        	}
            LoginAgg agg = userTrackingDao.getLoginAgg(user.getId());
     
            if (agg == null) {
                agg = new LoginAgg(user.getId(), 0L, user.getCreatedAt());
            }
            List<DeploymentStack> deploymentStacks=deploymentStackDao.findByUserWithStateNotIn(user, DeploymentState.DEFAULT_EXCLUDES);
            List<DeploymentStackDto> deploymentsList=deploymentServiceComponent.getDtoListForStacks(deploymentStacks);
            List<TrialInstance> trialInstances=trialInstanceDao.findByUserId(user.getId());
            
            deploymentsList.addAll(deploymentServiceComponent.getDeploymentStackDtoListForTrialInstances(trialInstances));
       
            dtos.add(new AdminUserDto(user, deploymentsList, agg));
            
        }
        return dtos;
    }
    
    @GET
    @Path("/getProductInfo")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(readOnly = true)
    @Secured(StringDefs.ROLE_SALES)
    public List<ProductInfoDto> getProductInfo(){
        return productDao.findProductInfoDtos();
    }

    @POST
    @Path("/updateUserProduct")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional(propagation = Propagation.REQUIRED)
    @Secured(StringDefs.ROLE_SALES)
    public UserProductUpdateDto updateUserProduct(UserProductUpdateDto update) {
    	//guts moved to UserServiceComponent so it can be re-used
    	
    	return userServiceComponent.updateUserProduct(update);
    }

    @POST
    @Path("/setUserActive")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional(propagation = Propagation.REQUIRED)
    @Secured(StringDefs.ROLE_SALES)
    public Boolean setUserActive(UserFlagUpdateDto update) {
        User user = userDao.findOne(update.getUserId());
        if (user == null) {
            throw new CSWebApplicationException(StringDefs.GENERAL_ERROR_CODE, "User does not exist");
        }
        boolean sales = securityService.getCurrentUser().isSales();
        boolean superAdmin= securityService.getCurrentUser().isSuperAdmin();
        boolean admin=securityService.getCurrentUser().isAdmin();
        if ((sales && (user.isAnAdmin()||user.isSalesRole()))||(admin && !superAdmin && user.isASuperAdmin())) {
            //bdrAdmin cannot deactivate an admin account.
        	//admin cannot activate a superadmin account
            return user.getActive();
        }

        user.setActive(update.getStatus());
        userDao.save(user);
        return user.getActive();
    }

    @POST
    @Path("/setUserRole")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional(propagation = Propagation.REQUIRED)
    @Secured(StringDefs.ROLE_SALES)
    public Boolean setUserRole(UserRoleUpdateDto update) {
        if (!Role.ALLOWED_ROLES.contains(update.getNewRole())) {
            return false;
        }
        User user = userDao.findOne(update.getUserId());
        if (user == null) {
            throw new CSWebApplicationException(StringDefs.GENERAL_ERROR_CODE, "User does not exist");
        }
        final Set<Role> roles = user.getRoles();

        Role newRole = update.getNewRole();
        if (roles.contains(newRole)) {
            return false;
        } else {
            roles.clear();
            roles.add(newRole);
        }

        try {
            userDao.save(user);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @GET
    @Path("/getBlacklist")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    @Secured(StringDefs.ROLE_ADMIN)
    public List<DomainBlacklist> getBlacklist() {
        return blacklistDao.findAll();
    }


    @POST
    @Path("/addBlacklist")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional(propagation = Propagation.REQUIRED)
    @Secured(StringDefs.ROLE_ADMIN)
    public DomainBlacklist addBlacklist(DomainBlacklist blacklist) {
        blacklist.setDomain(blacklist.getDomain().toLowerCase());
        DomainBlacklist existing = blacklistDao.findByDomain(blacklist.getDomain());
        if (existing != null) {
            return existing;
        }

        return blacklistDao.save(blacklist);
    }

    @POST
    @Path("/deleteBlacklist")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional(propagation = Propagation.REQUIRED)
    @Secured(StringDefs.ROLE_ADMIN)
    public DomainBlacklist deleteBlacklist(DomainBlacklist blacklist) {
        if (blacklist == null || (blacklist.getDomain() == null && blacklist.getId() == null)) {
            return blacklist;
        }
        final DomainBlacklist existing;
        if (blacklist.getId() != null) {
            existing = blacklistDao.findOne(blacklist.getId());
        } else {
            existing = blacklistDao.findByDomain(blacklist.getDomain().toLowerCase());
        }
        if (existing == null) {
            return blacklist;
        }

        blacklistDao.delete(existing);
        return existing;
    }
    
    @POST
    @Path("/importFromJsonData")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional(propagation = Propagation.REQUIRED)
    @Secured(StringDefs.ROLE_ADMIN)
    public String importFromJsonData(HashMap<String,Object> jsonMap){

    	return importExportComponent.importFromJsonData(jsonMap);
    }

    @POST
    @Path("/importFromJsonFile")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(propagation = Propagation.REQUIRED)
    @Secured(StringDefs.ROLE_ADMIN)
    public String importFromJsonFile(@QueryParam("filename") String filename, @QueryParam("fileType") ImportFileTypeEnum fileTypeEnum, @QueryParam("bucket") String bucket) {
    
    	return importExportComponent.importFromJsonFile(filename, fileTypeEnum,bucket);
    }

    @GET
    @Path("/exportRequiredData")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    @Secured(StringDefs.ROLE_ADMIN)
    public String exportRequiredData(@Context HttpServletResponse response,@QueryParam("filename") String filename,@QueryParam("overwriteConflicts") boolean overwriteConflicts, @QueryParam("format") boolean format){

    	return importExportComponent.exportRequiredData(response, filename,overwriteConflicts,format);
    }
    
    @GET
    @Path("/exportSeedData")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    @Secured(StringDefs.ROLE_ADMIN)
    public String exportSeedData(@Context HttpServletResponse response, @QueryParam("filename") String filename, @QueryParam("format") boolean format) {

    	return importExportComponent.exportSeedData(response,filename,format);
    	
    	
    }
    
    @GET
    @Path("/getRemoteVersionOneExport")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    @Secured(StringDefs.ROLE_ADMIN)
    public String getRemoteVersionOneExport(@Context HttpServletResponse response, @QueryParam("filename") String filename) {
    	
    	return remoteV1ExportComponent.getFormattedV1JsonString(response,filename);
    	
    }

    @POST
    @Path("/importRemoteV1JsonData")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional(propagation = Propagation.REQUIRED)
    @Secured(StringDefs.ROLE_ADMIN)
    public String importRemoteV1JsonData(@Context HttpServletResponse response){
    	
    	return importExportComponent.importFromJsonString(getRemoteVersionOneExport(response,null));
    }

}
