package com.infor.cloudsuite.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.transaction.annotation.Transactional;

import com.infor.cloudsuite.AbstractTest;
import com.infor.cloudsuite.dao.AmazonCredentialsDao;
import com.infor.cloudsuite.dao.AmiDescriptorDao;
import com.infor.cloudsuite.dao.DeploymentStackDao;
import com.infor.cloudsuite.dao.ProductDao;
import com.infor.cloudsuite.dao.ProductVersionDao;
import com.infor.cloudsuite.dao.RegionDao;
import com.infor.cloudsuite.dao.ScheduleDao;
import com.infor.cloudsuite.dao.UserDao;
import com.infor.cloudsuite.dto.*;
import com.infor.cloudsuite.entity.*;
import com.infor.cloudsuite.platform.amazon.AwsOperations;
import com.infor.cloudsuite.platform.amazon.CreateStackRequest;
import com.infor.cloudsuite.platform.amazon.DeploymentStackListener;
import com.infor.cloudsuite.platform.amazon.DummyAwsOperations;
import com.infor.cloudsuite.platform.amazon.DummyDeployStackListener;
import com.infor.cloudsuite.platform.amazon.DummyFailPoint;
import com.infor.cloudsuite.platform.amazon.DummyFailPointException;
import com.infor.cloudsuite.platform.amazon.StackBuilder;
import com.infor.cloudsuite.platform.components.EmailProvider;
import com.infor.cloudsuite.platform.components.NullEmailProvider;
import com.infor.cloudsuite.platform.security.SecurityService;
import com.infor.cloudsuite.platform.security.SecurityUser;
import com.infor.cloudsuite.service.component.DeploymentServiceComponent;
import com.infor.cloudsuite.service.component.ImportExportComponent;
import com.infor.cloudsuite.service.component.ImportFileTypeEnum;
import com.infor.cloudsuite.service.component.TrialEmailComponent;
import com.infor.cloudsuite.task.ScheduleDispatch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * User: bcrow
 * Date: 11/11/11 8:58 AM
 */
@Transactional
public class DeploymentServiceTest extends AbstractTest {

    final Logger logger = LoggerFactory.getLogger(DeploymentServiceTest.class);
    @Resource
    private ImportExportComponent importExportComponent;
    @Resource
    private DeploymentService deploymentService;
    @Resource
    private DeploymentServiceComponent deploymentServiceComponent;
    @Resource
    private UserDao userDao;
    @Resource
    private ProductDao productDao;

    @Resource
    private AmazonCredentialsDao amazonCredentialsDao;
    @Resource
    private DeploymentStackListener deploymentStackListener;
    @Resource
    private AmiDescriptorDao amiDescriptorDao;
    @Resource
    private StackBuilder stackBuilder;
    @Resource
    private DummyAwsOperations dummyAwsOperations;
    @Resource
    private SecurityService securityService;
    @Resource
    private ScheduleDao scheduleDao;
  
    @Resource
    private DeploymentStackDao deploymentStackDao;
    
    private HttpServletRequest request;
    @Resource
    private RegionDao regionDao;
    @Resource
    private TrialEmailComponent trialEmailComponent;
    @Resource
    private TrialService trailService;
    @Resource
    private ProductVersionDao productVersionDao;
    
    private NullEmailProvider nullEmailProvider=new NullEmailProvider();
    private EmailProvider currEmailProvider;
    
   
    @Before
    public void setUp() {
    	this.currEmailProvider=deploymentService.getStackBuilder().getEmailProvider();
    	deploymentService.getStackBuilder().setEmailProvider(nullEmailProvider);
    	HttpSession session = new MockHttpSession();
        request = getRequestStub(session);
    }
    
    @After
    public void after() {
    	super.after();
    	deploymentService.getStackBuilder().setEmailProvider(this.currEmailProvider);
    	nullEmailProvider.getAsyncEmails().clear();
    	nullEmailProvider.getEmails().clear();
    }

    private List<Long[]> productIdsSmallForTest() {
        Product eam = productDao.findByShortName("EAM");
        Product xm = productDao.findByShortName("XM");
        Product ln = productDao.findByShortName("ERP Enterprise");
        ArrayList<Long[]> toret = new ArrayList<>();
        toret.add(new Long[]{eam.getId(),eam.getProductVersions().get(0).getId()});
        toret.add(new Long[]{xm.getId(),xm.getProductVersions().get(0).getId()});
        toret.add(new Long[]{ln.getId(),ln.getProductVersions().get(0).getId()});
        return toret;
    }

    private List<Long[]> productIdsForTest() {
        ArrayList<Long[]> productIds = new ArrayList<>();

        for (Product product : productDao.findAll()) {
            productIds.add(new Long[]{product.getId(),product.getProductVersions().get(0).getId()});
        }
        return productIds;

    }

