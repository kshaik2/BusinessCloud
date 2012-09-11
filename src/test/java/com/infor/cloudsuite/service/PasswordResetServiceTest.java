package com.infor.cloudsuite.service;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.transaction.annotation.Transactional;

import com.infor.cloudsuite.AbstractTest;
import com.infor.cloudsuite.EntityManagerDao;
import com.infor.cloudsuite.dao.UserDao;
import com.infor.cloudsuite.dao.ValidationDao;
import com.infor.cloudsuite.dto.PasswordResetDto;
import com.infor.cloudsuite.dto.RedirectUrlDto;
import com.infor.cloudsuite.entity.Role;
import com.infor.cloudsuite.entity.User;
import com.infor.cloudsuite.entity.Validation;
import com.infor.cloudsuite.entity.ValidationType;
import com.infor.cloudsuite.platform.CSWebApplicationException;
import com.infor.cloudsuite.platform.security.SecurityService;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.fail;

/**
 * User: bcrow
 * Date: 10/20/11 3:47 PM
 */
public class PasswordResetServiceTest extends AbstractTest {

    @Resource
    PasswordResetService service;
    @Resource
    UserDao userDao;
    @Resource
    ValidationDao validationDao;
    @Resource
    SecurityService securityService;
    @Resource
    EntityManagerDao emDao;

    private static final Logger logger = LoggerFactory.getLogger(PasswordResetServiceTest.class);
    private HttpServletRequest request;
    private UriInfo uriInfo;
    private PasswordResetDto reset;
    private String email;

    @Before
    public void setUp() {
        HttpSession session = new MockHttpSession();
        request = getRequestStub(session);
        uriInfo = getUriInfoStub();
        reset = new PasswordResetDto();
        String camelEmail = "doNotReply@infor.com";
        email = "donotreply@infor.com";
        reset.setEmail(camelEmail);
        }

    @Test
    @Transactional
    public void testPasswordReset() throws InterruptedException {

        anonymous();

        //Add a valid user
        User user = addValidUser();

        //Send a reset request for the valid user.
        Validation val2 = resetUserPasswordAndValidateRequest();
        Validation validation;

        PasswordResetDto complete = new PasswordResetDto();

        //test everything correct.
        complete.setPassword2("test1");
        complete.setPassword("testagain");
        complete.setPassword2("testagain");
        complete.setEmail(val2.getEmail());
        RedirectUrlDto landingPage = null;
        try {
            landingPage = service.comleteRegistration(request, complete);
        } catch (CSWebApplicationException e) {
            fail();
        }
        assertNotNull(landingPage);
        assertEquals("/cloud.jsp", landingPage.getRedirectUrl());


        //Reset again with a diffrent role and landing page.
        user.getRoles().clear();
        user.getRoles().add(Role.ROLE_ADMIN);
        userDao.save(user);

        service.resetUserPassword(uriInfo, request, reset);
        emDao.flush();
        //        assertEquals("Response is always OK", Response.Status.OK.getStatusCode(), response.getStatus());
        validation = validationDao.findByEmailAndType(email, ValidationType.LOST_PASSWORD);
        assertNotNull("Validation should exist", validation);
        assertEquals("validation email should mathch", email, validation.getEmail());

        //Validate the reset request.
        service.passwordResetValidate(validation.getId(), validation.getValidationKey(), request);
        //        assertEquals("Response is redirect", Response.Status.TEMPORARY_REDIRECT.getStatusCode(), response.getStatus());
        val2 = (Validation) request.getSession().getAttribute(StringDefs.VALIDATION_NAME);
        assertEquals(ValidationType.LOST_PASSWORD, val2.getType());
        assertEquals(email, val2.getEmail());

        //Test passwords don't match
        complete = new PasswordResetDto();
        complete.setEmail(val2.getEmail());
        complete.setPassword("testagain");
        complete.setPassword2("testagain");
        try {
            landingPage = service.comleteRegistration(request, complete);
        } catch (CSWebApplicationException e) {
            fail();
        }
        assertNotNull(landingPage);
        assertEquals("/cloud.jsp", landingPage.getRedirectUrl());
    }

