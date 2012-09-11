package com.infor.cloudsuite.service;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.infor.cloudsuite.AbstractTest;
import com.infor.cloudsuite.dao.DomainBlacklistDao;
import com.infor.cloudsuite.dao.ProductDao;
import com.infor.cloudsuite.dao.ProductVersionDao;
import com.infor.cloudsuite.dao.TrialInstanceDao;
import com.infor.cloudsuite.dao.UserDao;
import com.infor.cloudsuite.dao.UserProductDao;
import com.infor.cloudsuite.dto.TrialDto;
import com.infor.cloudsuite.dto.UserIdDto;
import com.infor.cloudsuite.entity.Product;
import com.infor.cloudsuite.entity.ProductVersion;
import com.infor.cloudsuite.entity.User;
import com.infor.cloudsuite.entity.UserProduct;
import com.infor.cloudsuite.platform.CSWebApplicationException;
import com.infor.cloudsuite.platform.components.NullEmailProvider;
import com.infor.cloudsuite.service.component.TrialEmailComponent;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.fail;
import static org.junit.Assert.assertFalse;


@Transactional(propagation = Propagation.REQUIRED)
public class SuperAdminServiceTest extends AbstractTest {

    @Resource
    private SuperAdminService superAdminService;
    @Resource
    private ProductDao productDao;
    @Resource
    private UserDao userDao;
    @Resource
    private UserProductDao userProductDao;
    @Resource
    private TrialInstanceDao trialInstanceDao;
    @Resource
    private TrialEmailComponent trialEmailComponent;
    @Resource
    private ProductVersionDao productVersionDao;

    @Resource
    private UserService userService;

    @Test
    public void testDeleteUserByUserName() throws Exception {
        loginTestUser();
        final TrialDto trialDto = new TrialDto();
        final Product product = productDao.findByShortName("EAM");
        final ProductVersion productVersion=productVersionDao.findByProductAndName(product,"EAM-BC-3");
        
        trialDto.setProductId(product.getId());
        trialDto.setProductVersionId(productVersion.getId());
        trialDto.setRegionId(2L);

        activateTrial(testUserName, product);
        trialEmailComponent.setEmailProvider(new NullEmailProvider());
        userService.launchTrial(getRequestStub(), trialDto);
        userDao.flush();
        final List<UserProduct> products = userProductDao.findByUserId(userDao.findByUsername(testUserName).getId());
        assertEquals(1, products.size());
        assertFalse(trialInstanceDao.findByUserAndProductVersion_Product(products.get(0).getUser(), products.get(0).getProduct()).isEmpty());

        loginAdminUser();
        superAdminService.deleteUser(new UserIdDto(testUserName));
        User user = userDao.findByUsername(testUserName);
        assertNull(user);

        try {
            superAdminService.deleteUser(new UserIdDto(testAdminName));
            userDao.flush();
            fail("Delete should throw an exception for an administrator.");
        } catch (CSWebApplicationException e) {
            assertEquals(StringDefs.DELETE_USER_ADMIN, e.getResponse().getEntity());
        }
        user = userDao.findByUsername(testAdminName);
        assertNotNull(user);
        assertEquals(testAdminName, user.getUsername());

        try {
            superAdminService.deleteUser(new UserIdDto("notFoundUser@notFound.com"));
            userDao.flush();
            fail("Delete should throw an exception for user not found");
        } catch (CSWebApplicationException e) {
            assertEquals(StringDefs.DELETE_USER_NOT_FOUND, e.getResponse().getEntity());
        }
        user = userDao.findByUsername("notFoundUser@notFound.com");
        assertNull(user);
    }

    @Test
    public void testDeleteUserByUserId() throws Exception {
        loginTestUser();
        final TrialDto trialDto = new TrialDto();
        final Product product = productDao.findByShortName("EAM");        
        final ProductVersion productVersion=productVersionDao.findByProductAndName(product,"EAM-BC-3");
        
        trialDto.setProductId(product.getId());
        trialDto.setProductVersionId(productVersion.getId());
        trialDto.setRegionId(2L);

        trialEmailComponent.setEmailProvider(new NullEmailProvider());
        activateTrial(testUserName, product);
        userService.launchTrial(getRequestStub(), trialDto);
        userDao.flush();
        final List<UserProduct> products = userProductDao.findByUserId(userDao.findByUsername(testUserName).getId());
        assertEquals(1, products.size());
        assertFalse(trialInstanceDao.findByUserAndProductVersion_Product(products.get(0).getUser(), products.get(0).getProduct()).isEmpty());

        loginAdminUser();
        Long userId = userDao.findByUsername(testUserName).getId();
        superAdminService.deleteUser(new UserIdDto(userId));
        User user = userDao.findByUsername(testUserName);
        assertNull(user);

        try {
            userId = userDao.findByUsername(testAdminName).getId();
            superAdminService.deleteUser(new UserIdDto(userId));
            userDao.flush();
            fail("Delete should throw an exception for an administrator.");
        } catch (CSWebApplicationException e) {
            assertEquals(StringDefs.DELETE_USER_ADMIN, e.getResponse().getEntity());
        }
        user = userDao.findByUsername(testAdminName);
        assertNotNull(user);
        assertEquals(testAdminName, user.getUsername());

        try {
            superAdminService.deleteUser(new UserIdDto(2L));//Ids start at 1000
            userDao.flush();
            fail("Delete should throw an exception for user not found");
        } catch (CSWebApplicationException e) {
            assertEquals(StringDefs.DELETE_USER_NOT_FOUND, e.getResponse().getEntity());
        }
        user = userDao.findByUsername("notFoundUser@notFound.com");
        assertNull(user);
    }
}
