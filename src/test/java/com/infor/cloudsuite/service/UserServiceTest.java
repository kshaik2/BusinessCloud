package com.infor.cloudsuite.service;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.infor.cloudsuite.AbstractTest;
import com.infor.cloudsuite.CloudSuiteApp;
import com.infor.cloudsuite.EntityManagerDao;
import com.infor.cloudsuite.dao.AmazonCredentialsDao;
import com.infor.cloudsuite.dao.IndustryDao;
import com.infor.cloudsuite.dao.ProductDao;
import com.infor.cloudsuite.dao.ProductDescriptionDao;
import com.infor.cloudsuite.dao.ProductVersionDao;
import com.infor.cloudsuite.dao.RegionDao;
import com.infor.cloudsuite.dao.ScheduleDao;
import com.infor.cloudsuite.dao.TrialEnvironmentDao;
import com.infor.cloudsuite.dao.TrialInstanceDao;
import com.infor.cloudsuite.dao.UserDao;
import com.infor.cloudsuite.dao.UserProductDao;
import com.infor.cloudsuite.dto.AmazonCredentialsDto;
import com.infor.cloudsuite.dto.DeployActionScheduleType;
import com.infor.cloudsuite.dto.DeployRequestDto;
import com.infor.cloudsuite.dto.DeploymentStackDto;
import com.infor.cloudsuite.dto.DeploymentType;
import com.infor.cloudsuite.dto.ProductUserProductDto;
import com.infor.cloudsuite.dto.RegistrationCompleteDto;
import com.infor.cloudsuite.dto.TrialDto;
import com.infor.cloudsuite.entity.AmazonCredentials;
import com.infor.cloudsuite.entity.Product;
import com.infor.cloudsuite.entity.ProductVersion;
import com.infor.cloudsuite.entity.Region;
import com.infor.cloudsuite.entity.Role;
import com.infor.cloudsuite.entity.Schedule;
import com.infor.cloudsuite.entity.ScheduleStatus;
import com.infor.cloudsuite.entity.ScheduleType;
import com.infor.cloudsuite.entity.TrialInstance;
import com.infor.cloudsuite.entity.TrialInstanceType;
import com.infor.cloudsuite.entity.User;
import com.infor.cloudsuite.entity.UserProduct;
import com.infor.cloudsuite.platform.CSWebApplicationException;
import com.infor.cloudsuite.platform.components.GuidProvider;
import com.infor.cloudsuite.platform.components.MessageProvider;
import com.infor.cloudsuite.platform.components.NullEmailProvider;
import com.infor.cloudsuite.platform.components.NullEmailProvider.EmailInfo;
import com.infor.cloudsuite.platform.components.SettingsProvider;
import com.infor.cloudsuite.platform.security.SecurityService;
import com.infor.cloudsuite.service.component.DeploymentServiceComponent;
import com.infor.cloudsuite.service.component.ImportExportComponent;
import com.infor.cloudsuite.service.component.ImportFileTypeEnum;
import com.infor.cloudsuite.service.component.TrialEmailComponent;
import com.infor.cloudsuite.service.component.TrialObjectsCreatorComponent;

