package com.infor.cloudsuite.service;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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

import com.infor.cloudsuite.dao.UserDao;
import com.infor.cloudsuite.dao.ValidationDao;
import com.infor.cloudsuite.dto.PasswordResetDto;
import com.infor.cloudsuite.dto.RedirectUrlDto;
import com.infor.cloudsuite.entity.User;
import com.infor.cloudsuite.entity.Validation;
import com.infor.cloudsuite.entity.ValidationType;
import com.infor.cloudsuite.platform.CSWebApplicationException;
import com.infor.cloudsuite.platform.components.EmailProvider;
import com.infor.cloudsuite.platform.components.MessageProvider;
import com.infor.cloudsuite.platform.components.RequestServices;
import com.infor.cloudsuite.platform.components.TemplateProvider;
import com.infor.cloudsuite.platform.security.SecurityService;

/**
 * User: bcrow
 * Date: 10/20/11 3:14 PM
 */
@Service
@Path("/passwordReset")
public class PasswordResetService {
    private static final Logger logger = LoggerFactory.getLogger(PasswordResetService.class);

    @Resource
    private UserDao userDao;
    @Resource
    private ValidationDao validationDao;
    @Resource
    private EmailProvider emailProvider;
    @Resource
    private SecurityService securityService;
    @Resource
    private RequestServices requestServices;
    @Resource
    private TemplateProvider templateProvider;
    @Resource
    private MessageProvider messageProvider;

    /**
     * Register a user
     * @param uriInfo Injected context information
     * @param request Request for extracting locale information
     * @param reset Password Reset object populated only with the email.
     */
    @POST
    @Path("/passwordReset")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(propagation = Propagation.REQUIRED)
    public void resetUserPassword(@Context UriInfo uriInfo, @Context HttpServletRequest request, PasswordResetDto reset) {
        final String email = reset.getEmail().toLowerCase();
        User user = userDao.findByUsername(email);
        if (user == null) {
            throw new CSWebApplicationException(StringDefs.GENERAL_ERROR_CODE,"Email does not exist.");
        }
        Validation validation = new Validation();
        validation.setEmail(email);
        validation.setFirstName(user.getFirstName());
        validation.setLastName(user.getLastName());
        validation.setType(ValidationType.LOST_PASSWORD);
        validation.setCreateDate(new Date());
        String validationKey = securityService.encodePassword((validation.getEmail() + validation.getType()), validation.getCreateDate());
        validation.setValidationKey(validationKey);
        Validation oldValidation = validationDao.findByEmailAndType(validation.getEmail(), validation.getType());
        if(oldValidation != null) {
            logger.debug("Deleting prior a prior validation for {}", validation.getEmail());
            validationDao.delete(oldValidation);
            validationDao.flush();
        }
        validationDao.save(validation);
        Locale locale = requestServices.getLocale(request);
        sendPasswordResetEmail(validation.getEmail(), locale, user.getFirstName(), uriInfo.getBaseUri().toString(), validation.getId(), validation.getValidationKey(),reset.isCreate());
    }

    /**
     * Send the validation asynchronously.
     *
     *
     * @param address email address
     * @param locale Localization locale
     * @param firstName User's first name
     * @param baseUri the base uri for the reply address
     * @param id validation id
     * @param key validation key    @return Success/Failure string(Future used to stop unittests from killing the async process before finished.     @return The async Future, mostly for testing purposes.
     * @return the Future objcet
     */
    private Future<String> sendPasswordResetEmail(String address, Locale locale, String firstName, String baseUri, Long id, String key, boolean onCreate) {
        logger.debug("Sending password reset email to {} with the key: {}", address, key);
        final String subject = messageProvider.getMessage(onCreate?StringDefs.MESSAGE_PASSWORD_CREATE_SUBJECT:StringDefs.MESSAGE_PASSWORD_RESET_SUBJECT, locale);

        final String resetUrl = String.format("%spasswordReset/passwordReset/%s/%s", baseUri, id, key);
        logger.debug("resetUrl={}", resetUrl);
        Map<String, Object> model = new HashMap<>(2);
        model.put("firstName", firstName);
        model.put("resetUrl", resetUrl);

        final String text = templateProvider.processTemplate(onCreate?StringDefs.MESSAGE_PASSWORD_CREATE_TEMPLATE:StringDefs.MESSAGE_PASSWORD_RESET_TEMPLATE, locale, model);
        return emailProvider.sendEmailAsync(address, subject, text, true);
    }

    /**
     * Validate user using information provided in an email.
     *
     * @param id validation id
     * @param validationKey validation key
     * @param request HttpServletRequest for getting the session.
     * @return HTTP response
     */
    @GET
    @Path("/passwordReset/{validationId}/{validationKey}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response passwordResetValidate(@PathParam("validationId") Long id,
                                          @PathParam("validationKey") String validationKey,
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

    @Path("/completePasswordReset")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(propagation = Propagation.REQUIRED)
    @Secured("ROLE_VALIDATED")
    public RedirectUrlDto comleteRegistration(@Context HttpServletRequest request, PasswordResetDto complete) {
        logger.debug("Completing the password reset");
        Validation validation = requestServices.getObjectFromSession(request, StringDefs.VALIDATION_NAME);
        User user = userDao.findByUsername(validation.getEmail());
        if (user == null || !validation.getEmail().equals(user.getUsername())) {
            final String username = user == null ? "!NONE!" : user.getUsername();
            logger.warn("Validation email({}) address and username({}) do not match", validation.getEmail(), username);
            throw new CSWebApplicationException(Response.Status.BAD_REQUEST);
        }
        logger.debug("Updating user {}:{}", user.getId(), user.getUsername());
        if ((complete.getPassword()== null || complete.getPassword2() == null) ||
            (!complete.getPassword().equals(complete.getPassword2()))) {
            logger.warn("Passwords do not match [{}:{}]", complete.getPassword(), complete.getPassword2());
            throw new CSWebApplicationException(Response.Status.BAD_REQUEST);
        }
        String encodedPassword = securityService.encodePassword(complete.getPassword(), user.getCreatedAt());
        user.setPassword(encodedPassword);
        userDao.save(user);
        logger.debug("user updated.");
        validationDao.delete(validationDao.findById(validation.getId()));
        validationDao.flush();
        logger.debug("validation deleted.");
        securityService.fullAccessLogin(user.getUsername(), complete.getPassword());
        requestServices.removeObjectFromSession(request, StringDefs.VALIDATION_NAME);
        logger.debug("Logged in.");
//        String landingPage = securityService.getLandingPage(securityService.getCurrentUser().getAuthorities());
//        logger.debug("Redirect to : " + landingPage);
        String landingPage = "/cloud.jsp";
        return new RedirectUrlDto(landingPage);
    }
}
