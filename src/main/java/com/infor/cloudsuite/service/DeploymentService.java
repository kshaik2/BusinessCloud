package com.infor.cloudsuite.service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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

import com.infor.cloudsuite.dao.AmiDescriptorDao;
import com.infor.cloudsuite.dao.DeploymentStackDao;
import com.infor.cloudsuite.dao.DeploymentStackLogDao;
import com.infor.cloudsuite.dao.RegionDao;
import com.infor.cloudsuite.dto.DeployActionDto;
import com.infor.cloudsuite.dto.DeployRequestDto;
import com.infor.cloudsuite.dto.DeploymentStackDto;
import com.infor.cloudsuite.dto.DeploymentStackInfoDto;
import com.infor.cloudsuite.dto.DeploymentStackLogDto;
import com.infor.cloudsuite.dto.DeploymentType;
import com.infor.cloudsuite.dto.RegionDto;
import com.infor.cloudsuite.entity.AmiDescriptor;
import com.infor.cloudsuite.entity.DeploymentStack;
import com.infor.cloudsuite.entity.Region;
import com.infor.cloudsuite.entity.User;
import com.infor.cloudsuite.platform.CSWebApplicationException;
import com.infor.cloudsuite.platform.amazon.CreateStackRequest;
import com.infor.cloudsuite.platform.amazon.StackBuilder;
import com.infor.cloudsuite.platform.components.RequestServices;
import com.infor.cloudsuite.platform.security.SecurityService;
import com.infor.cloudsuite.platform.security.SecurityUser;
import com.infor.cloudsuite.service.component.DeploymentServiceComponent;
import com.infor.cloudsuite.service.component.TrialExtensionComponent;
import com.infor.cloudsuite.service.component.TrialObjectsCreatorComponent;
import com.infor.cloudsuite.task.ScheduleDispatch;

/**
 * User: bcrow
 * Date: 10/25/11 11:38 AM
 */
@Service
@Path("/deploy")
public class DeploymentService {
    private static final Logger logger = LoggerFactory.getLogger(DeploymentService.class);

    @Resource
    private DeploymentServiceComponent deploymentServiceComponent;
    @Resource
    private RequestServices requestServices;
    @Resource
    private DeploymentStackDao deploymentStackDao;
    @Resource
    private DeploymentStackLogDao deploymentStackLogDao;
    @Resource
    private SecurityService securityService;
    @Resource
    private StackBuilder stackBuilder;
    @Resource
    private AmiDescriptorDao amiDescriptorDao;
    @Resource
    private TrialObjectsCreatorComponent trialObjectsCreatorComponent;
    @Resource
    private TrialExtensionComponent trialExtensionComponent;
    @Resource
    private RegionDao regionDao;
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(StringDefs.ROLE_EXTERNAL)
    public DeploymentStackDto deployMultipleProducts(@Context HttpServletRequest request, DeployRequestDto deployRequestDto) {

        if (deployRequestDto.getDeploymentType()==DeploymentType.INFOR24) {
            return trialObjectsCreatorComponent.launchTrial(request,deployRequestDto);
        }

        String url = requestServices.getContextUriBuilder(request).build().toString();
        CreateStackRequest createStackRequest = new CreateStackRequest();

        final SecurityUser currentUser = securityService.getCurrentUser();
        String destEmail = currentUser.getUsername();
        //todo -- remove this.
        if (destEmail.equals("admin@infor.com")) destEmail = "brian.crow@infor.com";
        createStackRequest.setLocale(currentUser.getLanguage());

        createStackRequest.setDestEmails(Collections.singletonList(destEmail));

        final Set<AmiDescriptor> amiDescriptors = createStackRequest.getAmiDescriptors();
        DeploymentStack deploymentStack;
        deployRequestDto.setUrl(url);

        ScheduleDispatch dispatch = new ScheduleDispatch();

        try {
            deploymentStack=deploymentServiceComponent.createAndSaveStack(deployRequestDto,
                    amiDescriptors,
                    createStackRequest.getProductNames(), dispatch);

            dispatch.dispatch();

        } catch (Exception e) {
            logger.error("Exception encountered creating stack",e);
            throw new CSWebApplicationException(e,StringDefs.GENERAL_ERROR_CODE);
        }

        createStackRequest.setDeploymentStack(deploymentStack);

        Region region = regionDao.findById(deploymentStack.getRegion().getId());
        createStackRequest.setRegionName(region.getName());

        if (deploymentStack.getDeployedProductVersions().size() > 1) {
            amiDescriptors.add(amiDescriptorDao.findByName("GDE-IUX"));
            amiDescriptors.add(amiDescriptorDao.findByName("GDE-DC"));
        }


        if (!"DUMMYKEY".equals(deploymentStack.getAmazonCredentials().getAwsKey())) {
            stackBuilder.createStackAsync(createStackRequest);
        }

        return deploymentServiceComponent.getDtoForStack(deploymentStack.getId());
    }