    @Test
    public void testGetDeploymentStackInfo() throws Exception {
        importExportComponent.importFromJsonFile("useradminservicetest.json", ImportFileTypeEnum.IN_ARCHIVE, null);
        assertEquals(2, amazonCredentialsDao.count());
        User auser = userDao.findByUsername("auser@infor.com");
        User asalesuser = userDao.findByUsername("asalesuser@infor.com");
        assertNotNull(auser);
        assertNotNull(asalesuser);

        login("asalesuser@infor.com", "useruser");
        DeployRequestDto drd = new DeployRequestDto();
        AmazonCredentials auserCred = amazonCredentialsDao.findByUserAndName(auser, "Dummy Key");
        AmazonCredentials asalesuserCred = amazonCredentialsDao.findByUserAndName(asalesuser, "Dummy Key");
        assertNotNull(auserCred);
        assertNotNull(asalesuserCred);

        drd.setAmazonCredentialsId(asalesuserCred.getId());
        drd.setDeploymentName("asales user deployment");
        drd.setProductIds(this.productIdsSmallForTest());
        drd.setUserId(asalesuser.getId());

        DeploymentStackDto dsd = deploymentService.deployMultipleProducts(request, drd);
        assertNotNull(dsd);

        drd = new DeployRequestDto();
        drd.setAmazonCredentialsId(auserCred.getId());
        drd.setUserId(auser.getId());
        drd.setDeploymentName("auser's deployment");
        drd.setProductIds(this.productIdsForTest());

        dsd = deploymentService.deployMultipleProducts(request,drd);
        assertNotNull(dsd);

        loginAdminUser();
        DeploymentStackInfoDto dsid = deploymentService.getAllDeploymentsSummary();
        assertNotNull(dsid);
        assertEquals(1, dsid.getCreatedByCountMap().size());
        assertEquals(2, dsid.getCountMap().size());
        assertEquals((Long) 2L, dsid.getTotalCount());
        assertEquals((Integer) 1, dsid.getUniqueCreatedByUserCount());
        assertEquals((Integer) 2, dsid.getUniqueUserCount());

        assertEquals((Long) 2L, dsid.getCreatedByCountMap().get(asalesuser.getId()));


    }

    @Test
    public void testMultiProductDeployment() throws Exception {
        //import extra users including sales
        importExportComponent.importFromJsonFile("useradminservicetest.json", ImportFileTypeEnum.IN_ARCHIVE, null);
        assertEquals(2, amazonCredentialsDao.count());

        login("asalesuser@infor.com", "useruser");

        DeployRequestDto deployRequestDto = new DeployRequestDto();
        deployRequestDto.setAmazonCredentialsId(amazonCredentialsDao.findByUserAndName(userDao.findByUsername("asalesuser@infor.com"), "Dummy Key").getId());
        deployRequestDto.setRegionId(5L);

        List<Long[]> prodIds = productIdsForTest();
        deployRequestDto.setProductIds(prodIds);
        DeploymentStackDto dto = deploymentService.deployMultipleProducts(request,deployRequestDto);
        assertNotNull(dto);

        assertEquals(deployRequestDto.getRegionId(),dto.getRegionId());
        assertEquals(deployRequestDto.getAmazonCredentialsId(), dto.getAmazonCredentialsId());
        assertTrue(dto.getVpcId().contains("VPC"));
        assertNotNull(dto.getId());
        assertEquals(prodIds.size(), dto.getDeployedProductVersions().size());
        assertEquals(deployRequestDto.getRegionId(), dto.getRegionId());
    }


    @Test
    public void testStartDoAction() throws Exception {
        //import extra users
        importExportComponent.importFromJsonFile("useradminservicetest.json", ImportFileTypeEnum.IN_ARCHIVE, null);

        //create deployment
        login("asalesuser@infor.com", "useruser");
        DeployRequestDto deployRequestDto = new DeployRequestDto();
        deployRequestDto.setAmazonCredentialsId(amazonCredentialsDao.findByUserAndName(userDao.findByUsername("asalesuser@infor.com"), "Dummy Key").getId());
        deployRequestDto.setRegionId(4L);
        deployRequestDto.setScheduleType(DeployActionScheduleType.HOURLY);
        deployRequestDto.setScheduleValue("12");

        List<Long[]> prodIds = productIdsForTest();
        deployRequestDto.setProductIds(prodIds);
        DeploymentStackDto dto = deploymentService.deployMultipleProducts(request,deployRequestDto);
        assertNotNull(dto);

        //run through commands.

        DeployActionDto dad = new DeployActionDto();
        dad.setVpcId(dto.getVpcId());
        dad.setType(DeployActionType.START);
        dad.setAsync(false);
        dto = deploymentService.doAction(request,dad);
        assertEquals(DeploymentState.NOT_AVAILABLE.toString(), dto.getDeploymentState());
        assertEquals(DeploymentStatus.STARTING.toString(), dto.getDeploymentStatus());
        assertNotNull(dto.getScheduledStopAt());

        List<Schedule> schedules = scheduleDao.findByEntityIdAndTypeAndStatus(String.valueOf(dto.getId()), ScheduleType.STOP_STACK, ScheduleStatus.QUEUED);
        assertEquals(1, schedules.size());
    }