@Transactional(propagation = Propagation.REQUIRED)
public class UserServiceTest extends AbstractTest {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceTest.class);

    @Resource
    private UserService service;
    @Resource
    private UserAdminService userAdminService;
    @Resource
    private ImportExportComponent importExportComponent;
    @Resource
    private UserDao userDao;
    @Resource
    private ProductDao productDao;
    @Resource
    ProductDescriptionDao productDescriptionDao;

    @Resource
    private TrialInstanceDao instanceDao;

    @Resource
    private ScheduleDao scheduleDao;
    @Resource
    private UserProductDao userProductDao;
    @Resource
    private SecurityService securityService;
    @Resource
    private EntityManagerDao emService;
    @Resource
    private SettingsProvider settingsProvider;
    @Resource
    private TrialEnvironmentDao trialEnvironmentDao;
    @Resource
    private TrialInstanceDao trialInstanceDao;
    @Resource
    private TrialService trialService;
    @Resource
    private MessageProvider messageProvider;
    @Resource
    private AmazonCredentialsDao amazonCredentialsDao;
    @Resource
    private RegionDao regionDao;
    @Resource
    private TrialObjectsCreatorComponent trialObjectsCreatorComponent;
    @Resource
    private TrialEmailComponent trialEmailComponent;

    @Resource
    private DeploymentService deploymentService;
    @Resource
    private IndustryDao industryDao;
    @Resource
    private DeploymentServiceComponent deploymentServiceComponent;
    @Resource
    private ProductVersionDao productVersionDao;
    
    
    HttpServletRequest request;

    @Before
    public void setUp() {
        HttpSession session = new MockHttpSession();
        request = getRequestStub(session);
    }

    @Test
    public void testCloudSuiteApp() {
        assertNotNull("CloudSuiteApp not null", new CloudSuiteApp());
    }

    @Test
    public void testEmptyGetProducts() {
        loginTestUser();
        final List<UserProduct> products = service.getProducts();
        assertTrue("No products yet.", products.isEmpty());
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRED)
    public void testGetOwnedProducts_AndUpdate() throws Exception {
        final String username = "user@company.com";
        final String password = "password";
        User user = this.createUser(username, "Head", "Honcho", password, Role.ROLE_EXTERNAL);
        userDao.save(user);
        emService.flush();
        Product product = productDao.findByShortName("EAM");
        UserProduct userProduct = new UserProduct(user, product);
        userProduct.setOwned(true);
        userProductDao.save(userProduct);

        product = productDao.findByShortName("XM");
        userProduct = new UserProduct(user, product);
        userProduct.setOwned(true);
        userProductDao.save(userProduct);
        emService.flush();

        login(username, password);
        List<ProductUserProductDto> pupDtos = service.getOwnedProducts();
        assertEquals("Should have 2 owned products", 2, pupDtos.size());

        //update both to false :)
        for (ProductUserProductDto pupDto : pupDtos) {

            pupDto.setOwned(false);
            service.updateUserProductOwned(pupDto);
        }

        pupDtos = service.getOwnedProducts();
        assertEquals("Should have no owned products", 0, pupDtos.size());
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRED)
    public void testGetProducts() throws Exception {
        final String username = "user@user.com";
        final String password = "user";

        User user = createUser(username, "User", "Test", password, Role.ROLE_EXTERNAL);
        userDao.save(user);
        emService.flush();

        Product product = productDao.findByShortName("XM");
        ProductVersion productVersion=deploymentServiceComponent.latestProductVersion(product.getProductVersions());
        assertNotNull(productVersion);
        UserProduct userProduct = new UserProduct(user, product);
        userProductDao.save(userProduct);
        emService.flush();

        login(username, password);
        List<UserProduct> products = service.getProducts();
        assertEquals(1, products.size());
        UserProduct currUP = products.get(0);
        assertEquals(user.getId(), currUP.getUser().getId());
        assertEquals(product.getId(), currUP.getProduct().getId());
        assertFalse(currUP.getTrialAvailable());
        assertFalse(currUP.getLaunchAvailable());
        assertEquals(0, trialInstanceDao.findByUserAndProductVersion_Product(user, product).size());
        TrialInstance trial = new TrialInstance();
        final String guid = UUID.randomUUID().toString();
        trial.setGuid(guid);
        trial.setProductVersion(productVersion);
        trial.setRegion(regionDao.getReference(1L));
        final String envId = "TRIAL_TEST";
        trial.setEnvironmentId(envId);
        trial.setUser(user);
        trial.setUrl("http://this.url");
        trial.setUsername("R5");
        trial.setPassword("R5");
        Calendar cal = Calendar.getInstance();
        trial.setCreatedAt(cal.getTime());
        cal.add(Calendar.DATE, 90);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        trial.setExpirationDate(cal.getTime());
        instanceDao.save(trial);
        emService.flush();

        userProduct.setLaunchAvailable(false);
        trialInstanceDao.save(trial);//userProduct.getTrialInstances().add(trial);
        userProduct.setTrialAvailable(false);
        userProductDao.save(userProduct);
        emService.flush();

        products = service.getProducts();
        assertEquals(1, products.size());
        currUP = products.get(0);
        assertEquals(user.getId(), currUP.getUser().getId());
        assertEquals(product.getId(), currUP.getProduct().getId());
        assertFalse(currUP.getTrialAvailable());
        assertFalse(currUP.getLaunchAvailable());
        assertFalse(trialInstanceDao.findByUserAndProductVersion_Product(user, product).isEmpty());

        Object[] o = trialInstanceDao.findByUserAndProductVersion_Product(user, product).toArray();
        TrialInstance instance = (TrialInstance) o[0];

        assertEquals(guid, instance.getGuid());
        assertEquals("R5", instance.getUsername());
        assertEquals("R5", instance.getPassword());
        assertEquals(envId, instance.getEnvironmentId());
        assertEquals(user.getId(), instance.getUser().getId());
        logger.debug("User ID: {}", currUP.getId().getUser().getId());
        logger.debug("Product Id: {}", currUP.getId().getProduct().getId());
        logger.debug("TrialInstance: {}, Expriration date: {}", instance.getId(), DateFormat.getDateInstance().format(instance.getExpirationDate()));

        product = productDao.findByShortName("EAM");
        assertNotNull(product);

        final List<ProductUserProductDto> allWithUserProducts = service.getAllProducts();
        assertEquals("All products should return user product data for this user",productDao.count(),allWithUserProducts.size());
        //assertEquals(4, allWithUserProducts.size());
        for (ProductUserProductDto pupDto : allWithUserProducts) {
            logger.debug("PUP ID" + pupDto.getId());
            logger.debug("product Id: {}", pupDto.getId());
            logger.debug("User ID: {}", user.getId());
            logger.debug("Product {}: {}", pupDto.getShortName(), pupDto.getName());
            logger.debug("Deployment Available: {}", pupDto.getAvailability().getDeployment());
            logger.debug("Trial Available: {}", pupDto.getAvailability().getTrial());
            logger.debug("User Launch Available: {}", pupDto.getSecurity().getDeployment());
            logger.debug("User Trial Available: {}", pupDto.getSecurity().getTrial());
            logger.debug("Descriptions size(): {}", pupDto.getDescriptions().size());
            Product currProduct = productDao.findById(pupDto.getId());
            /*
             * NEED TO RE-DO!!
             */

            long pdCount=productDescriptionDao.countProductDescriptionsByProduct(currProduct);
            assertEquals("Products match", currProduct.getShortName(), pupDto.getShortName());
            assertEquals("Compare product desc counts",pdCount,pupDto.getDescriptions().size());
            assertEquals("Tile size",currProduct.getTileSize().name(), pupDto.getTileSize());
            assertEquals("trials avail", currProduct.getTrialsAvailable(), pupDto.getAvailability().getTrial());
           //assertEquals("IE Only", currProduct.getIeOnly(), pupDto.getIeOnly());
            assertEquals("Deployments available", currProduct.getDeploymentsAvailable(), pupDto.getAvailability().getDeployment());
            assertEquals("Tile Order", currProduct.getTileOrder(), pupDto.getTileOrder());
        }
    }

    @Test
    public void testGetMissingProduct() throws Exception {
        final Product nonexistant = productDao.findByShortName("NONEXISTANT");
        //make sure an exception is not thrown.
        assertNull(nonexistant);
    }

    @Test
    @Transactional
    public void testLaunchTrialNonProd() throws Exception {
        boolean currMode = settingsProvider.isProductionMode();
        settingsProvider.setProductionMode(false);
        boolean currSetting = settingsProvider.isTrialRecycleInforDomain();
        settingsProvider.setTrialRecycleInforDomain(false);
        
        Region na = regionDao.findByShortName("NA");
        Product eam = productDao.findByShortName("EAM");
        ProductVersion eamVersion = productVersionDao.findByName("EAM-BC-3");
        loginAdminUser();
        activateTrial("admin@infor.com",eam);
        TrialDto dtoIn = new TrialDto();
        dtoIn.setProductId(eam.getId());
        dtoIn.setProductVersionId(eamVersion.getId());
        dtoIn.setRegionId(na.getId());
        long size=trialEnvironmentDao.findByProductVersion_ProductAndRegionAndAvailable(eam, na, true).size();
        while ( size-- > 0) {
        	service.launchTrial(request, dtoIn);
        	
        	
        }
        for (int i=0; i<10; i++){
        	service.launchTrial(request, dtoIn);
        	service.launchTrial(request,dtoIn);
        }
        
        
        settingsProvider.setProductionMode(currMode);
        settingsProvider.setTrialRecycleInforDomain(currSetting);
    }
    
    
    @Test
    @Transactional(propagation = Propagation.REQUIRED)
    public void testLaunchTrial() throws Exception {

        boolean currMode = settingsProvider.isProductionMode();
        settingsProvider.setProductionMode(true);
        boolean currSetting = settingsProvider.isTrialRecycleInforDomain();
        settingsProvider.setTrialRecycleInforDomain(false);
        loginTestUser();
        Product product = productDao.findByShortName("EAM");
        ProductVersion productVersion=productVersionDao.findByProductAndName(product, "EAM-BC-3");
        TrialDto dtoIn = new TrialDto();
        dtoIn.setProductId(product.getId());
        dtoIn.setProductVersionId(productVersion.getId());
        Long regionId = 2L;
        dtoIn.setRegionId(regionId);

        final NullEmailProvider testEmailProvider = new NullEmailProvider();
        trialEmailComponent.setEmailProvider(testEmailProvider);

        final String guid = "Test-Guid";
        GuidProvider currGuidProvider = trialObjectsCreatorComponent.getGuidProvider();
        TestGuidProvider testGuidProvider = new TestGuidProvider();
        testGuidProvider.setCurrGuid(guid);
        trialObjectsCreatorComponent.setGuidProvider(testGuidProvider);
        activateTrial(testUserName, product);

        TrialDto dto = service.launchTrial(getRequestStub(), dtoIn);
        trialObjectsCreatorComponent.setGuidProvider(currGuidProvider);
        assertEquals(1, testEmailProvider.getAsyncEmails().size());
        assertNotNull(dto);
        assertNotNull(dto.getExpirationDate());
        assertNotNull(dto.getId());
        assertNotNull(dto.getProductId());
        assertEquals(product.getId(), dto.getProductId());
        assertEquals(regionId, dto.getRegionId());
        assertNotNull(dto.getEnvironmentId());
        assertNotNull(dto.getUrl());
        assertNotNull(dto.getUsername());
        assertEquals("R5", dto.getUsername());
        assertEquals(guid, dto.getGuid());
        assertNotNull(dto.getPassword());
        assertEquals("Infor273", dto.getPassword());

        final TrialInstance byGuid = trialInstanceDao.findByGuid(guid);
        assertNotNull(byGuid);
        assertEquals(guid, byGuid.getGuid());
        Region region = regionDao.findById(regionId);
        while (trialEnvironmentDao.countByProductVersionAndRegionAndAvailable(productVersion, region, true) > 6) {
            testEmailProvider.getAsyncEmails().clear();
            final TrialDto trialDto = service.launchTrial(getRequestStub(), dtoIn);
            assertNotNull(trialDto.getCreatedAt());
            logger.debug(trialDto.getUrl());
            assertEquals("No launch warning :" + trialEnvironmentDao.count(), 1, testEmailProvider.getAsyncEmails().size());
            final String text = testEmailProvider.getAsyncEmails().get(0).text;
            assertNotNull(text);
            String emailUrl = messageProvider.getMessageDef(StringDefs.MESSAGE_TRIAL_PROXY_URL,
                    StringDefs.MESSAGE_TRIAL_PROXY_URL_DEFAULT,
                    "http://localhost:8080/", trialDto.getGuid());
            assertTrue(text.contains(emailUrl));
        }

        String lastGuid = guid;
        for (int i = 5; i > 0; i--) {
            testEmailProvider.getAsyncEmails().clear();
            TrialDto aDto = service.launchTrial(getRequestStub(), dtoIn);
            assertFalse(lastGuid.equals(aDto.getGuid()));
            lastGuid = aDto.getGuid();
            assertEquals("Send Launch and warning.", 2, testEmailProvider.getAsyncEmails().size());
            assertTrue(testEmailProvider.getAsyncEmails().get(0).subject + ":" + i, testEmailProvider.getAsyncEmails().get(0).subject.contains("low"));
            assertTrue(testEmailProvider.getAsyncEmails().get(0).text.contains(Integer.toString(i)));

        }

        testEmailProvider.getAsyncEmails().clear();
        service.launchTrial(getRequestStub(), dtoIn);
        assertEquals("Send Launch and warning.", 2, testEmailProvider.getAsyncEmails().size());
        logger.debug(testEmailProvider.getAsyncEmails().get(0).subject);
        assertTrue(testEmailProvider.getAsyncEmails().get(0).subject.contains("Zero"));
        logger.debug(testEmailProvider.getAsyncEmails().get(0).address);
        logger.debug(testEmailProvider.getAsyncEmails().get(0).subject);
        logger.debug(testEmailProvider.getAsyncEmails().get(0).text);

        testEmailProvider.getAsyncEmails().clear();
        try {
            service.launchTrial(null, dtoIn);
        } catch (CSWebApplicationException e) {
            assertEquals(StringDefs.GENERAL_ERROR_CODE, e.getResponse().getStatus());
        }
        assertEquals("Send Launch and warning.", 1, testEmailProvider.getAsyncEmails().size());
        NullEmailProvider.EmailInfo emailInfo = testEmailProvider.getAsyncEmails().get(0);
        assertTrue(emailInfo.subject.contains("Zero"));
        logger.debug(emailInfo.address);
        logger.debug(emailInfo.subject);
        logger.debug(emailInfo.text);

        //Test North America Workspaces trials.


        region = regionDao.getReference(1L);
        assertEquals(2L, (long)trialEnvironmentDao.countByProductVersionAndRegionAndAvailable(productVersionDao.findByProductAndName(productDao.findByShortName("WS"),"WS-BC-3"), region, true));
        assertEquals(2L, (long)trialEnvironmentDao.countByProductVersionAndRegionAndAvailable(productVersionDao.findByProductAndName(productDao.findByShortName("XM"), "XM-BC-3"),region, true));
        assertEquals(2L, (long)trialEnvironmentDao.countByProductVersionAndRegionAndAvailable(productVersionDao.findByProductAndName(productDao.findByShortName("Syteline"),"Syteline-BC-3"), region,true));
        assertEquals(2L, (long)trialEnvironmentDao.countByProductVersionAndRegionAndAvailable(productVersionDao.findByProductAndName(productDao.findByShortName("EAM"),"EAM-BC-3"), region, true));


        product = productDao.findByShortName("EAM");
        productVersion=productVersionDao.findByProductAndName(product,"EAM-BC-3");
        testEmailProvider.getAsyncEmails().clear();

        dtoIn = new TrialDto();
        dtoIn.setProductId(product.getId());
        dtoIn.setProductVersionId(productVersion.getId());
        regionId = 1L;
        dtoIn.setRegionId(regionId);

        service.launchTrial(getRequestStub(), dtoIn);

        //check
        assertEquals("Send Launch and warning.", 3, testEmailProvider.getAsyncEmails().size());

        emailInfo = testEmailProvider.getAsyncEmails().get(0);
        assertEquals(messageProvider.getMessage(StringDefs.MESSAGE_LOW_TRIALS_SUBJECT, "EAM:EAM-BC-3"),
                emailInfo.subject);
        emailInfo = testEmailProvider.getAsyncEmails().get(1);
        assertEquals(messageProvider.getMessage(StringDefs.MESSAGE_LOW_TRIALS_SUBJECT, "WS:WS-BC-3"),
                emailInfo.subject);
        emailInfo = testEmailProvider.getAsyncEmails().get(2);
        assertEquals(messageProvider.getMessage(StringDefs.MESSAGE_TRIAL_SUBJECT),
                emailInfo.subject);

        ProductVersion wsProductVersion=productVersionDao.findByProductAndName(productDao.findByShortName("WS"),"WS-BC-3");
        
        assertEquals(regionId, trialEnvironmentDao.countByProductVersionAndRegionAndAvailable(productVersion, region, true));
        assertEquals(regionId, trialEnvironmentDao.countByProductVersionAndRegionAndAvailable(wsProductVersion, region, true));


        product = productDao.findByShortName("XM");
        productVersion=productVersionDao.findByProductAndName(product, "XM-BC-3");
        activateTrial(testUserName, product);
        testEmailProvider.getAsyncEmails().clear();

        dtoIn = new TrialDto();
        dtoIn.setProductId(product.getId());
        dtoIn.setProductVersionId(productVersion.getId());
        dtoIn.setRegionId(regionId);

        service.launchTrial(getRequestStub(), dtoIn);

        assertEquals("Send Launch and warning.", 2, testEmailProvider.getAsyncEmails().size());

        emailInfo = testEmailProvider.getAsyncEmails().get(0);
        assertEquals(messageProvider.getMessage(StringDefs.MESSAGE_LOW_TRIALS_SUBJECT, "XM:XM-BC-3"),
                emailInfo.subject);
        emailInfo = testEmailProvider.getAsyncEmails().get(1);
        assertEquals(messageProvider.getMessage(StringDefs.MESSAGE_TRIAL_SUBJECT),
                emailInfo.subject);

        assertEquals(Long.valueOf(1L), trialEnvironmentDao.countByProductVersionAndRegionAndAvailable(productVersion, region, true));
        assertEquals(Long.valueOf(1L), trialEnvironmentDao.countByProductVersionAndRegionAndAvailable(wsProductVersion, region, true));


        product = productDao.findByShortName("WS");
        productVersion=productVersionDao.findByProductAndName(product,"WS-BC-3");
        assertNotNull(productVersion);
        activateTrial(testUserName, product);
        testEmailProvider.getAsyncEmails().clear();

        dtoIn = new TrialDto();
        dtoIn.setProductId(product.getId());
        dtoIn.setProductVersionId(productVersion.getId());
        
        dtoIn.setRegionId(regionId);

        service.launchTrial(getRequestStub(), dtoIn);

        assertEquals("Send Launch and warning.", 5, testEmailProvider.getAsyncEmails().size());


        for (int count=0; count<testEmailProvider.getAsyncEmails().size();count++) {

        	logger.info("EMAIL SUBJECT["+count+"]:"+testEmailProvider.getAsyncEmails().get(count).subject);

        }
  
        assertTrue(testEmailProvider.asyncEmailsContainSubject(messageProvider.getMessage(StringDefs.MESSAGE_NO_TRIALS_SUBJECT, "WS:WS-BC-3")));
        assertTrue(testEmailProvider.asyncEmailsContainSubject(messageProvider.getMessage(StringDefs.MESSAGE_NO_TRIALS_SUBJECT, "EAM:EAM-BC-3")));
        assertTrue(testEmailProvider.asyncEmailsContainSubject(messageProvider.getMessage(StringDefs.MESSAGE_NO_TRIALS_SUBJECT, "XM:XM-BC-3")));
        assertTrue(testEmailProvider.asyncEmailsContainSubject(messageProvider.getMessage(StringDefs.MESSAGE_LOW_TRIALS_SUBJECT, "Syteline:Syteline-BC-3")));
        assertTrue(testEmailProvider.asyncEmailsContainSubject(messageProvider.getMessage(StringDefs.MESSAGE_TRIAL_SUBJECT)));
        
        emailInfo = testEmailProvider.getAsyncEmails().get(0);
        assertEquals(messageProvider.getMessage(StringDefs.MESSAGE_NO_TRIALS_SUBJECT, "WS:WS-BC-3"),
                emailInfo.subject);
        
        emailInfo = testEmailProvider.getAsyncEmails().get(1);
        assertEquals(messageProvider.getMessage(StringDefs.MESSAGE_NO_TRIALS_SUBJECT, "EAM:EAM-BC-3"),
                emailInfo.subject);
        emailInfo = testEmailProvider.getAsyncEmails().get(2);
        assertEquals(messageProvider.getMessage(StringDefs.MESSAGE_NO_TRIALS_SUBJECT, "XM:XM-BC-3"),
                emailInfo.subject);
        emailInfo = testEmailProvider.getAsyncEmails().get(3);
        assertEquals(messageProvider.getMessage(StringDefs.MESSAGE_LOW_TRIALS_SUBJECT, "Syteline:Syteline-BC-3"),
                emailInfo.subject);
        emailInfo = testEmailProvider.getAsyncEmails().get(4);
        assertEquals(messageProvider.getMessage(StringDefs.MESSAGE_TRIAL_SUBJECT),
                emailInfo.subject);

        ProductVersion eamVersion=productVersionDao.findByProductAndName(productDao.findByShortName("EAM"), "EAM-BC-3");
        ProductVersion xmVersion=productVersionDao.findByProductAndName(productDao.findByShortName("XM"),"XM-BC-3");
        ProductVersion sytelineVersion = productVersionDao.findByProductAndName(productDao.findByShortName("Syteline"),"Syteline-BC-3");
        
        assertEquals(Long.valueOf(0L), trialEnvironmentDao.countByProductVersionAndRegionAndAvailable(productVersion, region, true));
        assertEquals(Long.valueOf(0L), trialEnvironmentDao.countByProductVersionAndRegionAndAvailable(eamVersion, region, true));
        assertEquals(Long.valueOf(0L), trialEnvironmentDao.countByProductVersionAndRegionAndAvailable(xmVersion, region, true));
        assertEquals(Long.valueOf(1L), trialEnvironmentDao.countByProductVersionAndRegionAndAvailable(sytelineVersion, region, true));

        settingsProvider.setProductionMode(currMode);
        settingsProvider.setTrialRecycleInforDomain(currSetting);

    }

    @Test
    @Transactional(propagation = Propagation.REQUIRED)
    public void testLaunchTrialRecycle() throws Exception {

        boolean currMode = settingsProvider.isProductionMode();
        boolean recycleInfor = settingsProvider.isTrialRecycleInforDomain();
        settingsProvider.setTrialRecycleInforDomain(true);
        settingsProvider.setProductionMode(true);

        //create second user.
        User user = new User();
        final String testUser2Name = "test2@infor.com";
        user.setUsername(testUser2Name);
        user.setActive(true);
        Date date = new Date();
        user.setCreatedAt(date);
        user.setPassword(securityService.encodePassword("test2", date));
        user.getRoles().add(Role.ROLE_EXTERNAL);
        userDao.save(user);

        //create user with another domain.
        user = new User();
        final String testorUsername = "testor@gmail.com";
        user.setUsername(testorUsername);
        user.setActive(true);
        date = new Date();
        user.setCreatedAt(date);
        user.setPassword(securityService.encodePassword("test", date));
        user.getRoles().add(Role.ROLE_EXTERNAL);
        userDao.save(user);

        //create first trial
        loginTestUser();
        Product product = productDao.findByShortName("EAM");
        ProductVersion productVersion = productVersionDao.findByProductAndName(product, "EAM-BC-3");
        TrialDto dtoIn = new TrialDto();
        dtoIn.setProductId(product.getId());
        dtoIn.setProductVersionId(productVersion.getId());
        
        final Long regionId = 2L;
        dtoIn.setRegionId(regionId);
        NullEmailProvider testEmailProvider = new NullEmailProvider();
        trialEmailComponent.setEmailProvider(testEmailProvider);
        activateTrial(testUserName, product);
        TrialDto dto = service.launchTrial(getRequestStub(), dtoIn);
        assertEquals(1, testEmailProvider.getAsyncEmails().size());
        assertNotNull(dto);
        assertNotNull(dto.getExpirationDate());
        assertNotNull(dto.getId());
        assertNotNull(dto.getProductId());
        assertEquals(product.getId(), dto.getProductId());
        assertEquals(productVersion.getId(),dto.getProductVersionId());
        assertEquals(regionId, dto.getRegionId());
        assertNotNull(dto.getEnvironmentId());
        assertNotNull(dto.getUrl());
        String currUrl = dto.getUrl();
        assertNotNull(dto.getUsername());
        assertEquals("R5", dto.getUsername());
        assertNotNull(dto.getPassword());
        assertEquals("Infor273", dto.getPassword());
        Date oldExpDate = dto.getExpirationDate();
        assertEquals(2, trialInstanceDao.count());
        final TrialInstance eam = trialInstanceDao.findByProductVersion_Product_ShortNameAndTypeAndDomain("EAM", TrialInstanceType.DOMAIN, "infor.com");
        assertEquals(dto.getUrl(), eam.getUrl());

        //create trial for a different product
        product = productDao.findByShortName("XM");
        productVersion=productVersionDao.findByProductAndName(product,"XM-BC-3");
        dtoIn = new TrialDto();
        dtoIn.setProductId(product.getId());
        dtoIn.setProductVersionId(productVersion.getId());
        
        dtoIn.setRegionId(regionId);
        testEmailProvider = new NullEmailProvider();
        trialEmailComponent.setEmailProvider(testEmailProvider);
        activateTrial(testUserName, product);
        TrialDto newDto = service.launchTrial(getRequestStub(), dtoIn);
        assertEquals(1, testEmailProvider.getAsyncEmails().size());
        assertNotNull(newDto);
        assertEquals(regionId, newDto.getRegionId());
        assertNotNull(newDto.getEnvironmentId());
        assertFalse(currUrl.equals(newDto.getUrl()));
        assertEquals(4, trialInstanceDao.count());
        final TrialInstance xm = trialInstanceDao.findByProductVersion_Product_ShortNameAndTypeAndDomain("XM", TrialInstanceType.DOMAIN, "infor.com");
        assertEquals(newDto.getUrl(), xm.getUrl());

        login(testUser2Name, "test2");

        //create trial for another infor user
        product = productDao.findByShortName("EAM");
        productVersion=productVersionDao.findByProductAndName(product,"EAM-BC-3");
        dtoIn = new TrialDto();
        dtoIn.setProductId(product.getId());
        dtoIn.setProductVersionId(productVersion.getId());
        dtoIn.setRegionId(regionId);
        testEmailProvider = new NullEmailProvider();
        trialEmailComponent.setEmailProvider(testEmailProvider);
        activateTrial(testUser2Name, product);
        newDto = service.launchTrial(getRequestStub(), dtoIn);
        assertEquals(1, testEmailProvider.getAsyncEmails().size());
        assertNotNull(newDto);
        assertNotNull(newDto.getExpirationDate());
        assertEquals(newDto.getExpirationDate(), oldExpDate);
        assertNotNull(newDto.getId());
        assertFalse(dto.getId().equals(newDto.getId()));
        assertNotNull(newDto.getProductId());
        assertEquals(dto.getProductId(), newDto.getProductId());
        assertEquals(dto.getProductVersionId(),newDto.getProductVersionId());
        assertEquals(productVersion.getId(),newDto.getProductVersionId());
        assertEquals(dto.getRegionId(), newDto.getRegionId());
        assertNotNull(newDto.getEnvironmentId());
        assertNotNull(newDto.getUrl());
        assertEquals(dto.getUrl(), newDto.getUrl());
        assertEquals(dto.getProductId(), newDto.getProductId());
        assertNotNull(dto.getUsername(), newDto.getUsername());
        assertEquals(dto.getUsername(), newDto.getUsername());
        assertNotNull(newDto.getPassword());
        assertEquals(dto.getPassword(), newDto.getPassword());
        assertEquals(5, trialInstanceDao.count());

        //Launch trial for a different domain
        //gmail does not recycle trials.

        login(testorUsername, "test");
        dtoIn = new TrialDto();
        dtoIn.setProductId(product.getId());
        dtoIn.setProductVersionId(productVersion.getId());
        dtoIn.setRegionId(regionId);
        testEmailProvider = new NullEmailProvider();
        trialEmailComponent.setEmailProvider(testEmailProvider);
        activateTrial(testorUsername, product);
        newDto = service.launchTrial(getRequestStub(), dtoIn);
        assertEquals(1, testEmailProvider.getAsyncEmails().size());
        assertNotNull(newDto);
        assertNotNull(newDto.getExpirationDate());
        assertEquals(newDto.getExpirationDate(), oldExpDate);
        assertNotNull(newDto.getId());
        assertFalse(dto.getId().equals(newDto.getId()));
        assertNotNull(newDto.getProductId());
        assertEquals(dto.getProductId(), newDto.getProductId());
        assertEquals(productVersion.getId(),newDto.getProductVersionId());
        assertEquals(dto.getRegionId(), newDto.getRegionId());
        assertNotNull(newDto.getUrl());
        assertFalse(dto.getUrl().equals(newDto.getUrl()));
        assertNotNull(dto.getUsername(), newDto.getUsername());
        assertNotNull(newDto.getPassword());
        assertEquals(6, trialInstanceDao.count());

        settingsProvider.setProductionMode(currMode);
        settingsProvider.setTrialRecycleInforDomain(recycleInfor);
    }

    @Test
    public void testGetSettings() throws Exception {

        loginTestUser();
        User user = userDao.findOne(securityService.getCurrentUser().getId());
        RegistrationCompleteDto userSettings = service.getUserSettings();
        assertNotNull(userSettings);
        assertEquals(user.getLanguage(), userSettings.getLanguage());
        assertEquals(user.getCompany().getName(), userSettings.getCompanyName());
        assertNull(userSettings.getPassword());
        assertNull(userSettings.getPassword2());
        /* 
        assertEquals(user.getAwsAccountNumber(), userSettings.getAwsAccountNumber());
        assertEquals(user.getAwsKey(), userSettings.getAwsKey());
        assertEquals(user.getAwsSecretKey(), userSettings.getAwsSecretKey());
         */
        loginAdminUser();
        userSettings = service.getUserSettings();
        assertNotNull(userSettings);

        User bdrUser = createUser("bdradmin@infor.com", "BdrAdmin", "User", "bdradmin", Role.ROLE_SALES);
        bdrUser.setAddress1("PeepThis");
        userDao.save(bdrUser);
        login("bdradmin@infor.com", "bdradmin");
        userSettings = service.getUserSettings();
        assertNotNull(userSettings);
        assertEquals("PeepThis", userSettings.getAddress1());

    }

    @Test
    public void testUpdateSettings() throws Exception {

        HttpServletResponse response = new MockHttpServletResponse();

        loginTestUser();
        RegistrationCompleteDto settingsUpdate = new RegistrationCompleteDto();
        settingsUpdate.setLanguage("en_US");
        settingsUpdate.setCompanyName("newCompanyName");
        settingsUpdate.setAddress1("newAddr1");
        settingsUpdate.setAddress2("newAddr2");
        settingsUpdate.setPhone("555-867-5309");
        settingsUpdate.setCountry("Brasil");
        settingsUpdate.setInforId("BOB");
        settingsUpdate.setIndustryId(industryDao.findByName("Other").getId());
        final RegistrationCompleteDto userSettings = service.updateUserSettings(response, settingsUpdate);
        emService.flush();
        assertNotNull(userSettings);
        assertEquals(settingsUpdate.getLanguage(), userSettings.getLanguage());
        assertEquals(settingsUpdate.getCompanyName(), userSettings.getCompanyName());
        assertNull(userSettings.getPassword());
        assertNull(userSettings.getPassword2());
        assertEquals(settingsUpdate.getAddress1(), userSettings.getAddress1());
        assertEquals(settingsUpdate.getAddress2(), userSettings.getAddress2());
        assertEquals(settingsUpdate.getPhone(), userSettings.getPhone());
        assertEquals(settingsUpdate.getCountry(), userSettings.getCountry());
        assertEquals(settingsUpdate.getInforId(), userSettings.getInforId());

        User user = userDao.findOne(securityService.getCurrentUser().getId());
        assertEquals(userSettings.getLanguage(), user.getLanguage());
        assertEquals(userSettings.getCompanyName(), user.getCompany().getName());
        assertNull(userSettings.getPassword());
        assertNull(userSettings.getPassword2());
        assertEquals(userSettings.getAddress1(), user.getAddress1());
        assertEquals(userSettings.getAddress2(), user.getAddress2());
        assertEquals(userSettings.getCountry(), user.getCountry());
        assertEquals(Boolean.FALSE, user.getInforCustomer());
        /*
        assertEquals(userSettings.getAwsAccountNumber(), user.getAwsAccountNumber());
        assertEquals(userSettings.getAwsKey(), user.getAwsKey());
        assertEquals(userSettings.getAwsSecretKey(), user.getAwsSecretKey());
         */
        User bdrUser = createUser("bdradmin@infor.com", "BdrAdmin", "User", "bdradmin", Role.ROLE_SALES);
        bdrUser.setAddress1("PeepThis");
        userDao.save(bdrUser);
        login("bdradmin@infor.com", "bdradmin");
        final RegistrationCompleteDto bdrBeforeSettings = service.getUserSettings();
        assertNotNull(bdrBeforeSettings);
        assertEquals("PeepThis", bdrBeforeSettings.getAddress1());
        bdrBeforeSettings.setCompanyName("MyCompany");
        bdrBeforeSettings.setCompanyId(-1L);
        bdrBeforeSettings.setCountry("USA");
        bdrBeforeSettings.setLanguage("en_US");
        bdrBeforeSettings.setPhone("000-000-0000");
        bdrBeforeSettings.setIndustryId(industryDao.findByName("Other").getId());
        final RegistrationCompleteDto dto = service.updateUserSettings(response, bdrBeforeSettings);
        assertEquals("MyCompany", dto.getCompanyName());
    }

    @Test
    public void testUpdateSettingsPassword() throws Exception {

        HttpServletResponse response = new MockHttpServletResponse();
        User testUser = userDao.findByUsername(testUserName);
        //Set unset fields.
        testUser.setLanguage("en_US");
        testUser.setCountry("USA");
        testUser.setAddress1("Address1");
        testUser.setPhone("000-000-0000");

        loginTestUser();
        RegistrationCompleteDto settingsUpdate = service.getUserSettings();
        settingsUpdate.setPassword("newPassword");
        settingsUpdate.setPassword2("newPassword");
        final RegistrationCompleteDto userSettings = service.updateUserSettings(response, settingsUpdate);
        emService.flush();
        assertEquals("*UP*", userSettings.getPassword());
        assertEquals("*UP*", userSettings.getPassword2());
        login(testUserName, "newPassword");

    }

    class TestGuidProvider extends GuidProvider {
        private String currGuid = "current";

        @Override
        public String generateGuid() {
            return currGuid;
        }

        public void setCurrGuid(String currGuid) {
            this.currGuid = currGuid;
        }
    }


    @Test
    @Transactional(propagation = Propagation.REQUIRED)
    public void testAdd_Update_Delete_AmazonCredentials() throws Exception {
        final String username = "user@user.com";
        final String password = "user";

        final String awsKey = "shinyAmazonKey";
        final String awsSecKey = "*************************";
        final String name = "My shiny amazon key";
        final String newAwsSecKey = "1234554321";
        final String newAwsKey = "fixedAmazonKey";
        final String newName = "My shinier amazon key";

        User user = createUser(username, "User", "Test", password, Role.ROLE_EXTERNAL);
        userDao.save(user);
        emService.flush();

        login(username, password);
        //ADD
        AmazonCredentialsDto newCred = new AmazonCredentialsDto(null, user.getId(), name, awsKey, awsSecKey);
        AmazonCredentialsDto createCred = service.updateAmazonCredentials(newCred);
        assertTrue("only thing different should be ID", amazonCredentialsAreEqualIgnoreId(newCred, createCred));

        //UPDATE
        AmazonCredentialsDto update = new AmazonCredentialsDto(createCred.getId(), user.getId(), newName, newAwsKey, newAwsSecKey);
        AmazonCredentialsDto back = service.updateAmazonCredentials(update);
        assertFalse("everything but ids should be different", amazonCredentialsAreEqualIgnoreId(createCred, back));

        assertEquals("name check", newName, back.getName());
        assertEquals("awsKey check", newAwsKey, back.getAwsKey());
        assertEquals("awsSecKey check", newAwsSecKey, back.getSecretKey());
        assertEquals("user id check", user.getId(), back.getUserId());
        assertEquals("check same id", update.getId(), back.getId());

        //DELETE
        assertEquals(1, amazonCredentialsDao.getByUser(user).size());
        AmazonCredentialsDto deleteDto = new AmazonCredentialsDto();
        deleteDto.setId(back.getId());
        service.deleteAmazonCredentials(deleteDto);
        assertTrue(amazonCredentialsDao.getByUser(user).isEmpty());
    }


    @Test
    @Transactional(propagation = Propagation.REQUIRED)
    public void testGetAmazonCredentials() throws Exception {
        final String username = "user@user.com";
        final String password = "user";

        User user = createUser(username, "User", "Test", password, Role.ROLE_EXTERNAL);
        userDao.save(user);
        emService.flush();

        AmazonCredentials amCred = new AmazonCredentials();
        amCred.setUser(user);
        amCred.setAwsKey("shinyAmazonKey");
        amCred.setSecretKey("********************");
        amCred.setName("My Shiny Amazon Key");
        amazonCredentialsDao.save(amCred);
        amazonCredentialsDao.flush();

        login(username, password);

        List<AmazonCredentialsDto> myCreds = service.getAmazonCredentials();
        assertEquals(1, myCreds.size());
        AmazonCredentialsDto myDto = myCreds.get(0);
        assertEquals("User id check", user.getId(), myDto.getUserId());
        assertEquals("awsKey check", "shinyAmazonKey", myDto.getAwsKey());
        assertEquals("awsSecretKey check", "********************", myDto.getSecretKey());
        assertEquals("name check", "My Shiny Amazon Key", myDto.getName());

    }

    private boolean amazonCredentialsAreEqualIgnoreId(AmazonCredentialsDto one, AmazonCredentialsDto two) {
        if (!one.getName().equals(two.getName())) {
            return false;
        } else if (!one.getAwsKey().equals(two.getAwsKey())) {
            return false;
        } else if (!one.getSecretKey().equals(two.getSecretKey())) {
            return false;
        } else if (!one.getUserId().equals(two.getUserId())) {
            return false;
        }
        return true;
    }

    @Test
    public void testGetTrialsAndDeployments() throws Exception {

        importExportComponent.importFromJsonFile("useradminservicetest.json", ImportFileTypeEnum.IN_ARCHIVE, null);
        String username="asalesuser@infor.com";
        String password="useruser";

        User user=userDao.findByUsername(username);
        Product product=productDao.findByShortName("EAM");
        ProductVersion productVersion=productVersionDao.findByProductAndName(product,"EAM-BC-3");
        Region region=regionDao.findById(1L);

        activateTrial(username,product);
        activateDeploy(username,product);

        login(username,password);

        trialService.launchTrial(getRequestStub(), productVersion, user, region, new Locale("en_US"));
        List<AmazonCredentialsDto> credentials=service.getAmazonCredentials();
        assertTrue(credentials.size()>0);


        DeployRequestDto deployRequestDto=new DeployRequestDto();
        deployRequestDto.setAmazonCredentialsId(credentials.get(0).getId());
        deployRequestDto.setRegionId(region.getId());
        deployRequestDto.getProductIds().add(new Long[]{product.getId(),product.getProductVersions().get(0).getId()});
        deploymentService.deployMultipleProducts(request, deployRequestDto);

        List<DeploymentStackDto> dtos=service.getDeployments();

        assertEquals(2,dtos.size());
        int trialCount=0;
        int deployCount=0;
        for (DeploymentStackDto dto : dtos) {
        	 assertEquals(user.getId(),dto.getUser().getId());
        	 if (dto.getDeploymentType()==DeploymentType.INFOR24) {
        		 trialCount++;
        	 } else if (dto.getDeploymentType()==DeploymentType.AWS){
        		 deployCount++;
        	 }
        }
        assertEquals(1,deployCount);
        assertEquals(1,trialCount);
       

        loginAdminUser();
        List<DeploymentStackDto> asAdmin=userAdminService.getDeployments(user.getId());
        assertEquals(2,asAdmin.size());
        deployCount=0;
        trialCount=0;
        for (DeploymentStackDto dto : dtos) {
       	 assertEquals(user.getId(),dto.getUser().getId());
       	 if (dto.getDeploymentType()==DeploymentType.INFOR24) {
       		 trialCount++;
       	 } else if (dto.getDeploymentType()==DeploymentType.AWS){
       		 deployCount++;
       	 }
        }
       
    }

    @Test
    public void testImports() throws Exception {
        List<User> users=userDao.findAll();
        for (User user : users) {
            assertNotNull(user.getCompany());
            assertNotNull(user.getCompany().getIndustry());
        }
    }

    @Test
    public void testGetDeployments() {
        long schedCount = scheduleDao.count();

        login("sales@infor.com","useruser");

        User user=userDao.findByUsername("sales@infor.com");
        assertNotNull("User", user);

        AmazonCredentials fake=createAmazonCredentials(user);

        assertNotNull("Fake", fake);

        Product product=productDao.findByShortName("EAM");
        Region region=regionDao.findById(1L);

        DeployRequestDto deployRequestDto=new DeployRequestDto();
        deployRequestDto.setAmazonCredentialsId(fake.getId());//credentials.get(0).getId());
        deployRequestDto.setRegionId(region.getId());
        deployRequestDto.getProductIds().add(new Long[]{product.getId(),product.getProductVersions().get(0).getId()});
        deployRequestDto.setScheduleType(DeployActionScheduleType.HOURLY);
        deployRequestDto.setScheduleValue("12");
        deploymentService.deployMultipleProducts(request, deployRequestDto);

        logger.info("Preparing to deploy test deployment");

        List<DeploymentStackDto> dtoList = service.getDeployments();

        for(DeploymentStackDto dto : dtoList) {
            assertTrue("Users equal", dto.getCreatedByUser().getId().equals(user.getId()));
            assertNotNull("VPCid", dto.getVpcId());
            assertNotNull("ElasticIp", dto.getElasticIp());
            assertNotNull("ScheduleId", dto.getScheduleId());

            //Should have created a stop schedule when the stack was created above.
            List<Schedule> stopSchedule = 
                    scheduleDao.findByEntityIdAndTypeAndStatus( 
                            String.valueOf(dto.getId()), 
                            ScheduleType.STOP_STACK, 
                            ScheduleStatus.QUEUED);
            Date scheduledStopAt = null;
            if(stopSchedule != null) {
                scheduledStopAt = stopSchedule.get(0).getScheduledAt();
            }

            assertTrue("ScheduleStopAt", dto.getScheduledStopAt().equals(scheduledStopAt));
        }

      //stop schedule and notification schedule
        assertEquals("Schedule Count", (schedCount+2),scheduleDao.count());
    }

    @Test
    public void testGetDeploymentsWithNoScheduledStop() {
        login("sales@infor.com","useruser");

        User user=userDao.findByUsername("sales@infor.com");
        assertNotNull("User Not Null", user);

        AmazonCredentials fake=createAmazonCredentials(user);

        assertNotNull("Credentials Not Null", fake);

        Product product=productDao.findByShortName("EAM");
        Region region=regionDao.findById(1L);

        DeployRequestDto deployRequestDto=new DeployRequestDto();
        deployRequestDto.setAmazonCredentialsId(fake.getId());//credentials.get(0).getId());
        deployRequestDto.setRegionId(region.getId());
        deployRequestDto.getProductIds().add(new Long[]{product.getId(),product.getProductVersions().get(0).getId()});
        deployRequestDto.setScheduleType(DeployActionScheduleType.INDEFINITE);
        deployRequestDto.setScheduleValue("12");
        deploymentService.deployMultipleProducts(request, deployRequestDto);

        logger.info("Preparing to deploy test deployment");

        List<DeploymentStackDto> dtoList = service.getDeployments();

        for(DeploymentStackDto dto : dtoList) {
            assertTrue("UserIds Match", dto.getCreatedByUser().getId().equals(user.getId()));
            assertNotNull("VpcId is not null", dto.getVpcId());
            assertNotNull("ElasticIp is not null", dto.getElasticIp());
            assertNull("Get Stop at null", dto.getScheduledStopAt());
        }

        assertEquals("Schedule Count", 0,scheduleDao.count());
    }   

    private AmazonCredentials createAmazonCredentials(User user) {
        AmazonCredentials fake=new AmazonCredentials();
        fake.setAwsKey("FakeAwsKey");
        fake.setSecretKey("psst-fake_secret");
        fake.setName("Fake key");
        fake.setUser(user);
        amazonCredentialsDao.save(fake);
        amazonCredentialsDao.flush();
        assertNotNull(fake);
        return fake;
    }

}