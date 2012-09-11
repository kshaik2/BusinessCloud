package com.infor.cloudsuite.service;


import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.infor.cloudsuite.AbstractTest;
import com.infor.cloudsuite.EntityManagerDao;
import com.infor.cloudsuite.dao.AmazonCredentialsDao;
import com.infor.cloudsuite.dao.DomainBlacklistDao;
import com.infor.cloudsuite.dao.ProductDao;
import com.infor.cloudsuite.dao.UserDao;
import com.infor.cloudsuite.dto.DeployRequestDto;
import com.infor.cloudsuite.dto.AdminUserDto;
import com.infor.cloudsuite.dto.DeploymentStackDto;
import com.infor.cloudsuite.dto.ProductInfoDto;
import com.infor.cloudsuite.dto.UserFlagUpdateDto;
import com.infor.cloudsuite.dto.UserProductUpdateDto;
import com.infor.cloudsuite.dto.UserProductUpdateType;
import com.infor.cloudsuite.dto.UserRoleUpdateDto;
import com.infor.cloudsuite.entity.AmazonCredentials;
import com.infor.cloudsuite.entity.DomainBlacklist;
import com.infor.cloudsuite.entity.Product;
import com.infor.cloudsuite.entity.Role;
import com.infor.cloudsuite.entity.User;
import com.infor.cloudsuite.platform.security.SecurityUser;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * User: bcrow
 * Date: 11/8/11 12:08 PM
 */
@Transactional(propagation = Propagation.REQUIRED)
public class AdminServiceTest extends AbstractTest {
    private static final Logger logger = LoggerFactory.getLogger(AdminServiceTest.class);

    @Resource
    private DeploymentService deploymentService;
    @Resource
    private AmazonCredentialsDao amazonCredentialsDao;
    @Resource
    private AdminService adminService;
    @Resource
    private ProductDao productDao;
    @Resource
    private UserDao userDao;
    @Resource
    private EntityManagerDao emDao;
    @Resource
    private DomainBlacklistDao blacklistDao;

    private HttpServletRequest request;

    @Before
    public void setUp() {
        HttpSession session = new MockHttpSession();
        request = getRequestStub(session);
    }
    
    @Test
    public void testGetAllProducts() throws Exception {

        loginAdminUser();
        List<AdminUserDto> userDtos = adminService.getAllUsers(0, 100);
        assertNotNull(userDtos);
        assertEquals(4, userDtos.size());
        logger.debug("# of products:" + userDtos.size());
        //assertEquals(3, userDtos.size());
        for (AdminUserDto userDto : userDtos) {
            User currUser = userDao.findById(userDto.getId());
            assertEquals(currUser.getId(), userDto.getId());
            assertEquals(currUser.getUsername(), userDto.getUser().getUsername());
            assertEquals(currUser.getFirstName(), userDto.getUser().getFirstName());
            assertEquals(currUser.getLastName(), userDto.getUser().getLastName());
            assertEquals(currUser.getCountry(), userDto.getUser().getCountry());
            assertEquals(currUser.getCompany().getName(), userDto.getCompany().getName());
            assertEquals(currUser.getActive(), userDto.getUser().getActive());

            if (userDto.getUser().isAnAdmin()) {
                assertTrue(Role.ADMIN_ROLES.contains(Role.valueOf(userDto.getRole())));
            } else if (userDto.getUser().isSalesRole()){
                assertEquals(StringDefs.ROLE_SALES,userDto.getRole());
            } else {
                assertEquals(StringDefs.ROLE_EXTERNAL, userDto.getRole());
            }
        }
    }

