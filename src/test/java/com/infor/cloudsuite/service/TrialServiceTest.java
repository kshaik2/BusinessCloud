package com.infor.cloudsuite.service;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;

import javax.annotation.Resource;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.annotation.Transactional;

import com.infor.cloudsuite.AbstractTest;
import com.infor.cloudsuite.EntityManagerDao;
import com.infor.cloudsuite.dao.ProductDao;
import com.infor.cloudsuite.dao.ProductVersionDao;
import com.infor.cloudsuite.dao.RegionDao;
import com.infor.cloudsuite.dao.TrialEnvironmentDao;
import com.infor.cloudsuite.dao.TrialInstanceDao;
import com.infor.cloudsuite.dao.TrialProductChildDao;
import com.infor.cloudsuite.dao.UserDao;
import com.infor.cloudsuite.dao.UserTrackingDao;
import com.infor.cloudsuite.dto.RedirectUrlDto;
import com.infor.cloudsuite.dto.TrialDto;
import com.infor.cloudsuite.entity.*;
import com.infor.cloudsuite.platform.components.MessageProvider;
import com.infor.cloudsuite.platform.components.NullEmailProvider;
import com.infor.cloudsuite.platform.security.SecurityService;
import com.infor.cloudsuite.service.component.DeploymentServiceComponent;
import com.infor.cloudsuite.service.component.TrialEmailComponent;
import com.infor.cloudsuite.service.component.TrialObjectsCreatorComponent;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * User: bcrow
 * Date: 10/25/11 2:00 PM
 */
@Transactional
public class TrialServiceTest extends AbstractTest {
    private static final Logger logger = LoggerFactory.getLogger(TrialServiceTest.class);

    @Resource
    private TrialEnvironmentDao trialEnvironmentDao;
    @Resource
    private TrialProductChildDao trialProductChildDao;
    @Resource
    private TrialService trialService;
    @Resource
    private ProductService productService;
    @Resource
    private UserDao userDao;
    @Resource
    private SecurityService securityService;
    @Resource
    private EntityManagerDao emService;
    @Resource
    private ProductDao productDao;
    @Resource
    private ProductVersionDao productVersionDao;
    @Resource
    private DeploymentServiceComponent deploymentServiceComponent;
    
    @Resource
    private TrialInstanceDao trialInstanceDao;
    @Resource
    private RegionDao regionDao;
    @Resource
    private UserTrackingDao userTrackingDao;
    @Resource
    private TrialEmailComponent trialEmailComponent;
    @Resource
    private MessageProvider messageProvider;
    @Resource
    private TrialObjectsCreatorComponent trialObjectsCreatorComponent;


    @Test
    public void testAddTrialEnvironment() throws Exception {
        loginTestUser();
        List<TrialEnvironment> environments = trialService.getEnvironments(1L);
        int enviroSize = environments.size();

        User user = new User();
        user.setUsername("test@test.com");
        user.setFirstName("Test");
        user.setLastName("Test");
        user.setCreatedAt(new Date());
        user.getRoles().add(Role.ROLE_EXTERNAL);
        user.setPassword(securityService.encodePassword("test", user.getCreatedAt()));
        userDao.save(user);

        Product product = new Product();
        product.setShortName("Test");
        product.setName("Test Long");

        productService.addProduct(product);

        ProductVersion productVersion=new ProductVersion();
        productVersion.setName("Test-version");
        productVersion.setCreatedAt(new Date());
        productVersion.setProduct(product);
        productVersion.setIeOnly(true);
        productVersionDao.save(productVersion);
        product.getProductVersions().add(productVersion);
        productDao.save(product);
        
        emService.flush();

        TrialDto trial = new TrialDto();
        trial.setProductId(product.getId());
        trial.setProductVersionId(productVersion.getId());
        trial.setUserId(user.getId());
        trial.setUrl("http://test/hahahaha");
        trial.setUsername("test");
        trial.setPassword("haha");
        trialService.createTrialEnvironment(trial);
        emService.flush();

        environments = trialService.getEnvironments(product.getId());
        assertEquals(enviroSize + 1, environments.size());
    }