    @POST
    @Path("/do")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(StringDefs.ROLE_EXTERNAL)
    public DeploymentStackDto doAction(@Context HttpServletRequest request, DeployActionDto deployActionDto) {

        String url = requestServices.getContextUriBuilder(request).build().toString();

        DeploymentStack stack;
        if (deployActionDto.getDeploymentStackId() != null) {
            if( deployActionDto.getDeploymentType() != null && deployActionDto.getDeploymentType() == DeploymentType.INFOR24 )  {
                trialExtensionComponent.doAction(deployActionDto.getDeploymentStackId(), deployActionDto);  
                return trialExtensionComponent.getDeploymentStackDto(deployActionDto.getDeploymentStackId());
            }
            else {
                stack = deploymentStackDao.findById(deployActionDto.getDeploymentStackId());
            }
        } else if (deployActionDto.getVpcId() != null) {
            stack = deploymentStackDao.findByVpcId(deployActionDto.getVpcId());
        } else {
            throw new CSWebApplicationException(StringDefs.GENERAL_ERROR_CODE, "Must send valid vpcId or deploymentStackId!");
        }
        deployActionDto.setUrl(url);

        ScheduleDispatch dispatch = new ScheduleDispatch();

        deploymentServiceComponent.runActionSwitch(stack, deployActionDto, dispatch);

        dispatch.dispatch();

        //refresh;
        return deploymentServiceComponent.getDtoForStack(stack.getId());
    }

    @GET
    @Path("/getRegions")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(StringDefs.ROLE_EXTERNAL)
    public List<RegionDto> getRegions() { 
        return regionDao.findRegionDtos();
    }
    
    @GET
    @Path("/getDeploymentStackLogs")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(StringDefs.ROLE_EXTERNAL)
    public List<DeploymentStackLogDto> getDeploymentStackLogs(@QueryParam("vpcId") String vpcId, @QueryParam("id") Long id, @QueryParam("deploymentStackId") Long deploymentStackId) {
        if (id != null) {
            return deploymentStackLogDao.getLogsById(id);

        } else if (deploymentStackId != null) {
            return deploymentStackLogDao.getLogsByDeploymentStackId(deploymentStackId);

        } else if (vpcId != null) {
            return deploymentStackLogDao.getLogsByVpcId(vpcId);
        }

        throw new CSWebApplicationException(StringDefs.GENERAL_ERROR_CODE, "Gotta give me SOMETHING to search for!");
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    @Secured(StringDefs.ROLE_EXTERNAL)
    public DeploymentStackDto getByDeploymentStackId(@QueryParam("id") Long deploymentStackId, @QueryParam("vpcId") String vpcId) {

        DeploymentStack stack = null;
        if (deploymentStackId != null) {
            stack = deploymentStackDao.findById(deploymentStackId);

        } else if (vpcId != null) {
            stack = deploymentStackDao.findByVpcId(vpcId);
        }

        return deploymentServiceComponent.getDtoForStack(stack);
    }


    @GET
    @Path("/getAllDeploymentsSummary")
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(StringDefs.ROLE_ADMIN)
    public DeploymentStackInfoDto getAllDeploymentsSummary() {

        DeploymentStackInfoDto dsid=new DeploymentStackInfoDto();
        dsid.setTotalCount(deploymentStackDao.count());


        List<User> users=deploymentStackDao.getUsersWithDeployments();
        List<User> createUsers=deploymentStackDao.getCreatedByUsersWithDeployments();

        dsid.setUniqueUserCount(users.size());
        dsid.setUniqueCreatedByUserCount(createUsers.size());

        for (User user : users) {
            dsid.getCountMap().put(user.getId(), deploymentStackDao.countByUser(user));
        }

        for (User createUser : createUsers) {

            dsid.getCreatedByCountMap().put(createUser.getId(), deploymentStackDao.countByCreatedByUser(createUser));
        }

        return dsid;

    }

    public void setStackBuilder (StackBuilder stackBuilder) {
        this.stackBuilder=stackBuilder;
    }

    public StackBuilder getStackBuilder() {
        return stackBuilder;
    }

}