    private Validation resetUserPasswordAndValidateRequest() {
        service.resetUserPassword(uriInfo, request, reset);
        emDao.flush();
//        assertEquals("Response is always OK", Response.Status.OK.getStatusCode(), response.getStatus());
        Validation validation = validationDao.findByEmailAndType(email, ValidationType.LOST_PASSWORD);
        assertNotNull("Validation should exist", validation);
        assertEquals("validation email should mathch", email, validation.getEmail());

        //Validate the reset request.
        service.passwordResetValidate(validation.getId(), validation.getValidationKey(), request);
//        assertEquals("Response is redirect", Response.Status.TEMPORARY_REDIRECT.getStatusCode(), response.getStatus());
        Validation val2 = (Validation) request.getSession().getAttribute(StringDefs.VALIDATION_NAME);
        assertEquals(validation.getId(), val2.getId());
        assertEquals(validation.getValidationKey(), val2.getValidationKey());
        assertEquals(ValidationType.LOST_PASSWORD, val2.getType());
        assertEquals(email, val2.getEmail());
        return val2;
    }

    private User addValidUser() {
        String password = "testPassword";
        User user = new User();
        user.setUsername(email);
        user.setFirstName("test");
        user.setLastName("test");
        user.setCreatedAt(new Date());
        user.setPassword(securityService.encodePassword(password, user.getCreatedAt()));
        user.setActive(true);
        user.getRoles().add(Role.ROLE_EXTERNAL);
        userDao.saveAndFlush(user);
        return user;
    }

    @Test
    public void testPasswordResetFauilure() {

        anonymous();

        assertNotNull("Service is null", service);
        try {
            logger.info("resetFailure");
            service.resetUserPassword(uriInfo, request, reset);
            fail("Non-existant email should throw an exception.");
        } catch (CSWebApplicationException e) {
            //Throws exception when email does not exist.
        }
        //        assertEquals("Response is always OK", Response.Status.OK.getStatusCode(), response.getStatus());
        logger.info("FindByEmail");
        Validation validation = validationDao.findByEmailAndType(email, ValidationType.LOST_PASSWORD);
        assertNull("Validation should not exist", validation);
    }

    @Test
    @Transactional
    public void testPasswordsDontMatch() {
        anonymous();

        //Add a valid user
        addValidUser();

        //Send a reset request for the valid user.
        Validation val2 = resetUserPasswordAndValidateRequest();

        //Test passwords don't match
        PasswordResetDto complete = new PasswordResetDto();
        complete.setEmail(val2.getEmail());
        complete.setPassword("test1");
        complete.setPassword2("test0");
        try {
            service.comleteRegistration(request, complete);
            fail("Should not reset password: bad passwords");
        } catch (CSWebApplicationException e) {
            assertEquals("Error response", Response.Status.BAD_REQUEST.getStatusCode(), e.getResponse().getStatus());
        }
    }

    @Test
    @Transactional
    public void testAddressesDontMatch() {
        anonymous();

        //Add a valid user
        addValidUser();

        //Send a reset request for the valid user.
        Validation val2 = resetUserPasswordAndValidateRequest();

        //Test passwords don't match
        PasswordResetDto complete = new PasswordResetDto();
        complete.setPassword("testagain");
        complete.setPassword2("testagain");
        complete.setEmail(val2.getEmail());
        val2.setEmail(val2.getEmail() + "BAD");
        try {
            service.comleteRegistration(request, complete);
            fail("Should not reset password: addresses dont match.");
        } catch (CSWebApplicationException e) {
            assertEquals("Error response", Response.Status.BAD_REQUEST.getStatusCode(), e.getResponse().getStatus());
        }

    }
}