    @Test
    public void testRestartDoAction() throws Exception {
        //import extra users
        importExportComponent.importFromJsonFile("useradminservicetest.json", ImportFileTypeEnum.IN_ARCHIVE, null);

        //create deployment
        login("asalesuser@infor.com", "useruser");
        DeployRequestDto deployRequestDto = new DeployRequestDto();
        deployRequestDto.setAmazonCredentialsId(amazonCredentialsDao.findByUserAndName(userDao.findByUsername("asalesuser@infor.com"), "Dummy Key").getId());
        deployRequestDto.setRegionId(4L);
        deployRequestDto.setScheduleType(DeployActionScheduleType.HOURLY);
        deployRequestDto.setScheduleValue("12");


        deployRequestDto.setProductIds(productIdsForTest());
        DeploymentStackDto dto = deploymentService.deployMultipleProducts(request,deployRequestDto);
        assertNotNull(dto);

        //run through commands.

        DeployActionDto dad = new DeployActionDto();
        dad.setVpcId(dto.getVpcId());
        dad.setType(DeployActionType.RESTART);
        dad.setAsync(false);
        dto = deploymentService.doAction(request,dad);
        assertEquals(DeploymentState.NOT_AVAILABLE.toString(), dto.getDeploymentState());
        assertEquals(DeploymentStatus.DEPLOYED.toString(), dto.getDeploymentStatus());
        assertEquals(deployRequestDto.getRegionId(),dto.getRegionId());
        //Shoud be null unless started prior to restart
        //assertNotNull(dto.getScheduledStopAt());

        //todo Does not test anything - fix or remove.
        List<Schedule> schedules = scheduleDao.findByEntityIdAndTypeAndStatus(String.valueOf(dto.getId()), ScheduleType.STOP_STACK, ScheduleStatus.QUEUED);
        // no schedule without being started first.
        //assertEquals(1, schedules.size());
    }

    @Test
    public void testStopDoAction() throws Exception {
        //import extra users
        importExportComponent.importFromJsonFile("useradminservicetest.json", ImportFileTypeEnum.IN_ARCHIVE, null);

        //create deployment
        login("asalesuser@infor.com", "useruser");
        DeployRequestDto deployRequestDto = new DeployRequestDto();
        deployRequestDto.setAmazonCredentialsId(amazonCredentialsDao.findByUserAndName(userDao.findByUsername("asalesuser@infor.com"), "Dummy Key").getId());
        deployRequestDto.setRegionId(4L);
        deployRequestDto.setScheduleType(DeployActionScheduleType.HOURLY);
        deployRequestDto.setScheduleValue("12");

        deployRequestDto.setProductIds(productIdsForTest());
        DeploymentStackDto dto = deploymentService.deployMultipleProducts(request,deployRequestDto);
        assertNotNull(dto);

        //run through commands.

        DeployActionDto dad = new DeployActionDto();
        dad.setVpcId(dto.getVpcId());
        dad.setType(DeployActionType.STOP);
        dad.setAsync(false);
        dto = deploymentService.doAction(request,dad);
        assertEquals(DeploymentState.NOT_AVAILABLE.toString(), dto.getDeploymentState());
        assertEquals(DeploymentStatus.DEPLOYED.toString(), dto.getDeploymentStatus());
        assertTrue(dto.getScheduledStopAt() == null);

        List<Schedule> schedules = scheduleDao.findByEntityIdAndTypeAndStatus(String.valueOf(dto.getId()), ScheduleType.STOP_STACK, ScheduleStatus.CANCELED);
        assertEquals(1, schedules.size());
    }