    @Test
    public void testRequestTrialApprove() {
        final MockHttpServletRequest requestStub = super.getRequestStub();

        List<ProductVersion> productVersions=new ArrayList<ProductVersion>();

        productVersions.add(productVersionDao.findByProductAndName(productDao.findByShortName("EAM"),"EAM-BC-3"));
        productVersions.add(productVersionDao.findByProductAndName(productDao.findByShortName("XM"),"XM-BC-3"));
        User user = userDao.findByUsername(testUserName);
        Region region = regionDao.findById(2L);
        Locale locale = Locale.US;
        String comment = "Test comment.";

        final NullEmailProvider emailProvider = new NullEmailProvider();
        trialEmailComponent.setEmailProvider(emailProvider);

        TrialRequest trialRequest = trialService.requestTrial(requestStub, productVersions, user, region, locale, comment);
        List<NullEmailProvider.EmailInfo> asyncEmails = emailProvider.getAsyncEmails();
        assertEquals("Verify email count", 2, asyncEmails.size());
        NullEmailProvider.EmailInfo emailInfo = asyncEmails.get(0);
        assertEquals(messageProvider.getMessage(StringDefs.MESSAGE_TRIAL_REQUEST_SUBJECT, trialEmailComponent.productNamesStrungForEmail(productVersions)), emailInfo.subject);
        assertTrue(emailInfo.text.contains("Products: " + trialEmailComponent.productNamesStrungForEmail(productVersions)));
        assertTrue(emailInfo.text.contains("User: " + user.getUsername()));
        assertTrue(emailInfo.text.contains("Comment: " + comment));
        assertTrue(emailInfo.text.contains("Region: " + region.getName()));
        assertEquals(StringDefs.BC_LEADS_EMAIL, emailInfo.address);
        String done = trialService.approveTrialRequest(requestStub, trialRequest.getRequestKey());
        assertEquals("Done", done);
        
        try {
            trialService.deleteTrialRequest(trialRequest.getRequestKey());
            fail("Delete should fail, as it has already been deleted.");
        } catch (DataAccessException dae) {
            //eat.
        }
    }

    @Test
    public void testRequestTrialDelete() {
        final MockHttpServletRequest requestStub = super.getRequestStub();
        List<ProductVersion> productVersions = new ArrayList<ProductVersion>();
        productVersions.add(productVersionDao.findByProductAndName(productDao.findByShortName("EAM"),"EAM-BC-3"));
        User user = userDao.findByUsername(testUserName);
        Region region = regionDao.findById(1L);
        Locale locale = Locale.US;
        String comment = "Test comment.";

        final NullEmailProvider emailProvider = new NullEmailProvider();
        trialEmailComponent.setEmailProvider(emailProvider);

        TrialRequest trialRequest = trialService.requestTrial(requestStub, productVersions, user, region, locale, comment);
        List<NullEmailProvider.EmailInfo> asyncEmails = emailProvider.getAsyncEmails();
        assertEquals(2, asyncEmails.size());
        NullEmailProvider.EmailInfo emailInfo = asyncEmails.get(0);
        assertEquals(messageProvider.getMessage(StringDefs.MESSAGE_TRIAL_REQUEST_SUBJECT, trialEmailComponent.productNamesStrungForEmail(productVersions)), emailInfo.subject);
        assertTrue(emailInfo.text.contains("Products: " + trialEmailComponent.productNamesStrungForEmail(productVersions)));
        assertTrue(emailInfo.text.contains("User: " + user.getUsername()));
        assertTrue(emailInfo.text.contains("Comment: " + comment));
        assertTrue(emailInfo.text.contains("Region: " + region.getName()));

        String done = trialService.deleteTrialRequest(trialRequest.getRequestKey());
        assertEquals("Deleted", done);
        
        try {
            trialService.approveTrialRequest(requestStub, trialRequest.getRequestKey());
            fail("Delete should fail, as it has already been deleted.");
        } catch (DataAccessException dae) {
            //eat.
        }
    }