    //    @Test
    //    public void testGetAllProductsPaging() throws Exception {
    //        loginAdminUser();
    //        List<AdminUserDto> userDtos = adminService.getAllProducts(0, 100);
    //        assertNotNull(userDtos);
    //        assertEquals(2, userDtos.size());
    //        //assert sort order.
    //        assertTrue("Admin", "Admin".equals(userDtos.get(0).getUser().getFirstName()));
    //        assertEquals(1L, userDtos.get(0).getLoginAgg().getLoginCnt().longValue());
    //        assertTrue("Default", "Default".equals(userDtos.get(1).getUser().getFirstName()));
    //        //Paging
    //        userDtos = adminService.getAllProducts(0, 1);
    //        assertEquals(1, userDtos.size());
    //        //assert sort order.
    //        assertTrue("Admin", "Admin".equals(userDtos.get(0).getUser().getFirstName()));
    //        assertEquals(1L, userDtos.get(0).getLoginAgg().getLoginCnt().longValue());
    //        //page2
    //        userDtos = adminService.getAllProducts(1, 1);
    //        assertEquals(1, userDtos.size());
    //        //assert sort order.
    //        assertTrue("Default", "Default".equals(userDtos.get(0).getUser().getFirstName()));
    //        //
    //        userDtos = adminService.getAllProducts(2, 1);
    //        assertEquals(0, userDtos.size());
    //
    //    }

    @Test
    @Transactional
    public void testGetProductInfos() {
        long prodCount = productDao.count();
        List<ProductInfoDto> infoDtos = productDao.findProductInfoDtos();

        assertTrue("There are products", prodCount > 0);
        assertTrue("There are product Infos", infoDtos.size() > 0);
        assertEquals("infoDtos.size = prodCount", prodCount, infoDtos.size());

        for (ProductInfoDto infoDto : infoDtos) {
            Product currProd = productDao.findById(infoDto.getId());
            assertEquals("Ids",currProd.getId(), infoDto.getId());
            assertEquals("Short name",currProd.getShortName(), infoDto.getShortName());
            assertEquals("name", currProd.getName(), infoDto.getLongName());
            assertEquals("trials", currProd.getTrialsAvailable(), infoDto.getAvailability().getTrial());
            assertEquals("Deployments", currProd.getDeploymentsAvailable(), infoDto.getAvailability().getDeployment());
            assertEquals("Tile order", currProd.getTileOrder(), infoDto.getTileOrder());
        }
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRED)
    public void testGetOwnedProducts() throws Exception {
        final String username = "user@company.com";
        final String password = "password";
        User user = this.createUser(username, "Head", "Honcho", password, Role.ROLE_EXTERNAL);
        userDao.save(user);

        loginAdminUser();
        UserProductUpdateDto update = new UserProductUpdateDto();
        update.setUserId(user.getId());
        Product product = productDao.findByShortName("EAM");
        update.setProductId(product.getId());
        update.setType(UserProductUpdateType.OWNED_TYPE);
        update.setActive(true);

        UserProductUpdateDto dto = adminService.updateUserProduct(update);
        assertEquals(update, dto);
        emDao.flush();

    }