    @Test
    public void testTerminateDoAction() throws Exception {
        //import extra users
        importExportComponent.importFromJsonFile("useradminservicetest.json", ImportFileTypeEnum.IN_ARCHIVE, null);

        //create deployment
        login("asalesuser@infor.com", "useruser");
        DeployRequestDto deployRequestDto = new DeployRequestDto();
        deployRequestDto.setAmazonCredentialsId(amazonCredentialsDao.findByUserAndName(userDao.findByUsername("asalesuser@infor.com"), "Dummy Key").getId());
        deployRequestDto.setRegionId(4L);
        deployRequestDto.setScheduleType(DeployActionScheduleType.HOURLY);
        deployRequestDto.setScheduleValue("12");

        deployRequestDto.setProductIds(productIdsForTest());
        DeploymentStackDto dto = deploymentService.deployMultipleProducts(request,deployRequestDto);
        assertNotNull(dto);

        //run through commands.

        DeployActionDto dad = new DeployActionDto();
        dad.setVpcId(dto.getVpcId());
        dad.setType(DeployActionType.TERMINATE);
        dad.setAsync(false);
        dto = deploymentService.doAction(request,dad);
        assertEquals("DeploymentState ", DeploymentState.NOT_AVAILABLE.toString(), dto.getDeploymentState());
        assertEquals("DeploymentStatus ", DeploymentStatus.DEPLOY_INITIATED.toString(), dto.getDeploymentStatus());
        assertTrue("ScheduledStopAt ", dto.getScheduledStopAt() == null);

        List<Schedule> schedules = scheduleDao.findByEntityIdAndTypeAndStatus(String.valueOf(dto.getId()), ScheduleType.STOP_STACK, ScheduleStatus.CANCELED);
        assertEquals(1, schedules.size());
    }

    @Test
    public void testTerminateDoActionForTrial() throws Exception {
        //import extra users
        importExportComponent.importFromJsonFile("useradminservicetest.json", ImportFileTypeEnum.IN_ARCHIVE, null);

        //create deployment
        login("asalesuser@infor.com", "useruser");
        
        User user=userDao.findByUsername("asalesuser@infor.com");
        Product product = productDao.findByShortName("EAM");
        ProductVersion productVersion = productVersionDao.findByProductAndName(product,"EAM-BC-3");
        
        Region region = regionDao.findById(2L);

        TrialDto trial = trailService.launchTrial(getRequestStub(), productVersion, user, region, Locale.US);


        //run through commands.
        DeploymentStackDto dto;

        DeployActionDto dad = new DeployActionDto();
        dad.setVpcId("2");
        dad.setDeploymentStackId(trial.getId());
        dad.setType(DeployActionType.TERMINATE);
        dad.setDeploymentType(DeploymentType.INFOR24);
        dad.setAsync(false);
        dto = deploymentService.doAction(request,dad);
        assertEquals("DeploymentState ", DeploymentState.DELETED.toString(), dto.getDeploymentState());
        assertEquals("DeploymentStatus ", DeploymentStatus.UNKNOWN.toString(), dto.getDeploymentStatus());
        assertTrue("ScheduledStopAt ", dto.getScheduledStopAt().compareTo(trial.getExpirationDate()) < 1);
    }
    
    @Test
    public void testExtendDoActionForTrial() throws Exception {
        //import extra users
        importExportComponent.importFromJsonFile("useradminservicetest.json", ImportFileTypeEnum.IN_ARCHIVE, null);

        //create deployment
        login("asalesuser@infor.com", "useruser");
        
        User user=userDao.findByUsername("asalesuser@infor.com");
        Product product = productDao.findByShortName("EAM");
        ProductVersion productVersion = productVersionDao.findByProductAndName(product,"EAM-BC-3");
        assertNotNull(productVersion);
        Region region = regionDao.findById(2L);

        TrialDto trial = trailService.launchTrial(getRequestStub(), productVersion, user, region, Locale.US);


        //run through commands.
        DeploymentStackDto dto;

        DeployActionDto dad = new DeployActionDto();
        dad.setDeploymentStackId(trial.getId());
        dad.setType(DeployActionType.EXTEND);
        dad.setDeploymentType(DeploymentType.INFOR24);
        dto = deploymentService.doAction(request,dad);
        assertEquals("DeploymentState ", DeploymentState.AVAILABLE.toString(), dto.getDeploymentState());
        assertEquals("DeploymentStatus ", DeploymentStatus.STARTED.toString(), dto.getDeploymentStatus());
        assertTrue("ScheduledStopAt ", dto.getScheduledStopAt().compareTo(trial.getExpirationDate())  > 0);
    }

