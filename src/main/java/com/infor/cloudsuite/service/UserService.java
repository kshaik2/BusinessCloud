package com.infor.cloudsuite.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.infor.cloudsuite.dao.ProductDao;
import com.infor.cloudsuite.dao.ProductVersionDao;
import com.infor.cloudsuite.dao.RegionDao;
import com.infor.cloudsuite.dao.UserDao;
import com.infor.cloudsuite.dao.UserProductDao;
import com.infor.cloudsuite.dto.AmazonCredentialsDto;
import com.infor.cloudsuite.dto.DeploymentStackDto;
import com.infor.cloudsuite.dto.ProductUserProductDto;
import com.infor.cloudsuite.dto.RegistrationCompleteDto;
import com.infor.cloudsuite.dto.TrialDto;
import com.infor.cloudsuite.entity.Product;
import com.infor.cloudsuite.entity.ProductVersion;
import com.infor.cloudsuite.entity.Region;
import com.infor.cloudsuite.entity.User;
import com.infor.cloudsuite.entity.UserProduct;
import com.infor.cloudsuite.entity.UserProductKey;
import com.infor.cloudsuite.platform.CSWebApplicationException;
import com.infor.cloudsuite.platform.security.SecurityService;
import com.infor.cloudsuite.platform.security.SecurityUser;
import com.infor.cloudsuite.service.component.UserServiceComponent;

/**
 * User: bcrow
 * Date: 9/29/11 12:22 AM
 */
@Path("/user")
@Service
public class UserService {

	private final static Logger logger=LoggerFactory.getLogger(UserService.class);
    @Resource
    private UserDao userDao;
    @Resource
    private ProductDao productDao;
    @Resource
    private SecurityService securityService;
    @Resource
    private UserProductDao userProductDao;
    @Resource
    private RegionDao regionDao;

    @Resource
    private UserServiceComponent userServiceComponent;

    @Resource
    private ProductVersionDao productVersionDao;
    
    
    /**
     * Check to see if a username exists.
     *
     * @param username username to check
     * @return true if user exists.
     */
    @GET
    @Path("/exists")
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean getUserExists(@QueryParam("username") String username) {
        return (null != userDao.findByUsername(username.toLowerCase()));
    }

    @GET
    @Path("/getProducts")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    @Secured(StringDefs.ROLE_EXTERNAL)
    public List<UserProduct> getProducts() {
        final SecurityUser user = securityService.getCurrentUser();
        if (user == null) {
        	logger.error("Null user in UserService.getProducts()!");
            return null;
        }

        return userProductDao.findByUserId(user.getId());
    }

    @POST
    @Path("/updateUserProductOwned")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    @Secured(StringDefs.ROLE_EXTERNAL)
    public ProductUserProductDto updateUserProductOwned(ProductUserProductDto pupDto) {
     
    	return userServiceComponent.updateUserProductOwned(pupDto);
    
    }
    
    @GET
    @Path("/getOwnedProducts")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
    @Secured(StringDefs.ROLE_EXTERNAL)
    public List<ProductUserProductDto> getOwnedProducts() {

    	return userServiceComponent.getOwnedProducts();
    }
    
    @GET
    @Path("/getAllProducts")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
//    @Secured(StringDefs.ROLE_EXTERNAL)
    @Secured(StringDefs.ROLE_EXTERNAL)
    public List<ProductUserProductDto> getAllProducts() {

        return userServiceComponent.getAllProducts();

    }