    /*
    @Test
    public void testUpdateUserProduct() throws Exception {

        loginAdminUser();
        List<AdminUserDto> userDtos;
        long startValue=productDao.count();

        UserProductUpdateDto update = new UserProductUpdateDto();
        User user = userDao.findByUsername("auser@infor.com");
        update.setUserId(user.getId());
        Product byShortName = productDao.findByShortName("XM");
        final Long xm = byShortName.getId();
        update.setProductId(xm);
        update.setActive(true);
        update.setType(UserProductUpdateType.DEPLOY_TYPE);
        UserProductUpdateDto dto = adminService.updateUserProduct(update);

        assertEquals(update, dto);
        emDao.flush();
        emDao.clear();

        userDtos = adminService.getAllUsers(0, 100);
        assertEquals(4, userDtos.size());
        for (AdminUserDto userDto : userDtos) {
            assertTrue(userDto.getUser().getActive());
            if (userDto.getId().equals(user.getId())) {
                assertEquals(startValue, userDto.getAdminUserProducts().size());
                for (AdminUserDto.AdminUserProduct adminUserProduct : userDto.getAdminUserProducts()) {
                    if (adminUserProduct.getProductId().equals(update.getProductId())) {
                        assertEquals(update.getProductId(), adminUserProduct.getProductId());
                        assertEquals(byShortName.getShortName(), adminUserProduct.getProductShortName());
                        assertTrue(adminUserProduct.getLaunchAvailable());
                        assertFalse(adminUserProduct.getTrialAvailable());
                    }
                }
            }
        }

        update.setType(UserProductUpdateType.TRIAL_TYPE);
        update.setActive(true);
        dto = adminService.updateUserProduct(update);

        assertEquals(update, dto);
        emDao.flush();
        emDao.clear();

        userDtos = adminService.getAllProducts(0, 100);
        assertEquals(4, userDtos.size());
        for (AdminUserDto userDto : userDtos) {
            assertTrue(userDto.getUser().getActive());
            if (userDto.getId().equals(user.getId())) {
                assertEquals(startValue, userDto.getAdminUserProducts().size());
                for (AdminUserDto.AdminUserProduct adminUserProduct : userDto.getAdminUserProducts()) {
                    if (adminUserProduct.getProductId().equals(update.getProductId())) {
                        assertEquals(update.getProductId(), adminUserProduct.getProductId());
                        assertEquals(byShortName.getShortName(), adminUserProduct.getProductShortName());
                        assertTrue(adminUserProduct.getLaunchAvailable());
                        assertTrue(adminUserProduct.getTrialAvailable());
                    }
                }
            }
        }

        update = new UserProductUpdateDto();
        assertEquals(4, userDtos.size());
        update.setUserId(userDao.findByUsername("auser@infor.com").getId());
        byShortName = productDao.findByShortName("EAM");
        update.setProductId(byShortName.getId());
        update.setActive(true);
        update.setType(UserProductUpdateType.DEPLOY_TYPE);
        dto = adminService.updateUserProduct(update);
        assertEquals(update, dto);
        emDao.flush();
        emDao.clear();

        userDtos = adminService.getAllProducts(0, 100);
        assertEquals(4, userDtos.size());
        for (AdminUserDto userDto : userDtos) {
            assertTrue(userDto.getUser().getActive());
            if (userDto.getId().equals(user.getId())) {
                assertEquals(startValue, userDto.getAdminUserProducts().size());
                for (AdminUserDto.AdminUserProduct adminUserProduct : userDto.getAdminUserProducts()) {
                    if (adminUserProduct.getProductId().equals(update.getProductId())) {
                        assertEquals(update.getProductId(), adminUserProduct.getProductId());
                        assertEquals(byShortName.getShortName(), adminUserProduct.getProductShortName());
                        assertTrue(adminUserProduct.getLaunchAvailable());
                        assertFalse(adminUserProduct.getTrialAvailable());
                    }
                }
            }
        }
    }
     */

