package com.infor.cloudsuite;

import java.net.URI;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.UriInfo;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.infor.cloudsuite.dao.UserDao;
import com.infor.cloudsuite.dto.UserProductUpdateDto;
import com.infor.cloudsuite.dto.UserProductUpdateType;
import com.infor.cloudsuite.entity.Product;
import com.infor.cloudsuite.entity.Role;
import com.infor.cloudsuite.entity.User;
import com.infor.cloudsuite.platform.CSWebApplicationException;
import com.infor.cloudsuite.platform.jpa.hibernate.CacheOperator;
import com.infor.cloudsuite.platform.jpa.hibernate.StatisticsOperator;
import com.infor.cloudsuite.platform.security.SecurityService;
import com.infor.cloudsuite.platform.security.SecurityUser;
import com.infor.cloudsuite.service.SeedService;
import com.infor.cloudsuite.service.StringDefs;
import com.infor.cloudsuite.service.component.UserServiceComponent;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;

/**
 * User: bcrow
 * Date: 9/28/11 11:23 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:WEB-INF/applicationContext-settings.xml",
        "classpath:WEB-INF/applicationContext.xml",
        "classpath:WEB-INF/applicationContext-security.xml",
        "classpath:WEB-INF/applicationContext-mail.xml",
        "classpath:ApplicationContext-emf-test.xml",
        "classpath:WEB-INF/applicationContext-orm.xml",
        "classpath:WEB-INF/applicationContext-scheduling.xml",
        "classpath:WEB-INF/applicationContext-upgrade.xml"})
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        MockitoTestExecutionListener.class})
@TransactionConfiguration(defaultRollback = true)
public abstract class AbstractTest {
    private static final Logger logger = LoggerFactory.getLogger(AbstractTest.class);

    protected final String testUserName = "auser@infor.com";
    protected final String testUserPassword = "useruser";
    protected final String testAdminName = "admin@infor.com";
    protected final String testAdminPassword = "adminadmin";
    protected final String serviceUserGuid = "serviceuserguid";
    protected final String serviceUserKey = "key";

    @Resource
    private EntityManagerDao emService;
    @Resource
    private UserDao userDao;
    @Resource
    private SeedService seedService;
    @Resource
    private SecurityService securityService;
    @Resource
    private CacheOperator cacheOperator;
    @Resource
    private StatisticsOperator statisticsOperator;
    @Resource
    private UserServiceComponent userServiceComponent;


    @Before
    @Transactional(propagation = Propagation.REQUIRED)
    public void before() {
        seedService.seedDatabase();
    }

    public User createUser(String username, String firstName, String lastName, String password, Role... roles) {
    	return createUser(username,firstName,lastName,password,new Date(),roles);
    }
    
    public User createUser(String username, String firstName, String lastName, String password, Date createdAt,Role... roles) {
        User user = new User();
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setAddress1(username + "_address1");
        user.setAddress2(username + "_address2");
        //user.setCompanyName(username + "_companyName");
        user.setActive(true);
        user.setCreatedAt(createdAt);
        for (Role role : roles) {
            user.getRoles().add(role);
        }
        user.setPassword(securityService.encodePassword(password, user.getCreatedAt()));
        return user;
    }

    @After
    public void after() {
        clearCachesAndStats();
        clearContext();
    }

    protected void clearCachesAndStats() {
        try {
            cacheOperator.evictAll();
            cacheOperator.evictAllQueryCache();
            statisticsOperator.clear();
        } catch (Exception e) {
            logger.info("Clear caches failed: " + e.getMessage());
        }
    }

    protected void clearContext() {
        SecurityContextHolder.clearContext();
    }

    protected void anonymous() {
        Authentication auth = new AnonymousAuthenticationToken("anonymousKey", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    protected void loginTestUser() {
        login(testUserName, testUserPassword);
    }

    protected void loginAdminUser() {
        login(testAdminName, testAdminPassword);
    }

    protected SecurityUser getCurrentUser() {
        return securityService.getCurrentUser();
    }

    protected void authValidation() {
        securityService.authenticateTemporary(StringDefs.ROLE_VALIDATED);
    }

    protected void login(String username, String password) {
        securityService.fullAccessLogin(username, password);
        logger.debug("User:" + username + " logged in");
    }

    protected String getLoginDetails() {
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }

    }

    public UriInfo getUriInfoStub() {
        URI uri = URI.create("http://localhost:8080/services");
        UriInfo uriInfo = mock(UriInfo.class);
        stub(uriInfo.getBaseUri()).toReturn(uri);
        return uriInfo;
    }

    protected MockHttpServletRequest getRequestStub(HttpSession session) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSession(session);
        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort(8080);
        request.setContextPath("");
        return request;
    }

    protected MockHttpServletRequest getRequestStub() {
        return getRequestStub(new MockHttpSession());
    }

    protected void activateTrial(String userName, Product product) {
        Long userId = userDao.findByUsername(userName).getId();
        userServiceComponent.updateUserProduct(new UserProductUpdateDto(userId, product.getId(), UserProductUpdateType.TRIAL_TYPE, true));
    }
    
    protected void activateDeploy(String userName, Product product) {
    	Long userId=userDao.findByUsername(userName).getId();
    	userServiceComponent.updateUserProduct(new UserProductUpdateDto(userId, product.getId(), UserProductUpdateType.DEPLOY_TYPE,true));
    	
    }
    protected List<Product> productAsList(Product product) {
        return Collections.singletonList(product);
    }
    
    protected boolean csWebExceptionContains(CSWebApplicationException e, String part) {
    	if (e==null) {
    		return false;
    	}
    	return e.getResponse().getEntity().toString().contains(part);
    }
}
