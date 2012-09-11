package com.infor.cloudsuite.service;

import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.infor.cloudsuite.AbstractTest;
import com.infor.cloudsuite.dao.IndustryDao;
import com.infor.cloudsuite.dao.ProductDao;
import com.infor.cloudsuite.dao.UserDao;
import com.infor.cloudsuite.dao.UserProductDao;
import com.infor.cloudsuite.dao.ValidationDao;
import com.infor.cloudsuite.dto.RegistrationCompleteDto;
import com.infor.cloudsuite.entity.User;
import com.infor.cloudsuite.entity.Validation;
import com.infor.cloudsuite.entity.ValidationType;
import com.infor.cloudsuite.platform.components.NullEmailProvider;
import com.infor.cloudsuite.platform.security.SecurityService;
import com.infor.cloudsuite.service.component.RegistrationServiceComponent;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * User: bcrow
 * Date: 10/16/11 9:54 PM
 */
public class RegistrationServiceTest  extends AbstractTest {
    private static final Logger logger = LoggerFactory.getLogger(RegistrationServiceTest.class);

    @Resource
    RegistrationService regService;
    @Resource
    ValidationDao validationDao;
    @Resource
    UserDao userDao;
    @Resource
    SecurityService securityService;
    @Resource
    RegistrationServiceComponent registrationServiceComponent;
    @Resource
    ProductDao productDao;
    @Resource
    UserProductDao userProductDao;
    @Resource
    IndustryDao industryDao;

    @Test
    @Transactional(propagation = Propagation.REQUIRED)
    public void testMail() throws ExecutionException, InterruptedException {
        final String email = "doNotReply@infor.com";
        Future<String> result = registrationServiceComponent.sendValidationEmail(email, "test", "http://localhost/", 1000L, "validationKey", "en_US");
        assertEquals(StringDefs.SUCCESS, result.get());
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRED)
    public void testRegister() {

        UriInfo uriInfo = getUriInfoStub();

        anonymous();
        final String emailMixedCase = "doNotReply@ExtErn.Com";
        final String email = "donotreply@extern.com";
        //register a user.
        Validation val = new Validation();
        val.setCompany("comptest");
        val.setLanguage("en_US");
        val.setEmail(emailMixedCase);
        val.setFirstName("firsttest");
        val.setLastName("lasttest");
        final NullEmailProvider emailProvider = new NullEmailProvider();
        registrationServiceComponent.setEmailProvider(emailProvider);
        Validation regValidation = regService.registerUser(uriInfo, val);
        assertEquals("Registration Email was sent.", 1, emailProvider.getAsyncEmails().size());
        assertNotNull("Validation is not null", regValidation);
        assertEquals("Validation email case is lowercase", email, regValidation.getEmail());
        
        Validation validation = validationDao.findByEmailAndType(email, ValidationType.REGISTRATION);
        assertNotNull("Validation exists", validation);
        logger.debug("ValidationKey", validation.getValidationKey());
        String expectedKey = securityService.encodePassword((validation.getEmail() + validation.getType()), validation.getCreateDate());
        assertEquals("validation keys match",expectedKey, validation.getValidationKey());

        //Validate the user and check the status code
        HttpSession session = new MockHttpSession();
        MockHttpServletRequest request = getRequestStub(session);
        MockHttpServletResponse httpResponse = new MockHttpServletResponse();

        Response response = regService.validateUser(validation.getId(), validation.getValidationKey(), request);
        assertEquals("Response is redirect", Response.Status.TEMPORARY_REDIRECT.getStatusCode(), response.getStatus());
        Object valSession = request.getSession().getAttribute(StringDefs.VALIDATION_NAME);
        assertEquals("Returned a Validation class", Validation.class, valSession.getClass());

        //test validation return.
        Validation respVal = (Validation) valSession;
        assertEquals(validation.getId(), respVal.getId());
        assertEquals(validation.getEmail(), respVal.getEmail());
        assertEquals(validation.getFirstName(), respVal.getFirstName());
        assertEquals(validation.getLastName(), respVal.getLastName());
        assertEquals(validation.getCompany(), respVal.getCompany());
        assertEquals(validation.getLanguage(), respVal.getLanguage());

        //Check ROLE_VALIDATED authority granted.
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        assertTrue("Validated Role added", authorities.contains(new SimpleGrantedAuthority("ROLE_VALIDATED")));

        //Complete the registration.
        String password="testing";
        RegistrationCompleteDto complete = new RegistrationCompleteDto();
        complete.setLanguage("en_US");
        complete.setValidationId(respVal.getId());
        complete.setPassword(password);
        complete.setPassword2(password);
        complete.setAddress1("address1");
        complete.setAddress2("address2");
        complete.setPhone("555-867-5309");
        complete.setCompanyName("comptest");
        complete.setCountry("USA");
        complete.setIndustryId(industryDao.findAll().get(0).getId());
        complete.setInforId("INFOR-ID");
        regService.completeRegistration(httpResponse, complete);
        try {
            login(email, password);
        } catch (Exception e) {
            fail("Login validation exception");
        }

        //Check user is now logged in with ROLE_EXTERNAL authority and not ROLE_VALIDATED
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        assertTrue("Principal is UserDetails", UserDetails.class.isAssignableFrom(principal.getClass()));
        UserDetails details = (UserDetails) principal;
        assertEquals("Username in principal is correct", email, details.getUsername());
        authorities = authentication.getAuthorities();
        assertTrue("Now ROLE_EXTERNAL", authorities.contains(new SimpleGrantedAuthority("ROLE_EXTERNAL")));
        assertFalse("No ROLE_VALIDATED", authorities.contains(new SimpleGrantedAuthority("ROLE_VALIDATED")));
        
        User user = userDao.findByUsername(email);
        assertNotNull(user);
        assertEquals("address1", user.getAddress1());
        assertEquals("address2", user.getAddress2());
        assertEquals("555-867-5309", user.getPhone());
        assertEquals("USA", user.getCountry());

//        assertEquals("Checking that infor user has product.size() userproducts",productDao.count(),userProductDao.findByUserId(user.getId()).size());
        //Check validation is deleted
        Validation nullVal = validationDao.findById(respVal.getId());
        assertNull("Validation should be deleted.", nullVal);
    }
    