    @Test
    public void testDeploymentsInAdminUserDto() throws Exception {
        User sales=userDao.findByUsername("sales@infor.com");
        AmazonCredentials amCred=new AmazonCredentials();
        amCred.setAwsKey("DUMMYKEY");
        amCred.setName("DUMMYKEY");
        amCred.setSecretKey("Psst...");
        amCred.setUser(sales);
        amazonCredentialsDao.save(amCred);
        amazonCredentialsDao.flush();

        DeployRequestDto dto=new DeployRequestDto();
        dto.setAmazonCredentialsId(amCred.getId());
        Product eam=productDao.findByShortName("EAM");
        Product xm=productDao.findByShortName("XM");
        dto.getProductIds().add(new Long[]{eam.getId(),eam.getProductVersions().get(0).getId()});
        dto.getProductIds().add(new Long[]{xm.getId(),xm.getProductVersions().get(0).getId()});
        
        dto.setUserId(sales.getId());
        login("sales@infor.com","useruser");
        DeploymentStackDto stackDto=deploymentService.deployMultipleProducts(request,dto);

        assertNotNull(stackDto);
        loginAdminUser();

        List<AdminUserDto> userDtos=adminService.getAllUsers(0, 100);
        for (AdminUserDto userDto : userDtos) {
            if (userDto.getId().equals(sales.getId())) {
                assertEquals(1,userDto.getDeployments().size());
                break;
            }
        }


    }
    @Test
    public void testSetUserActive() throws Exception {

        loginTestUser();

        loginAdminUser();
        User user = userDao.findByUsername(testUserName);
        adminService.setUserActive(new UserFlagUpdateDto(user.getId(), false));

        assertFalse("Test user is no longer active.", userDao.findOne(user.getId()).getActive());
        DisabledException exception = null;
        try {
            loginTestUser();
            fail("Test user account should be disabled");
        } catch (DisabledException de) {
            exception = de;
        }
        assertNotNull("DisabledException was thrown.", exception);

        loginAdminUser();
        adminService.setUserActive(new UserFlagUpdateDto(user.getId(), true));
        assertTrue("Test user is now active.", userDao.findOne(user.getId()).getActive());

        loginTestUser();

        //Test BDR Admin fuctionality
        final String username = "salesOps@infor.com";
        final String password = "salesOps";
        final User bdrUser = createUser(username, "Sales", "Ops", password, Role.ROLE_SALES);
        userDao.save(bdrUser);

        login(username, password);
        //can change User statuses
        adminService.setUserActive(new UserFlagUpdateDto(user.getId(), false));
        assertFalse("Test user is now inactive.", userDao.findOne(user.getId()).getActive());
        adminService.setUserActive(new UserFlagUpdateDto(user.getId(), true));
        assertTrue("Test user is now active.", userDao.findOne(user.getId()).getActive());

        final User adminUser = userDao.findByUsername(testAdminName);
        Long adminId = adminUser.getId();
        assertTrue(adminUser.getActive());
        //cannot change admin status
        adminService.setUserActive(new UserFlagUpdateDto(adminId, false));
        assertTrue("Admin status did not change.", userDao.findOne(adminId).getActive());
        adminService.setUserActive(new UserFlagUpdateDto(adminId, true));
        assertTrue("Admin status did not change", userDao.findOne(user.getId()).getActive());

        assertTrue(bdrUser.getActive());

        //Cannot change bdrAdmin status.
        adminService.setUserActive(new UserFlagUpdateDto(bdrUser.getId(), false));
        assertTrue("Admin status did not change.", userDao.findOne(bdrUser.getId()).getActive());
        adminService.setUserActive(new UserFlagUpdateDto(bdrUser.getId(), true));
        assertTrue("Admin status did not change", userDao.findOne(bdrUser.getId()).getActive());

    }

    @Test
    public void testSetUserRole() throws Exception {
        loginAdminUser();
        User testUser = userDao.findByUsername(testUserName);

        //test change to BDR admin.
        boolean changed = adminService.setUserRole(new UserRoleUpdateDto(testUser.getId(), Role.ROLE_SALES));
        assertTrue(changed);
        Set<Role> roles = userDao.findOne(testUser.getId()).getRoles();
        assertFalse(roles.contains(Role.ROLE_ADMIN));
        assertFalse(roles.contains(Role.ROLE_EXTERNAL));
        assertTrue(roles.contains(Role.ROLE_SALES));
        assertFalse(roles.contains(Role.ROLE_VALIDATED));

        //test unchanged/not allowed Role
        changed = adminService.setUserRole(new UserRoleUpdateDto(testUser.getId(), Role.ROLE_VALIDATED));
        assertFalse(changed);
        roles = userDao.findOne(testUser.getId()).getRoles();
        assertFalse(roles.contains(Role.ROLE_ADMIN));
        assertFalse(roles.contains(Role.ROLE_EXTERNAL));
        assertTrue(roles.contains(Role.ROLE_SALES));
        assertFalse(roles.contains(Role.ROLE_VALIDATED));

        changed = adminService.setUserRole(new UserRoleUpdateDto(testUser.getId(), Role.ROLE_ADMIN));
        assertTrue(changed);
        roles = userDao.findOne(testUser.getId()).getRoles();
        assertTrue(roles.contains(Role.ROLE_ADMIN));
        assertFalse(roles.contains(Role.ROLE_EXTERNAL));
        assertFalse(roles.contains(Role.ROLE_SALES));
        assertFalse(roles.contains(Role.ROLE_VALIDATED));

        //test no update
        changed = adminService.setUserRole(new UserRoleUpdateDto(testUser.getId(), Role.ROLE_ADMIN));
        assertFalse(changed);
        roles = userDao.findOne(testUser.getId()).getRoles();
        assertTrue(roles.contains(Role.ROLE_ADMIN));
        assertFalse(roles.contains(Role.ROLE_EXTERNAL));
        assertFalse(roles.contains(Role.ROLE_SALES));
        assertFalse(roles.contains(Role.ROLE_VALIDATED));

        loginTestUser();
        //test update with test user.
        changed = adminService.setUserRole(new UserRoleUpdateDto(testUser.getId(), Role.ROLE_EXTERNAL));
        assertTrue(changed);
        userDao.findOne(testUser.getId()).getRoles();
        assertFalse(roles.contains(Role.ROLE_ADMIN));
        assertTrue(roles.contains(Role.ROLE_EXTERNAL));
        assertFalse(roles.contains(Role.ROLE_SALES));
        assertFalse(roles.contains(Role.ROLE_VALIDATED));

        loginTestUser();
        try {
            adminService.setUserRole(new UserRoleUpdateDto(testUser.getId(), Role.ROLE_EXTERNAL));
            fail("Not permissable.");
        } catch (AccessDeniedException e) {
            //pass condition.
        }
    }

