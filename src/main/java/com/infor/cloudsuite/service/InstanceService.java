package com.infor.cloudsuite.service;

import java.util.List;

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
import org.springframework.stereotype.Service;

import com.infor.cloudsuite.dto.InstanceDto;
import com.infor.cloudsuite.dto.InstanceStateChangeDto;
import com.infor.cloudsuite.platform.CSWebApplicationException;
import com.infor.cloudsuite.platform.components.RequestServices;
import com.infor.cloudsuite.platform.security.SecurityService;
import com.infor.cloudsuite.service.component.InstanceServiceComponent;
import com.infor.cloudsuite.task.ScheduleDispatch;


@Service
@Path("/instances")
public class InstanceService {
    private static final Logger logger = LoggerFactory.getLogger(InstanceService.class);

    @Resource
    private InstanceServiceComponent instanceServiceComponent; 
    @Resource
    private RequestServices requestServices;
    @Resource
    private SecurityService securityService;

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<InstanceDto> getInstances(@QueryParam("id") Long id, @QueryParam("regionId") Long regionId ) {
        List<InstanceDto> instances; 

        instances = getInstances(id, null, regionId);

        return instances;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<InstanceDto> updateInstancesStatus(@Context HttpServletRequest request, InstanceStateChangeDto stateChange) {

        String url = requestServices.getContextUriBuilder(request).build().toString();
        stateChange.setUrl(url);
        if(stateChange.getCredentials().getUserId() == null || stateChange.getCredentials().getUserId() <= 0) {
            stateChange.getCredentials().setUserId(securityService.getCurrentUser().getId());
        }

        ScheduleDispatch dispatch = new ScheduleDispatch();

        instanceServiceComponent.updateInstancesStatus(stateChange, dispatch);

        dispatch.dispatch();

        return getInstances(stateChange.getCredentials().getId(), stateChange.getInstanceIds(), stateChange.getRegionId());

    }

    private List<InstanceDto> getInstances(Long id, List<String> instanceIds, Long regionId) {
        List<InstanceDto> instances;
        try {
            instances = instanceServiceComponent.getInstances(id, instanceIds, regionId);
        } catch (Exception e) {
            logger.error("Exception encountered retrieving instances",e);
            throw new CSWebApplicationException(e,StringDefs.GENERAL_ERROR_CODE);
        }
        return instances;
    }
}