    @Test
    public void testChangeScheduleDoAction() throws Exception {
        //import extra users
        importExportComponent.importFromJsonFile("useradminservicetest.json", ImportFileTypeEnum.IN_ARCHIVE, null);

        //create deployment
        login("asalesuser@infor.com", "useruser");
        DeployRequestDto deployRequestDto = new DeployRequestDto();
        deployRequestDto.setAmazonCredentialsId(amazonCredentialsDao.findByUserAndName(userDao.findByUsername("asalesuser@infor.com"), "Dummy Key").getId());
        deployRequestDto.setRegionId(4L);
        deployRequestDto.setScheduleType(DeployActionScheduleType.HOURLY);
        deployRequestDto.setScheduleValue("12");

        deployRequestDto.setProductIds(productIdsForTest());
        DeploymentStackDto dto = deploymentService.deployMultipleProducts(request,deployRequestDto);
        assertNotNull(dto);

        //run through commands.
        DeployActionDto dad = new DeployActionDto();
        dad.setVpcId(dto.getVpcId());
        dad.setType(DeployActionType.CHANGE_SCHEDULE);
        dad.setScheduleValue("");
        dad.setScheduleType(DeployActionScheduleType.INDEFINITE);
        dad.setAsync(false);
        dto = deploymentService.doAction(request,dad);
        assertEquals("DeploymentState ", DeploymentState.NOT_AVAILABLE.toString(), dto.getDeploymentState());
        assertEquals("DeploymentStatus ", DeploymentStatus.DEPLOY_INITIATED.toString(), dto.getDeploymentStatus());
        assertTrue("ScheduledStopAt ", dto.getScheduledStopAt() == null);

        List<Schedule> schedules = scheduleDao.findByEntityIdAndTypeAndStatus(String.valueOf(dto.getId()), ScheduleType.STOP_STACK, ScheduleStatus.CANCELED);
        assertEquals(1, schedules.size());
        schedules = scheduleDao.findByEntityIdAndTypeAndStatus(String.valueOf(dto.getId()), ScheduleType.STOP_STACK, ScheduleStatus.QUEUED);
        assertEquals(0, schedules.size());

        //run through commands.
        dad.setVpcId(dto.getVpcId());
        dad.setType(DeployActionType.CHANGE_SCHEDULE);
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 1);
        long stopTime = calendar.getTimeInMillis();
        dad.setScheduleValue(String.valueOf(stopTime));
        dad.setScheduleType(DeployActionScheduleType.STOP_DATE);
        dad.setAsync(false);
        dto = deploymentService.doAction(request,dad);
        assertEquals("DeploymentState ", DeploymentState.NOT_AVAILABLE.toString(), dto.getDeploymentState());
        assertEquals("DeploymentStatus ", DeploymentStatus.DEPLOY_INITIATED.toString(), dto.getDeploymentStatus());
        assertEquals("ScheduledStopAt ", stopTime, dto.getScheduledStopAt().getTime());

        schedules = scheduleDao.findByEntityIdAndTypeAndStatus(String.valueOf(dto.getId()), ScheduleType.STOP_STACK, ScheduleStatus.CANCELED);
        assertEquals(1, schedules.size());
        schedules = scheduleDao.findByEntityIdAndTypeAndStatus(String.valueOf(dto.getId()), ScheduleType.STOP_STACK, ScheduleStatus.QUEUED);
        assertEquals(1, schedules.size());
        final Schedule schedule = schedules.get(0);
        assertEquals("Scheduled time:", stopTime, schedule.getScheduledAt().getTime());


        //run through commands.
        dad.setVpcId(dto.getVpcId());
        dad.setType(DeployActionType.CHANGE_SCHEDULE);
        dad.setScheduleValue("");
        dad.setScheduleType(DeployActionScheduleType.INDEFINITE);
        dad.setAsync(false);
        dto = deploymentService.doAction(request,dad);
        assertEquals("DeploymentState ", DeploymentState.NOT_AVAILABLE.toString(), dto.getDeploymentState());
        assertEquals("DeploymentStatus ", DeploymentStatus.DEPLOY_INITIATED.toString(), dto.getDeploymentStatus());
        assertTrue("ScheduledStopAt ", dto.getScheduledStopAt() == null);