    @Test
    public void testUserRetrievalCounts() throws Exception {

        login("sales@infor.com","useruser");
        List<AdminUserDto> users=adminService.getAllUsers(0, 100);
        assertEquals(1,users.size());
        loginAdminUser();
        users=adminService.getAllUsers(0, 100);
        assertEquals(4,users.size());
    }
    @Test
    public void testSuperAdmin() throws Exception {
        login("superadmin@infor.com","supersuperadmin");

    }

    @Test
    public void testSecurityUserHelpers() throws Exception {
        loginAdminUser();
        SecurityUser secUser;
        User testUser = userDao.findByUsername(testUserName);

        //test change to BDR admin.
        assertTrue(adminService.setUserRole(new UserRoleUpdateDto(testUser.getId(), Role.ROLE_SALES)));
        loginTestUser();
        secUser = getCurrentUser();
        assertFalse(secUser.isAdmin());
        assertFalse(secUser.isExternalUser());
        assertTrue(secUser.isSales());

        loginAdminUser();
        assertTrue(adminService.setUserRole(new UserRoleUpdateDto(testUser.getId(), Role.ROLE_ADMIN)));
        loginTestUser();
        secUser = getCurrentUser();
        assertTrue(secUser.isAdmin());
        assertFalse(secUser.isExternalUser());
        assertFalse(secUser.isSales());

        loginAdminUser();
        assertTrue(adminService.setUserRole(new UserRoleUpdateDto(testUser.getId(), Role.ROLE_EXTERNAL)));
        loginTestUser();
        secUser = getCurrentUser();
        assertFalse(secUser.isAdmin());
        assertTrue(secUser.isExternalUser());
        assertFalse(secUser.isSales());
    }

    @Test
    public void testGetBlacklist() throws Exception {
        loginAdminUser();
        List<DomainBlacklist> blacklist;

        blacklist = adminService.getBlacklist();
        assertEquals(0, blacklist.size());

        blacklistDao.save(new DomainBlacklist("gmail\\.com"));
        emDao.flush();

        blacklist = adminService.getBlacklist();
        assertEquals(1, blacklist.size());

    }

    @Test
    public void testAddBlacklist() {
        loginAdminUser();
        List<DomainBlacklist> blacklist;
        DomainBlacklist result;
        blacklist = blacklistDao.findAll();
        assertEquals(0, blacklist.size());

        result = adminService.addBlacklist(new DomainBlacklist("hello\\.com"));
        blacklist = blacklistDao.findAll();
        assertEquals(1, blacklist.size());
        assertNotNull(result);
        assertNotNull(result.getId());
        Integer id = result.getId();

        result = adminService.addBlacklist(new DomainBlacklist("hello\\.com"));
        blacklist = blacklistDao.findAll();
        assertEquals(1, blacklist.size());
        assertEquals(id, result.getId());

        result = adminService.addBlacklist(new DomainBlacklist("gmail.*"));
        blacklist = blacklistDao.findAll();
        assertEquals(2, blacklist.size());
        assertNotNull(result);
        assertNotNull(result.getId());

    }


