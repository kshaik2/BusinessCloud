package com.infor.cloudsuite.service;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.infor.cloudsuite.dao.TrialEnvironmentDao;
import com.infor.cloudsuite.dao.TrialInstanceDao;
import com.infor.cloudsuite.dto.RedirectUrlDto;
import com.infor.cloudsuite.dto.TrialDto;
import com.infor.cloudsuite.dto.TrialInstanceUpdateDto;
import com.infor.cloudsuite.entity.ProductVersion;
import com.infor.cloudsuite.entity.Region;
import com.infor.cloudsuite.entity.TrialEnvironment;
import com.infor.cloudsuite.entity.TrialInstance;
import com.infor.cloudsuite.entity.TrialRequest;
import com.infor.cloudsuite.entity.User;
import com.infor.cloudsuite.platform.components.UserTrackingProvider;
import com.infor.cloudsuite.service.component.TrialExtensionComponent;
import com.infor.cloudsuite.service.component.TrialObjectsCreatorComponent;
import com.infor.cloudsuite.service.component.TrialRequestsComponent;

/**
 * User: bcrow
 * Date: 10/24/11 2:35 PM
 */
@Path("/trialService")
@Service
@Transactional(propagation = Propagation.SUPPORTS)
public class TrialService {

    @Resource
    private TrialEnvironmentDao trialEnvironmentDao;

    @Resource
    private TrialInstanceDao trialInstanceDao;

    @Resource
    private UserTrackingProvider userTrackingProvider;

    @Resource
    private TrialExtensionComponent trialExtensionComponent;

    @Resource
    private TrialObjectsCreatorComponent trialObjectsCreatorComponent;

    @Resource
    private TrialRequestsComponent trialRequestsComponent;


    @POST
    @Path("/createEnvironment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional(propagation = Propagation.REQUIRED)
    public void createTrialEnvironment(TrialDto trialDto) {
        trialObjectsCreatorComponent.createAndInsertTrialEnvironment(trialDto);
    }

    @GET
    @Path("/getEnvironments")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<TrialEnvironment> getEnvironments(Long productId) {
        return trialEnvironmentDao.findByProductVersion_Product_Id(productId);
    }

    @GET
    @Path("/getActualUrl")
    @Produces(MediaType.APPLICATION_JSON)
    public RedirectUrlDto getActualUrl(@QueryParam("g") String guid) {
        final TrialInstance instance = trialInstanceDao.findByGuid(guid);
        if (instance != null) {
            userTrackingProvider.trackProxyUrlHit(instance.getUser(), instance, null);
            return new RedirectUrlDto(instance.getUrl());
        }
        return null;
    }

    //Synchronization across cluster could be a problem.
    public TrialDto launchTrial(@Context HttpServletRequest request, ProductVersion productVersion, User user, Region region, Locale locale, Date createdAt) {
        return trialObjectsCreatorComponent.launchTrial(request, productVersion, user, region, locale, createdAt);
    }
	public TrialDto launchTrial(@Context HttpServletRequest  request,
			ProductVersion productVersion, User user, Region region, Locale locale) {
	
		return launchTrial(request,productVersion,user,region,locale,new Date());
	}

    @Path("/extendTrialExpiration/{extendType}")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured("ROLE_ADMIN")
    @Transactional
    public void extendTrialExpiration(@PathParam("extendType") String extendType, TrialInstanceUpdateDto trialInstanceUpdateDto) {

        trialExtensionComponent.extendTrialExpiration(extendType, trialInstanceUpdateDto);

    }

    public TrialRequest requestTrial(HttpServletRequest request, List<ProductVersion> productVersions, User user, Region region, Locale language, String comment) {

        return trialRequestsComponent.createTrialRequest(request, productVersions, user, region, language, comment);
    }

    @Path("/approveRequest/{requestKey}")
    @GET
    @Transactional
    public String approveTrialRequest(@Context HttpServletRequest request, @PathParam("requestKey") String requestKey) {
        final TrialRequest trialRequest = trialRequestsComponent.deleteTrialRequest(requestKey);
        final Locale locale = StringUtils.parseLocaleString(trialRequest.getLanguage());
        // TODO launch a trial for each product individually? 
        for(ProductVersion productVersion : trialRequest.getProductVersions())
        launchTrial(request, productVersion, trialRequest.getUser(), trialRequest.getRegion(), locale, new Date());
        return "Done";
    }

    @Path("/deleteRequest/{requestKey}")
    @GET
    @Transactional
    public String deleteTrialRequest(@PathParam("requestKey") String requestKey) {
        trialRequestsComponent.deleteTrialRequest(requestKey);
        return "Deleted";
    }
    
    @Path("/getTrialRequests")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Secured(StringDefs.ROLE_SALES)
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<TrialRequest> getTrialRequests() {
        return trialRequestsComponent.getTrialRequests();
    }

	public TrialObjectsCreatorComponent getTrialObjectsCreatorComponent() {
		return trialObjectsCreatorComponent;
	}





    
    
}