        schedules = scheduleDao.findByEntityIdAndTypeAndStatus(String.valueOf(dto.getId()), ScheduleType.STOP_STACK, ScheduleStatus.CANCELED);
        assertEquals(2, schedules.size());
        schedules = scheduleDao.findByEntityIdAndTypeAndStatus(String.valueOf(dto.getId()), ScheduleType.STOP_STACK, ScheduleStatus.QUEUED);
        assertEquals(0, schedules.size());

    }

    @Test
    public void testDeploymentStackLogging() throws Exception {
        //import extra users
        importExportComponent.importFromJsonFile("useradminservicetest.json", ImportFileTypeEnum.IN_ARCHIVE, null);

        //create deployment
        login("asalesuser@infor.com", "useruser");
        DeployRequestDto deployRequestDto = new DeployRequestDto();
        deployRequestDto.setAmazonCredentialsId(amazonCredentialsDao.findByUserAndName(userDao.findByUsername("asalesuser@infor.com"), "Dummy Key").getId());
        deployRequestDto.setRegionId(4L);

        deployRequestDto.setProductIds(productIdsForTest());
        DeploymentStackDto dto = deploymentService.deployMultipleProducts(request, deployRequestDto);
        assertNotNull(dto);

        DeploymentStackUpdateDto dsud = new DeploymentStackUpdateDto();
        dsud.setAction(DeploymentStackLogAction.CHANGE_IN_STATE);
        dsud.setState(DeploymentState.AVAILABLE);
        dsud.setVpcId(dto.getVpcId());
        dsud.setMessage("MESSAGE1");
        deploymentStackListener.logAction(dsud);

        List<DeploymentStackLogDto> logs = deploymentService.getDeploymentStackLogs(dto.getVpcId(), null, null);
        assertEquals(1, logs.size());
        assertEquals(DeploymentState.AVAILABLE, logs.get(0).getState());
        assertEquals(dto.getVpcId(), logs.get(0).getVpcId());
        assertEquals(dto.getId(), logs.get(0).getDeploymentStackId());
        assertEquals("MESSAGE1", logs.get(0).getMessage());
        assertEquals(DeploymentStackLogAction.CHANGE_IN_STATE, logs.get(0).getLogAction());

        dsud.setAction(DeploymentStackLogAction.CHANGE_IN_STATUS);
        dsud.setState(DeploymentState.NOT_AVAILABLE);
        dsud.setStatus(DeploymentStatus.DEPLOYED);
        dsud.setMessage("MESSAGE2");
        deploymentStackListener.logAction(dsud);

        logs = deploymentService.getDeploymentStackLogs(null, null, dto.getId());
        assertEquals(2, logs.size());
        assertEquals(DeploymentState.NOT_AVAILABLE, logs.get(1).getState());
        assertEquals(DeploymentStatus.DEPLOYED, logs.get(1).getStatus());
        assertEquals("MESSAGE2", logs.get(1).getMessage());
        assertEquals(DeploymentStackLogAction.CHANGE_IN_STATUS, logs.get(1).getLogAction());
        assertEquals(dto.getVpcId(), logs.get(1).getVpcId());

        DeploymentStackDto stackDto = deploymentService.getByDeploymentStackId(dto.getId(), null);
        assertNotNull(stackDto.getLastStartedAt());
        assertNotNull(stackDto.getNumServers());
    }

    @Test
    public void testCountAmiDescriptors() throws Exception {

        assertEquals(14, amiDescriptorDao.count());


    }

    //TODO - Does not test anything - fix or remove.
    @Test
    public void testCountProductAmiDescriptors() throws Exception {
        Product eam = productDao.findByShortName("EAM");
        Product xm = productDao.findByShortName("XM");
        Product syteline = productDao.findByShortName("Syteline");
        Product ln = productDao.findByShortName("ERP Enterprise");
     /*   assertEquals(1, xm.getAmiDescriptors().size());
        assertEquals(2, eam.getAmiDescriptors().size());
        assertEquals(2, syteline.getAmiDescriptors().size());
        assertEquals(3, ln.getAmiDescriptors().size());
    */
    }

    @Test
    public void testStackBuilderDeployment_NoCred() throws Exception {
        login("sales@infor.com", "useruser");
        userDao.findByUsername("sales@infor.com");


        DeployRequestDto deployRequestDto = new DeployRequestDto();
        deployRequestDto.setDeploymentName("Bob");
        Product eam = productDao.findByShortName("EAM");
        Product erp = productDao.findByShortName("ERP Enterprise");
        deployRequestDto.getProductIds().add(new Long[]{eam.getId(),eam.getProductVersions().get(0).getId()});
        deployRequestDto.getProductIds().add(new Long[]{erp.getId(),erp.getProductVersions().get(0).getId()});
        deployRequestDto.setAsync(false);
        logger.info("Preparing to deploy (BAD) test deployment");

        NullEmailProvider nep = new NullEmailProvider();
        DummyDeployStackListener ddsl = new DummyDeployStackListener(deploymentStackListener);

        try {
            doMultiDeployment(deployRequestDto, nep, ddsl);
            fail("We should have gotten an error without credentials");
        } catch (Exception e) {
            logger.info("Exception encountered test deploying with no amazon credentials", e);

        }

    }

    @Test
    public void testStackBuilderDeployment_NoProducts() throws Exception {
        login("sales@infor.com", "useruser");
        User user = userDao.findByUsername("sales@infor.com");
        AmazonCredentials fake = new AmazonCredentials();
        fake.setAwsKey("FakeAwsKey");
        fake.setSecretKey("psst-fake_secret");
        fake.setName("Fake key");
        fake.setUser(user);
        amazonCredentialsDao.save(fake);
        amazonCredentialsDao.flush();

        assertNotNull(fake.getId());

        DeployRequestDto deployRequestDto = new DeployRequestDto();
        deployRequestDto.setDeploymentName("Bob");
        deployRequestDto.setAmazonCredentialsId(fake.getId());
        //deployRequestDto.setRegionId(4L);
        deployRequestDto.setAsync(false);
        logger.info("Preparing to deploy (BAD) test deployment");

        NullEmailProvider nep = new NullEmailProvider();
        DummyDeployStackListener ddsl = new DummyDeployStackListener(deploymentStackListener);
        assertEquals("product list should be empty", 0, deployRequestDto.getProductIds().size());

        try {
            doMultiDeployment(deployRequestDto, nep, ddsl);
            fail("We should have gotten an error with 0 products!");
        } catch (Exception e) {
            if (!e.getMessage().equals("Empty product list")) {
                fail("Error message mismatched:" + e.getMessage());
            }
            logger.info("Correct exception received for 0 products");
        }


    }

    private AmazonCredentials createAmazonCredentials(User user) {
        AmazonCredentials fake = new AmazonCredentials();
        fake.setAwsKey("FakeAwsKey");
        fake.setSecretKey("psst-fake_secret");
        fake.setName("Fake key");
        fake.setUser(user);
        amazonCredentialsDao.save(fake);
        amazonCredentialsDao.flush();
        assertNotNull(fake);
        return fake;
    }

    private DeployRequestDto buildValidDeployRequestDto(AmazonCredentials fake) {

        DeployRequestDto deployRequestDto = new DeployRequestDto();
        deployRequestDto.setDeploymentType(DeploymentType.AWS);
        deployRequestDto.setDeploymentName("Bob");
        Product eam = productDao.findByShortName("EAM");
        Product erp = productDao.findByShortName("ERP Enterprise");
        deployRequestDto.getProductIds().add(new Long[]{eam.getId(),eam.getProductVersions().get(0).getId()});
        deployRequestDto.getProductIds().add(new Long[]{erp.getId(),erp.getProductVersions().get(0).getId()});
        deployRequestDto.setAmazonCredentialsId(fake.getId());
        deployRequestDto.setScheduleType(DeployActionScheduleType.HOURLY);
        deployRequestDto.setScheduleValue("12");
        deployRequestDto.setRegionId(4L);
        return deployRequestDto;
    }

    private DeployRequestDto buildValidTrialRequestDto(String productShortName, String name) {
    	DeployRequestDto deployRequestDto=new DeployRequestDto();
        Product product = productDao.findByShortName(productShortName);
        deployRequestDto.getProductIds().add(new Long[]{product.getId(),product.getProductVersions().get(0).getId()});

    	deployRequestDto.setDeploymentName(name);
    	deployRequestDto.setDeploymentType(DeploymentType.INFOR24);
    	deployRequestDto.setRegionId(1L);
    	return deployRequestDto;

    }

    private void runAssertionsAfterValidDeployment(DeploymentStackDto dto, DeployRequestDto deployRequestDto, User user) {

        assertTrue(dto.getCreatedByUser().getId().equals(user.getId()));
        assertEquals("Server count should be 5", 5, (int) dto.getNumServers());
        assertEquals(DeploymentStatus.DEPLOYED.toString(), dto.getDeploymentStatus());
        assertEquals(DeploymentState.AVAILABLE.toString(), dto.getDeploymentState());
        assertNotNull(dto.getVpcId());
        assertNotNull(dto.getElasticIp());
        assertEquals(deployRequestDto.getDeploymentName(), dto.getDeploymentName());
        assertEquals(DeploymentType.AWS,dto.getDeploymentType());
    }

    private void runAssertionsAfterValidTrialDeployment(DeploymentStackDto dto, DeployRequestDto deployRequestDto, User user){
        assertTrue(dto.getCreatedByUser().getId().equals(user.getId()));
        assertEquals("Server count should be 1", 1, (int) dto.getNumServers());
        assertEquals(DeploymentStatus.STARTED.toString(), dto.getDeploymentStatus());
        assertEquals(DeploymentState.AVAILABLE.toString(), dto.getDeploymentState());
        assertTrue(dto.getVpcId()==null);
        assertTrue(dto.getElasticIp()==null);
        assertEquals(DeploymentType.INFOR24,dto.getDeploymentType());
        assertEquals(deployRequestDto.getDeploymentName(), dto.getDeploymentName());

    }
    @Test
    public void testTrialLaunchViaDeploymentService() throws Exception {
        importExportComponent.importFromJsonFile("deploymentservice-test.json", ImportFileTypeEnum.IN_ARCHIVE, null);

        login("sales@infor.com","useruser");

        User user=userDao.findByUsername("bob.vila@this-old-house.com");
        assertNotNull(user);
        User secUser=userDao.findByUsername("sales@infor.com");
        assertNotNull(secUser);
        DeployRequestDto reqDto=this.buildValidTrialRequestDto("EAM","EAM-Test");
        reqDto.setUserId(user.getId());

        NullEmailProvider nep=new NullEmailProvider();
        trialEmailComponent.setEmailProvider(nep);

        DeploymentStackDto dto=deploymentService.deployMultipleProducts(request, reqDto);
        this.runAssertionsAfterValidTrialDeployment(dto, reqDto, secUser);


    }
    @Test
    public void testDeploymentStackWithBuilder() throws Exception {
        long schedCount = scheduleDao.count();

        login("sales@infor.com", "useruser");

        User user = userDao.findByUsername("sales@infor.com");
        assertNotNull(user);
        AmazonCredentials fake = createAmazonCredentials(user);
        DeployRequestDto deployRequestDto = buildValidDeployRequestDto(fake);

        logger.info("Preparing to deploy test deployment");
        NullEmailProvider nep = new NullEmailProvider();
        DummyDeployStackListener ddsl = new DummyDeployStackListener(deploymentStackListener);

        DeploymentStackDto dto = doMultiDeployment(deployRequestDto, nep, ddsl);

        runAssertionsAfterValidDeployment(dto, deployRequestDto, user);
        //stop schedule and notification schedule
        assertEquals((schedCount + 2), scheduleDao.count());

    }

    @Test
    public void testDeploymentStackForCompletesListOfFailures() throws Exception {
        login("sales@infor.com", "useruser");

        User user = userDao.findByUsername("sales@infor.com");
        assertNotNull(user);
        AmazonCredentials fake = createAmazonCredentials(user);
        DeployRequestDto deployRequestDto = buildValidDeployRequestDto(fake);

        logger.info("Preparing to deploy test deployment");
        NullEmailProvider nep = new NullEmailProvider();
        DummyDeployStackListener ddsl = new DummyDeployStackListener(deploymentStackListener);

        for (DummyFailPoint failPoint : DummyFailPoint.AUTO_TEST) {
            dummyAwsOperations.clearFailPoints();
            dummyAwsOperations.addDummyFailPoint(failPoint);
            try {
                DeploymentStackDto dto = doMultiDeployment(deployRequestDto, nep, ddsl);
                if (failPoint.examineStateAndStatus()) {
                    assertEquals(failPoint.getExpectedDeploymentState().toString(), dto.getDeploymentState());
                    assertEquals(failPoint.getExpectedDeploymentStatus().toString(), dto.getDeploymentStatus());
                } else {
                    fail("Error should have been thrown for failPoint:" + failPoint.name());
                }

            } catch (DummyFailPointException dfpe) {
                assertEquals("Fail point should be at:" + failPoint.name(), failPoint, dfpe.getDummyFailPoint());
            } catch (ExecutionException ee) {
                assertTrue(ee.getCause() instanceof DummyFailPointException);
                assertEquals("(EE)Fail point should be at:" + failPoint.name(), failPoint, ((DummyFailPointException) ee.getCause()).getDummyFailPoint());
            }


        }
    }

    private DeploymentStackDto doMultiDeployment(DeployRequestDto deployRequestDto, EmailProvider newEmailProvider, DummyDeployStackListener dummyDeployStackListener) throws Exception {


        EmailProvider oldEmailProvider = stackBuilder.getEmailProvider();
        AwsOperations oldAwsOperations = stackBuilder.getAwsOperations();
        DeploymentStackListener oldStackListener = stackBuilder.getStackListener();

        stackBuilder.setAwsOperations(dummyAwsOperations);
        stackBuilder.setEmailProvider(newEmailProvider);
        stackBuilder.setStackListener(dummyDeployStackListener);
        
        CreateStackRequest createStackRequest = new CreateStackRequest();

        final SecurityUser currentUser = securityService.getCurrentUser();
        String destEmail = currentUser.getUsername();
        //todo -- remove this.
        createStackRequest.setLocale(currentUser.getLanguage());

        createStackRequest.setDestEmails(Collections.singletonList(destEmail));


        final Set<AmiDescriptor> amiDescriptors = createStackRequest.getAmiDescriptors();

        ScheduleDispatch dispatch = new ScheduleDispatch();

        DeploymentStack deploymentStack = deploymentServiceComponent.createAndSaveStack(
                deployRequestDto,
                amiDescriptors,
                createStackRequest.getProductNames(),
                dispatch);

        try {
            dispatch.dispatch();
        } catch (Exception e) {
            logger.error("Error dispatching scheduled task in doAction()",e);
        }

        createStackRequest.setDeploymentStack(deploymentStack);

        Long deploymentStackId = deploymentStack.getId();
        
        Region region = regionDao.findById(deployRequestDto.getRegionId());
        createStackRequest.setRegionName(region.getName());

        if (deploymentStack.getDeployedProductVersions().size() > 1) {
            amiDescriptors.add(amiDescriptorDao.findByName("GDE-IUX"));
            amiDescriptors.add(amiDescriptorDao.findByName("GDE-DC"));
        }

        //sync
        stackBuilder.createStack(createStackRequest).get();
        dummyDeployStackListener.flush();

        deploymentStack = deploymentStackDao.findById(deploymentStackId);

        stackBuilder.setAwsOperations(oldAwsOperations);
        stackBuilder.setEmailProvider(oldEmailProvider);
        stackBuilder.setStackListener(oldStackListener);
        return deploymentServiceComponent.getDtoForStack(deploymentStack);

    }
}