    @Test
    public void testDeleteBlacklist() throws Exception {
        loginAdminUser();
        List<DomainBlacklist> blacklist;
        DomainBlacklist result;
        blacklist = blacklistDao.findAll();
        assertEquals(0, blacklist.size());

        DomainBlacklist hello = adminService.addBlacklist(new DomainBlacklist("hello\\.com"));
        DomainBlacklist gmail = adminService.addBlacklist(new DomainBlacklist("gmail\\.com"));
        DomainBlacklist yahoo = adminService.addBlacklist(new DomainBlacklist("yahoo\\.com"));
        DomainBlacklist other = adminService.addBlacklist(new DomainBlacklist("OthEr\\.com"));
        blacklist = blacklistDao.findAll();
        assertEquals(4, blacklist.size());

        result = adminService.deleteBlacklist(new DomainBlacklist("nonexistant"));
        blacklist = blacklistDao.findAll();
        assertEquals(4, blacklist.size());
        assertNotNull(result);
        assertNull(result.getId());

        final DomainBlacklist nonexistant = new DomainBlacklist();
        nonexistant.setId(1);
        result = adminService.deleteBlacklist(nonexistant);
        blacklist = blacklistDao.findAll();
        assertEquals(4, blacklist.size());
        assertNotNull(result);
        assertEquals(1, (int) result.getId());
        assertNull(result.getDomain());


        result = adminService.deleteBlacklist(new DomainBlacklist("yahoo\\.com"));
        blacklist = blacklistDao.findAll();
        assertEquals(3, blacklist.size());
        assertNotNull(result);
        assertEquals(yahoo.getId(), result.getId());

        result = adminService.deleteBlacklist(new DomainBlacklist("GMail\\.com"));
        blacklist = blacklistDao.findAll();
        assertEquals(2, blacklist.size());
        assertNotNull(result);
        assertEquals(gmail.getId(), result.getId());

        result = adminService.deleteBlacklist(new DomainBlacklist("otheR\\.com"));
        blacklist = blacklistDao.findAll();
        assertEquals(1, blacklist.size());
        assertNotNull(result);
        assertEquals(other.getId(), result.getId());

        final DomainBlacklist byId = new DomainBlacklist();
        byId.setId(hello.getId());
        result = adminService.deleteBlacklist(byId);
        blacklist = blacklistDao.findAll();
        assertEquals(0, blacklist.size());
        assertNotNull(result);
        assertEquals(hello.getId(), result.getId());
    }

    //Tests going to V1 prod db in read only, and doing SQL queries to get export data directly--turned off until
    //I get back to working on it --DSW ((this test succeeded 4/26/12 11:45am))
    /* 
    @Resource
    RemoteV1ExportComponent rmv1Component;

    @Resource
    ImportExportComponent importExportComponent;

    @Test
    public void testStraightSQLV1Export() {

    	String v1JsonString=rmv1Component.getFormattedV1JsonString(null, null);

    	assertNotNull(v1JsonString);
    	assertTrue(v1JsonString.length()>0);

    }

    @Test
    public void testAutomatedV1Import() {
    	String retValue=importExportComponent.importFromJsonString(rmv1Component.getFormattedV1JsonString(null, null));
    	assertNotNull(retValue);
    	assertTrue(retValue.indexOf("USER")>-1);
    	assertTrue(retValue.indexOf("TRIAL_INSTANCE")>-1);
    	assertTrue(retValue.indexOf("USER_TRACK")>-1);

    	logger.info("Output from automated V1->V2 import:\n"+retValue+"\n");
    }

     */
}
