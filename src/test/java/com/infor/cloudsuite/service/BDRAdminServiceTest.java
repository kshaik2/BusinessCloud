package com.infor.cloudsuite.service;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.infor.cloudsuite.AbstractTest;
import com.infor.cloudsuite.dao.ProductDao;
import com.infor.cloudsuite.dao.ProductVersionDao;
import com.infor.cloudsuite.dao.RegionDao;
import com.infor.cloudsuite.dao.UserDao;
import com.infor.cloudsuite.dao.UserProductDao;
import com.infor.cloudsuite.dto.BDRAdminUserDto;
import com.infor.cloudsuite.dto.BdrLeadDetails;
import com.infor.cloudsuite.dto.BdrTrialInfoDto;
import com.infor.cloudsuite.dto.TrialDto;
import com.infor.cloudsuite.entity.Company;
import com.infor.cloudsuite.entity.Product;
import com.infor.cloudsuite.entity.ProductVersion;
import com.infor.cloudsuite.entity.Region;
import com.infor.cloudsuite.entity.Role;
import com.infor.cloudsuite.entity.User;
import com.infor.cloudsuite.entity.UserProductKey;

import com.infor.cloudsuite.platform.components.NullEmailProvider;
import com.infor.cloudsuite.platform.components.SettingsProvider;
import com.infor.cloudsuite.platform.security.SecurityService;
import com.infor.cloudsuite.service.component.TrialEmailComponent;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import com.infor.cloudsuite.dao.CompanyDao;

/**
 * User: bcrow
 * Date: 11/8/11 12:08 PM
 */
@Transactional(propagation = Propagation.REQUIRED)
public class BDRAdminServiceTest extends AbstractTest {
    private static final Logger logger = LoggerFactory.getLogger(BDRAdminServiceTest.class);

    @Resource
    private ProductVersionDao productVersionDao;
    @Resource
    private BDRAdminService bdrAdminService;

    @Resource
    private SecurityService securityService;

    @Resource
    private UserDao userDao;

    @Resource
    private SettingsProvider settingsProvider;

    @Resource
    private TrialService trialService;
    @Resource
    private ProductDao productDao;
    @Resource
    private UserProductDao userProductDao;
    @Resource
    private RegionDao regionDao;
    @Resource
    private TrialEmailComponent trialEmailComponent;
    @Resource
    private CompanyDao companyDao;
    

    @Test
    public void testGetAllLeads() throws Exception {
        logger.debug("TESTING exclusion of infor.com emails scenario");
        testGetAllLeads(true);


    }

    @Test
    public void testGetAllLeadsIncludeInfor() throws Exception {
        logger.debug("TESTING !exclusion of infor.com emails scenario");
        testGetAllLeads(false);
    }

    private void testGetAllLeads(boolean excludeInfor) throws Exception {

        settingsProvider.setExcludeInforEmailsFromLeads(excludeInfor);

        int first, second, third;
        if (!excludeInfor) {
            first = 1;
            second = 4;
            third = 3;
        } else {
            first = 0;
            second = 3;
            third = 2;
        }

        this.addBDRAdminUser();
        this.loginBDRAdmin();

        List<BDRAdminUserDto> userDtos = bdrAdminService.getAllLeads(0, 100);
        assertNotNull(userDtos);
        //we should have only 1 user at the get go that is not an admin
        assertEquals(first, userDtos.size());

        addUser("user1@fake.domain", "User1", "One");
        addUser("user2@fake.domain", "User2", "Two");
        addUser("user3@fake.domain", "User3", "Three");

        userDtos = bdrAdminService.getAllLeads(0, 100);
        assertNotNull(userDtos);
        logger.debug("userDtos.size()==" + userDtos.size());
        //we should have four after asking for four

        assertEquals(second, userDtos.size());

        //no, make one of our new users an admin
        User user = userDao.findByUsername("user1@fake.domain");
        user.getRoles().clear();
        user.getRoles().add(Role.ROLE_ADMIN);
        userDao.save(user);
        userDtos = bdrAdminService.getAllLeads(0, 100);
        assertNotNull(userDtos);
        //should now be 3
        assertEquals(third, userDtos.size());

    }

    @Test
    public void testGetLeadDetails() throws Exception {
        loginTestUser();
        Region region = regionDao.getReference(2L);

        User user = userDao.findByUsername(testUserName);
        Product product = productDao.findByShortName("EAM");
        ProductVersion productVersion=productVersionDao.findByProductAndName(product, "EAM-BC-3");
        trialEmailComponent.setEmailProvider(new NullEmailProvider());
        TrialDto trialDto = trialService.launchTrial(getRequestStub(), productVersion, user, region, Locale.US);
        Long trialId = trialDto.getId();
        final Date expirationDate = trialDto.getExpirationDate();

        //Needed because the trial data is not written to the database yet.
        user.getUserProducts().put(product.getId(), userProductDao.findById(new UserProductKey(user, product)));

        addBDRAdminUser();
        loginBDRAdmin();

        final BdrLeadDetails leadDetails = bdrAdminService.getLeadDetails(user.getId());
        assertNotNull(leadDetails);
        assertEquals(user.getUsername(), leadDetails.getUserName());
        assertEquals(user.getAddress1(), leadDetails.getAddress1());
        assertEquals(user.getAddress2(), leadDetails.getAddress2());
        assertEquals(user.getCompany().getName(), leadDetails.getCompanyName());
        assertEquals(user.getCountry(), leadDetails.getCountry());
        assertEquals(user.getPhone(), leadDetails.getPhone());
        assertEquals(user.getCreatedAt(), leadDetails.getCreatedAt());
        assertEquals(user.getInforCustomer(), leadDetails.getInforCustomer());
        final List<BdrTrialInfoDto> trials = leadDetails.getTrials();
        assertEquals(1, trials.size());
        final BdrTrialInfoDto infoDto = trials.get(0);
        assertEquals(trialId, infoDto.getTrialId());
        assertEquals(expirationDate, infoDto.getExpirationDate());
        assertEquals(product.getShortName(), infoDto.getProductShortName());
    }

    private void loginBDRAdmin() {
        this.login("bdradmin@infor.com", "bdradmin");
    }

    private void addUser(String username, String firstName, String lastName) {

    	Company test=companyDao.findByName("Test Company");
        User user = new User();
        user.setCompany(test);
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.getRoles().add(Role.ROLE_EXTERNAL);
        user.setActive(true);
        user.setCreatedAt(new Date());
        user.setPassword(securityService.encodePassword("password", user.getCreatedAt()));
        userDao.save(user);
    }

    private void addBDRAdminUser() {
        User user = new User();
        user.setUsername("bdradmin@infor.com");
        user.setFirstName("BDRAdminFName");
        user.setLastName("BDRAdminLName");
        user.setActive(true);
        user.setCreatedAt(new Date());
        user.getRoles().add(Role.ROLE_SALES);
        user.setPassword(securityService.encodePassword("bdradmin", user.getCreatedAt()));

        userDao.save(user);
    }


}