    @Resource
    TrialService trialService;
    @POST
    @Path("/launchTrial")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional(propagation = Propagation.REQUIRED)
    @Secured(StringDefs.ROLE_EXTERNAL)
    public TrialDto launchTrial(@Context HttpServletRequest request, TrialDto trialDto) {
        Product product = productDao.getReference(trialDto.getProductId());
        ProductVersion productVersion=productVersionDao.getReference(trialDto.getProductVersionId());
        final SecurityUser currentUser = securityService.getCurrentUser();
        User user = userDao.findOne(currentUser.getId());
        if (product == null || user == null || productVersion == null) {
            throw new CSWebApplicationException(StringDefs.GENERAL_ERROR_CODE, "Product does not exist.");
        }
        /*
        if (request == null) {
        	throw new CSWebApplicationException(StringDefs.GENERAL_ERROR_CODE, "Request null~!?");
        }
        */
        boolean trialEnabled = false;
        final UserProduct userProduct = userProductDao.findById(new UserProductKey(user, product));
        if (userProduct != null) {
            trialEnabled = userProduct.getTrialAvailable();
        }

        Region region = regionDao.getReference(trialDto.getRegionId());
        if (trialEnabled) {
            return trialService.launchTrial(request, productVersion, user, region, currentUser.getLanguage(),new Date());
        } else {
        	 // TODO Dicuss swaping launch out for deploy using a list of products
        	List<ProductVersion> productVersions = new ArrayList<>();
        	productVersions.add(productVersion);
            trialService.requestTrial(request, productVersions, user, region, currentUser.getLanguage(), "");
            return null;
        }
    }

    @Path("/settings")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(StringDefs.ROLE_EXTERNAL)
    public RegistrationCompleteDto getUserSettings() {
        User user = userDao.findOne(securityService.getCurrentUser().getId());
        if (user == null) {
            throw new CSWebApplicationException(StringDefs.GENERAL_ERROR_CODE, "User not found");
        }
        return userServiceComponent.createRegistrationCompleteDto(user);
    }

    @Path("/settings")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional(propagation = Propagation.REQUIRED)
    @Secured(StringDefs.ROLE_EXTERNAL)
    public RegistrationCompleteDto updateUserSettings(@Context HttpServletResponse response, RegistrationCompleteDto dto) {

    	return userServiceComponent.updateUserSettings(response, dto);
    	
    }

    @Path("/getAmazonCredentials")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(StringDefs.ROLE_EXTERNAL)
    public List<AmazonCredentialsDto> getAmazonCredentials() {

        return userServiceComponent.getAmazonCredentials();
    }
    
    @Path("/updateAmazonCredentials")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional(propagation = Propagation.REQUIRED)
    @Secured(StringDefs.ROLE_EXTERNAL)
    public AmazonCredentialsDto updateAmazonCredentials(AmazonCredentialsDto dto)
    {
    	return userServiceComponent.updateAmazonCredentials(dto);
    	
    }
    
    @Path("/deleteAmazonCredentialsById")
    @POST
    @Transactional(propagation = Propagation.REQUIRED)
    @Secured(StringDefs.ROLE_EXTERNAL)
    public void deleteAmazonCredentials(AmazonCredentialsDto dto) {
    	
    	userServiceComponent.deleteAmazonCredentials(dto.getId());
    }
    
    @Path("/getDeployments")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(StringDefs.ROLE_EXTERNAL)
    @Transactional(readOnly = true)
    public List<DeploymentStackDto> getDeployments() {

    	User self=userDao.findById(securityService.getCurrentUser().getId());
    	
        return userServiceComponent.getDeployments(self);
    }

    @Path("getLogin.json")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured("ROLE_OSQA")
    public Creds getLoginCredentials(){
        logger.info("Get Login Credentials.");
        final SecurityUser currentUser = securityService.getCurrentUser();
        Creds creds = new Creds();
        //creds.setScreen_name("Anon");
        if (currentUser != null) {
            String username = currentUser.getUsername();
            String screenName = username.replaceAll("(@|\\.|\\+)", "_");
            creds.setScreen_name(screenName);
            creds.setUsername(username);
            String realName = currentUser.getFirstName() + " " + currentUser.getLastName();
            creds.setReal_name(realName);
        }
        return creds;
    }

    static class Creds {
        private String screen_name;
        private String username;
        private String real_name;

        public String getScreen_name() {
            return screen_name;
        }

        public void setScreen_name(String screen_name) {
            this.screen_name = screen_name;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getReal_name() {
            return real_name;
        }

        public void setReal_name(String real_name) {
            this.real_name = real_name;
        }
    }
}