    @Test
    @Transactional(propagation = Propagation.REQUIRED)
    public void testThatNonInforRegisterDoesNotEnableUserProducts() {

        UriInfo uriInfo = getUriInfoStub();
        anonymous();
        final String emailMixedCase = "uSeR@faKE.cOM";
        final String email = "user@fake.com";
        //register a user.
        Validation val = new Validation();
        val.setCompany("comptest");
        val.setLanguage("en_US");
        val.setEmail(emailMixedCase);
        val.setFirstName("firsttest");
        val.setLastName("lasttest");
        final NullEmailProvider emailProvider = new NullEmailProvider();
        registrationServiceComponent.setEmailProvider(emailProvider);
        regService.registerUser(uriInfo, val);

        Validation validation = validationDao.findByEmailAndType(email, ValidationType.REGISTRATION);
        assertNotNull("Validation exists", validation);
        logger.debug("ValidationKey", validation.getValidationKey());
        String expectedKey = securityService.encodePassword((validation.getEmail() + validation.getType()), validation.getCreateDate());
        assertEquals("validation keys match",expectedKey, validation.getValidationKey());

        //Validate the user and check the status code
        HttpSession session = new MockHttpSession();
        MockHttpServletRequest request = getRequestStub(session);
        MockHttpServletResponse httpResponse = new MockHttpServletResponse();

        Response response = regService.validateUser(validation.getId(), validation.getValidationKey(), request);
        assertEquals("Response is redirect", Response.Status.TEMPORARY_REDIRECT.getStatusCode(), response.getStatus());
        Object valSession = request.getSession().getAttribute(StringDefs.VALIDATION_NAME);
        assertEquals("Returned a Validation class", Validation.class, valSession.getClass());

        //tiny eyeball test validation return.
        Validation respVal = (Validation) valSession;
        assertEquals(validation.getId(), respVal.getId());

        //Check ROLE_VALIDATED authority granted.
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        assertTrue("Validated Role added", authorities.contains(new SimpleGrantedAuthority("ROLE_VALIDATED")));

        //Complete the registration.
        String password="testing";
        RegistrationCompleteDto complete = new RegistrationCompleteDto();
        complete.setLanguage("en_US");
        complete.setValidationId(respVal.getId());
        complete.setPassword(password);
        complete.setPassword2(password);
        complete.setAddress1("address1");
        complete.setAddress2("address2");
        complete.setPhone("555-867-5309");
        complete.setCompanyName("comptest");
        complete.setCountry("USA");
        complete.setInforId("INFOR-ID");
        regService.completeRegistration(httpResponse, complete);
        try {
            login(email, password);
        } catch (Exception e) {
            fail("Login validation exception");
        }
  
        User user = userDao.findByUsername(email);
        assertNotNull(user);
        
        assertEquals("Checking that infor user 0 userproducts",0,userProductDao.findByUserId(user.getId()).size());
        //Check validation is deleted
        Validation nullVal = validationDao.findById(respVal.getId());
        assertNull("Validation should be deleted.", nullVal);
    }
}
