package com.infor.cloudsuite.service;

import java.net.URI;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.infor.cloudsuite.dao.ValidationDao;
import com.infor.cloudsuite.dto.ContraintViolationDto;
import com.infor.cloudsuite.dto.RegistrationCompleteDto;
import com.infor.cloudsuite.entity.Validation;
import com.infor.cloudsuite.platform.CSWebApplicationException;
import com.infor.cloudsuite.platform.components.RequestServices;
import com.infor.cloudsuite.platform.components.ValidationProvider;
import com.infor.cloudsuite.platform.security.SecurityService;
import com.infor.cloudsuite.service.component.RegistrationServiceComponent;

/**
 * User: bcrow
 * Date: 10/16/11 9:17 PM
 */
@Service
@Path("/registration")
public class RegistrationService {
    private static final Logger logger = LoggerFactory.getLogger(RegistrationService.class);

    @Resource
    private ValidationDao validationDao;
 
    @Resource
    private ValidationProvider validationService;

    @Resource
    private SecurityService securityService;

    @Resource
    private RequestServices requestServices;

    @Resource
    private RegistrationServiceComponent registrationServiceComponent;
    
    /**
     * Register a user
     * @param uriInfo Injected context information
     * @param validation Validation object
     * @return The updated validation object.
     */
    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(propagation = Propagation.REQUIRED)
    public Validation registerUser(@Context UriInfo uriInfo, Validation validation) {
        validation.setEmail(validation.getEmail().toLowerCase());
        ContraintViolationDto<Validation> violationsDto = validationService.validate(validation);
        if (violationsDto.isHasViolations()) {
            throw new CSWebApplicationException(StringDefs.VALIDATION_ERROR_CODE, violationsDto);
        }

        return registrationServiceComponent.registerUser(uriInfo, validation);
        
    }

    /**
     * Validate user using information provided in an email.
     *
     * @param id validation object id
     * @param validationKey validation key
     * @param request HttpServletRequest for getting the session.
     * @return HTTP response
     */
    @GET
    @Path("/validate/{validationId}/{validationKey}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response validateUser(@PathParam(StringDefs.VALIDATION_ID) Long id,
                                 @PathParam(StringDefs.VALIDATION_KEY) String validationKey,
                                 @Context HttpServletRequest request) {

        Validation validation = validationDao.findById(id);
        UriBuilder uriBuilder = requestServices.getContextUriBuilder(request);
        if (validation != null && validation.getValidationKey().equals(validationKey)) {
            securityService.authenticateTemporary(StringDefs.ROLE_VALIDATED);
            requestServices.addObjectToSession(request, StringDefs.VALIDATION_NAME, validation);
            URI redirectUri = uriBuilder.path(StringDefs.ACCOUNT_SETUP_JSP).build();
            logger.debug("Redirect URI:{}", redirectUri.toString());
            return Response.temporaryRedirect(redirectUri).build();
        }
        return Response.temporaryRedirect(uriBuilder.path(StringDefs.INDEX_JSP).build()).build();
    }

    @Path("/completeRegistration")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(propagation = Propagation.REQUIRED)
    @Secured(StringDefs.ROLE_VALIDATED)
    public void completeRegistration(@Context HttpServletResponse response, RegistrationCompleteDto complete) {
        logger.debug("Completing the Registration");
        final ContraintViolationDto<RegistrationCompleteDto> validate = validationService.validate(complete);
        if (validate.isHasViolations()) {
            throw new CSWebApplicationException(StringDefs.VALIDATION_ERROR_CODE, validate);
        }        
        registrationServiceComponent.completeRegistration(response, complete);           
    }


}