    @Test
    public void testRequestTrialSize() {

        final MockHttpServletRequest requestStub = super.getRequestStub();
        List<ProductVersion> productVersions = new ArrayList<ProductVersion>();
        productVersions.add(productVersionDao.findByProductAndName(productDao.findByShortName("EAM"),"EAM-BC-3"));
        User user = userDao.findByUsername(testUserName);
        Region region = regionDao.findById(1L);
        Locale locale = Locale.US;
        String comment = "Test comment.";

        final NullEmailProvider emailProvider = new NullEmailProvider();
        trialEmailComponent.setEmailProvider(emailProvider);

        TrialRequest trialRequest = trialService.requestTrial(requestStub, productVersions, user, region, locale, comment);

        List<NullEmailProvider.EmailInfo> asyncEmails = emailProvider.getAsyncEmails();
        assertEquals(2, asyncEmails.size());
        NullEmailProvider.EmailInfo emailInfo = asyncEmails.get(0);
        assertEquals(messageProvider.getMessage(StringDefs.MESSAGE_TRIAL_REQUEST_SUBJECT, trialEmailComponent.productNamesStrungForEmail(productVersions)), emailInfo.subject);
        assertTrue(emailInfo.text.contains("Products: " + trialEmailComponent.productNamesStrungForEmail(productVersions)));
        assertTrue(emailInfo.text.contains("User: " + user.getUsername()));
        assertTrue(emailInfo.text.contains("Comment: " + comment));
        assertTrue(emailInfo.text.contains("Region: " + region.getName()));
        logger.debug("\n----- EMAIL BODY --- \n" + emailInfo.text + "\n---------------");
        
        loginAdminUser();

        assertEquals(1, trialService.getTrialRequests().size());
        trialService.deleteTrialRequest(trialRequest.getRequestKey());
        assertEquals(0, trialService.getTrialRequests().size());
    }

    @Test
    public void testGetActualUrl() {
        ProductVersion productVersion = productVersionDao.findByProductAndName(productDao.findByShortName("EAM"),"EAM-BC-3");
        User user = userDao.findByUsername(testUserName);
        Region region = regionDao.getReference(2L);
        final TrialDto trialDto = trialService.launchTrial(getRequestStub(), productVersion, user, region, Locale.US);
        String guid = trialDto.getGuid();
        String url = trialDto.getUrl();
        assertNotNull(guid);
        assertNotNull(url);
        final TrialInstance actual = trialInstanceDao.findById(trialDto.getId());
        assertEquals(guid, actual.getGuid());
        assertEquals(url, actual.getUrl());

        RedirectUrlDto redirectUrlDto = trialService.getActualUrl(guid);
        assertNotNull(redirectUrlDto);
        assertEquals(actual.getUrl(), redirectUrlDto.getRedirectUrl());
        List<UserTracking> data = userTrackingDao.findByTrackingTypeAndUser(TrackingType.PROXY_URL_HIT, user);
        assertNotNull(data);
        assertEquals(1, data.size());
        assertEquals(actual.getId(), data.get(0).getTargetObject());

        redirectUrlDto = trialService.getActualUrl(guid);
        assertEquals(actual.getUrl(), redirectUrlDto.getRedirectUrl());
        data = userTrackingDao.findByTrackingTypeAndUser(TrackingType.PROXY_URL_HIT, user);
        assertNotNull(data);
        assertEquals(2, data.size());
        assertEquals(actual.getId(), data.get(0).getTargetObject());
        assertEquals(actual.getId(), data.get(1).getTargetObject());


    }

@Test
public void testTrialProductChild() throws Exception {
	assertEquals(3,trialProductChildDao.count());
	ProductVersion productVersion=productVersionDao.findByProductAndName(productDao.findByShortName("EAM"), "EAM-BC-3");
	TrialEnvironment environment=trialEnvironmentDao.findByProductVersionAndRegionAndAvailable(productVersion,regionDao.findByShortName("NA"),true).get(0);
	for (TrialProductChild tpc : trialProductChildDao.findByRegion(regionDao.findByShortName("NA"))) {
		
		logger.error("tpc.id:"+tpc.getId());
		logger.error("tpc.getChildVersion().getName():"+tpc.getChildVersion().getName());
		logger.error("tpc.getParentVersion().getName():"+tpc.getParentVersion().getName());

	}
	assertEquals("3 records for region:NA",3,trialProductChildDao.findByRegion(regionDao.findByShortName("NA")).size());
	assertEquals("3 records with WS as parent",3,trialProductChildDao.findByRegionAndParentVersion(regionDao.findByShortName("NA"),productVersionDao.findByName("WS-BC-3")).size());
	assertEquals("1 record with EAM as child",1,trialProductChildDao.findByRegionAndChildVersion(regionDao.findByShortName("NA"), productVersion).size());
	assertEquals("2 records should come back for EAM delete",2,trialObjectsCreatorComponent.deleteEnvironmentAndRelations(productVersion, regionDao.findByShortName("NA"),environment).size());
	
}
}

