package com.infor.cloudsuite.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.infor.cloudsuite.AbstractTest;
import com.infor.cloudsuite.dao.AmazonCredentialsDao;
import com.infor.cloudsuite.dao.DeploymentStackDao;
import com.infor.cloudsuite.dao.ProductDao;
import com.infor.cloudsuite.dao.ProductVersionDao;
import com.infor.cloudsuite.dao.RegionDao;
import com.infor.cloudsuite.dao.TrialEnvironmentDao;
import com.infor.cloudsuite.dao.TrialInstanceDao;
import com.infor.cloudsuite.dao.UserDao;
import com.infor.cloudsuite.dto.DeployRequestDto;
import com.infor.cloudsuite.dto.DeploymentStackDto;
import com.infor.cloudsuite.dto.DeploymentType;
import com.infor.cloudsuite.dto.dashboard.DashboardDto;
import com.infor.cloudsuite.dto.dashboard.DashboardRollingDto;
import com.infor.cloudsuite.dto.dashboard.DashboardRollingMonthDto;
import com.infor.cloudsuite.dto.dashboard.DashboardRollingPeriodDto;
import com.infor.cloudsuite.dto.db.ProductCountDto;
import com.infor.cloudsuite.entity.AmazonCredentials;
import com.infor.cloudsuite.entity.DeploymentStack;
import com.infor.cloudsuite.entity.Product;
import com.infor.cloudsuite.entity.ProductVersion;
import com.infor.cloudsuite.entity.Region;
import com.infor.cloudsuite.entity.Role;
import com.infor.cloudsuite.entity.TrialInstance;
import com.infor.cloudsuite.entity.User;
import com.infor.cloudsuite.platform.components.EmailProvider;
import com.infor.cloudsuite.platform.components.GuidProvider;
import com.infor.cloudsuite.platform.components.NullEmailProvider;
import com.infor.cloudsuite.platform.components.SettingsProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Transactional
public class DashboardServiceTest extends AbstractTest {

	private final Logger logger=LoggerFactory.getLogger(DashboardServiceTest.class);
	@Resource
	private AmazonCredentialsDao amazonCredentialsDao;
	@Resource
	private UserDao userDao;
	@Resource
	private ProductDao productDao;
	@Resource
	private ProductVersionDao productVersionDao;
	@Resource
	private DashboardService dashboardService;
	@Resource
	private DeploymentService deploymentService;
	@Resource
	private TrialEnvironmentDao trialEnvironmentDao;
	@Resource
	private TrialInstanceDao trialInstanceDao;
	@Resource
	private RegionDao regionDao;
	@Resource
	private SettingsProvider settingsProvider;
	@Resource
	private GuidProvider guidProvider;
	@Resource
	private DeploymentStackDao deploymentStackDao;
	@Resource
	private TrialService trialService;
	
	private NullEmailProvider nullEmailProvider=new NullEmailProvider();
	private EmailProvider currEmailProvider;
	private boolean currMode;
	private boolean currRecycleInfor;
	
	
    @Before
    public void setUp() {
    	this.currEmailProvider=deploymentService.getStackBuilder().getEmailProvider();
    	deploymentService.getStackBuilder().setEmailProvider(nullEmailProvider);
    	this.currMode=settingsProvider.isProductionMode();
    	this.currRecycleInfor=settingsProvider.isTrialRecycleInforDomain();
    	settingsProvider.setProductionMode(false);
    	settingsProvider.setTrialRecycleInforDomain(false);
    }
    
    @After
    public void after() {
    	super.after();
    	deploymentService.getStackBuilder().setEmailProvider(this.currEmailProvider);
    	nullEmailProvider.getAsyncEmails().clear();
    	nullEmailProvider.getEmails().clear();
    	settingsProvider.setProductionMode(currMode);
    	settingsProvider.setTrialRecycleInforDomain(currRecycleInfor);
    }


@Test
public void testGetAdminDashboard() throws Exception {

	int oneMonthUserCount=5;
	int oneDayUserCount=5;
	int oneWeekUserCount=5;

	
	Date now=new Date();

	
	GregorianCalendar cal=new GregorianCalendar();
	cal.setTime(now);
	//set two weeks back
	
	cal.add(GregorianCalendar.DATE, -1);
	Date yesterday=cal.getTime();
	long yesterdayOffset=userDao.countByActiveAndCreatedAt(true, yesterday);
	cal.add(GregorianCalendar.DATE, -5);
	Date winOneWeek=cal.getTime();
	cal.add(GregorianCalendar.DATE,-1);
	Date oneWeek=cal.getTime();
	long oneWeekOffset=userDao.countByActiveAndCreatedAt(true, oneWeek);
	cal.add(GregorianCalendar.DATE,-22);
	Date winOneMonth=cal.getTime();
	
	cal.add(GregorianCalendar.DATE,-2);
	Date oneMonth=cal.getTime();
	cal.add(GregorianCalendar.YEAR,-20);
	Date effectivelyForever=cal.getTime();
	
	long oneMonthOffset=userDao.countByActiveAndCreatedAt(true, oneMonth);
	long foreverOffset=userDao.countByActiveAndCreatedAt(true, effectivelyForever);
	
	
	
	long baseMonthUsers=oneMonthUserCount+oneWeekUserCount+oneDayUserCount;
	long baseWeekUsers=oneWeekUserCount+oneDayUserCount;

    ArrayList<User> allUsers=new ArrayList<>();
	logger.info("oneMonthUserCount:"+winOneMonth.getTime());
	logger.info("oneWeekUserCount:"+winOneWeek.getTime());
	logger.info("oneDayUserCount:"+now.getTime());
	allUsers.addAll(createUsers(winOneMonth,"fakedomain1.com","first","last","password",oneMonthUserCount));
	allUsers.addAll(createUsers(winOneWeek,"fakedomain2.com","first","last","password",oneWeekUserCount));
	allUsers.addAll(createUsers(now,"fakedomain3.com","first","last","password",oneDayUserCount));
	
	loginAdminUser();
	userDao.save(allUsers);
	for (User user : allUsers) {
		logger.info("oneMonthUserCount:"+winOneMonth+",millis:"+winOneMonth.getTime());
		logger.info("oneWeekUserCount:"+winOneWeek+",millis:"+winOneWeek.getTime());
		logger.info("oneDayUserCount:"+now+",millis:"+now.getTime());
		logger.info("user["+user.getUsername()+"] createdAt:"+user.getCreatedAt()+" , millis:"+user.getCreatedAt().getTime());
	}
	Region region=regionDao.findById(1L);
	
	/*
	 * TRIALS SECTION
	 */
	List<Product> productsForTrial=productDao.findByTrialsAvailable(true);
	ArrayList<Long> validTrialProductIds=new ArrayList<>();
	for (Product product : productsForTrial) {
		
		ProductVersion productVersion=pickOne(product.getProductVersions(),region,false);
		
		if (productVersion ==null) {
			continue;
		}
		validTrialProductIds.add(product.getId());
		
		HashSet<Long> dates=new HashSet<>();
		
		for (User user : allUsers) {
			loginAdminUser();	
			dates.add(user.getCreatedAt().getTime());
			activateTrial(user.getUsername(),product);
			DeployRequestDto deployRequestDto=this.getTrialRequestFor(user, productVersion, region,user.getCreatedAt());
			login(user.getUsername(),"password");
	
			DeploymentStackDto retDto=deploymentService.deployMultipleProducts(getRequestStub(), deployRequestDto);
			TrialInstance instance = trialInstanceDao.findById(retDto.getId());
			assertEquals(instance.getCreatedAt(),user.getCreatedAt());
		}
		assertEquals(3,dates.size());
	
	}
	//END TRIALS SECTION
	
	/*
	 * 
	 * DO DEPLOYMENTS
	 * SECTION
	 */
	
	loginAdminUser();
	
	
	//need something there :)
	addAWSCredentials(allUsers);
	//NEED TO VARY.  For now, we will do this with all products for everyone
	List<Long[]> productAndVersionIdList=this.getDtoProdAndVersionList(productDao.findAll(), region);
	int badUsers=0;
	for (User user : allUsers) {			
			AmazonCredentials amazonCredentials=amazonCredentialsDao.findByUserAndName(user, "Dummy Key");
			if (amazonCredentials == null) {
				badUsers++;
				continue;
			}
			loginAdminUser();
			activateDeploys(user,productAndVersionIdList);
			login(user.getUsername(),"password");
			DeployRequestDto reqDto=this.getDeployRequestFor(user, productAndVersionIdList, region, amazonCredentials,user.getCreatedAt());
			DeploymentStackDto result=deploymentService.deployMultipleProducts(getRequestStub(), reqDto);
			DeploymentStack stack=deploymentStackDao.findById(result.getId());
			assertEquals("User and stack created at times should be ==",user.getCreatedAt(),stack.getCreatedAt());
	
	}
	assertEquals(0,badUsers);
	
	//END DO DEPLOYMENTS SECTION
	
	/*
	 * TESTING THE RETURN
	 */
	loginAdminUser();
	DashboardDto dashboardDto=dashboardService.getAdminDashboard();
	assertEquals("Compare what trial count should be",(long)(validTrialProductIds.size()*allUsers.size()),(long)dashboardDto.getActiveInfor24());
	assertEquals("Compare all users", foreverOffset+allUsers.size(),(long)dashboardDto.getUsersTotal());
	assertEquals("Compare all time deployment count",(long)allUsers.size(),(long)dashboardDto.getAllTimeAws());
	assertEquals("Compare all time trial count",(long)(validTrialProductIds.size()*allUsers.size()),(long)dashboardDto.getAllTimeInfor24());
	 for (Long[] ids : productAndVersionIdList) {
	 

		//SPECIFIC day/week/month checks for products
		assertEquals("Compare deploy counts for day/productId:"+ids[0],(long) oneDayUserCount,(long)dashboardDto.getDay().getAwsDeploymentsByProduct().get(ids[0]));
		assertEquals("Compare deploy counts for week/productId:"+ids[0], baseWeekUsers,(long)dashboardDto.getWeek().getAwsDeploymentsByProduct().get(ids[0]));
		assertEquals("Compare deploy counts for month/productId:"+ids[0], baseMonthUsers,(long)dashboardDto.getMonth().getAwsDeploymentsByProduct().get(ids[0]));
		assertEquals("Compare all time deploy counts for productId:"+ids[0],(long)allUsers.size(),(long)dashboardDto.getActiveAwsByProduct().get(ids[0]));
	}
	
	
	List<ProductCountDto> prodCountDtos=trialInstanceDao.countByCreatedAtAfter(yesterday);
	for (ProductCountDto count : prodCountDtos) {
		logger.info("prod-id:"+count.getProductId()+",count:"+count.getCount());
	}
	
	int processed=0;
	for (Long productId : validTrialProductIds) {		
		assertEquals("Compare counts for productId:"+productId,(long)allUsers.size(),(long)dashboardDto.getActiveInfor24ByProduct().get(productId));
		//SPECIFIC day/week/month checks for products
		assertEquals("Compare trial counts for day/productId:"+productId+",shortName:"+productDao.findById(productId).getShortName()+",processed already:"+processed
				,(long) oneDayUserCount,(long)dashboardDto.getDay().getInfor24DeploymentsByProduct().get(productId));
		assertEquals("Compare trial counts for week/productId:"+productId, baseWeekUsers,(long)dashboardDto.getWeek().getInfor24DeploymentsByProduct().get(productId));
		assertEquals("Compare trial counts for month/productId:"+productId, baseMonthUsers,(long)dashboardDto.getMonth().getInfor24DeploymentsByProduct().get(productId));
		processed++;
	}	
	

	
	assertEquals("Compare users added since day", yesterdayOffset+ oneDayUserCount,(long)dashboardDto.getDay().getNewUsersCount());
	assertEquals("Compare users added since week", oneWeekOffset+baseWeekUsers,(long)dashboardDto.getWeek().getNewUsersCount());
	assertEquals("Compare users added since day", oneMonthOffset+baseMonthUsers,(long)dashboardDto.getMonth().getNewUsersCount());
		
	
}

private int totalList (int ... ints) {
	int total=0;
	for (int i : ints) {
		total+=i;
	}
	return total;
}
@Test
public void testGetRollingDeploys() throws Exception {
	int sixMonthDeploy=2;
	int fiveMonthDeploy=3;
	int fourMonthDeploy=5;
	int threeMonthDeploy=9;
	int twoMonthDeploy=7;
	int oneMonthDeploy=4;
	int totalDeploy=totalList(sixMonthDeploy,fiveMonthDeploy,fourMonthDeploy,threeMonthDeploy,twoMonthDeploy,oneMonthDeploy);
	int[] deployedCountList=new int[]{sixMonthDeploy,fiveMonthDeploy,fourMonthDeploy,threeMonthDeploy,twoMonthDeploy,oneMonthDeploy};
	int sixMonthTrial=5;
	int fiveMonthTrial=9;
	int fourMonthTrial=4;
	int threeMonthTrial=12;
	int twoMonthTrial=1;
	int oneMonthTrial=8;
	int totalTrial=totalList(sixMonthTrial,fiveMonthTrial,fourMonthTrial,threeMonthTrial,twoMonthTrial,oneMonthTrial);
	int[] trialCountList=new int[]{sixMonthTrial,fiveMonthTrial,fourMonthTrial,threeMonthTrial,twoMonthTrial,oneMonthTrial};
	
	assertEquals("Lengths of trialMonths and deployMonths should be same",trialCountList.length,deployedCountList.length);
	
	User user=this.createUser("uberuser@infor.com", "UberUser", "LastName", "password", Role.ROLE_SALES);
	userDao.save(user);
	addAWSCredentials(user);
	user=userDao.findByUsername("uberuser@infor.com");
	assertNotNull(user);
	
	Date now=new Date();
	GregorianCalendar cal=new GregorianCalendar();
	cal.setTime(now);
	zeroOutTime(cal);
	cal.set(GregorianCalendar.DATE, 1);
	
	
	List<Date> datesToDeploy=getDates(cal.getTime(),oneMonthDeploy);
	List<Date> datesToTry=getDates(cal.getTime(),oneMonthTrial);
	
	cal.add(GregorianCalendar.MONTH, -1);
	datesToDeploy.addAll(getDates(cal.getTime(),twoMonthDeploy));
	datesToTry.addAll(getDates(cal.getTime(),twoMonthTrial));
	
	cal.add(GregorianCalendar.MONTH, -1);
	datesToDeploy.addAll(getDates(cal.getTime(),threeMonthDeploy));
	datesToTry.addAll(getDates(cal.getTime(),threeMonthTrial));
	
	cal.add(GregorianCalendar.MONTH, -1);
	datesToDeploy.addAll(getDates(cal.getTime(),fourMonthDeploy));
	datesToTry.addAll(getDates(cal.getTime(),fourMonthTrial));
	
	cal.add(GregorianCalendar.MONTH, -1);
	datesToDeploy.addAll(getDates(cal.getTime(),fiveMonthDeploy));
	datesToTry.addAll(getDates(cal.getTime(),fiveMonthTrial));
	
	cal.add(GregorianCalendar.MONTH, -1);
	datesToDeploy.addAll(getDates(cal.getTime(),sixMonthDeploy));
	datesToTry.addAll(getDates(cal.getTime(),sixMonthTrial));
			
	assertEquals("Trial total dates",totalTrial,datesToTry.size());
	assertEquals("Deploy total dates",totalDeploy,datesToDeploy.size());
	
	Region region=regionDao.findById(1L);
	assertNotNull(region);
	/*
	 * TRIALS SECTION
	 */

	
	List<Product> productsForTrial=productDao.findByTrialsAvailable(true);
	ArrayList<Long> validTrialProductIds=new ArrayList<>();
	
	for (Product product : productsForTrial) {
		
		ProductVersion productVersion=pickOne(product.getProductVersions(),region,false);
		
		if (productVersion ==null) {
			continue;
		}
		loginAdminUser();
		this.activateTrial("uberuser@infor.com", product);
		validTrialProductIds.add(product.getId());
		login("uberuser@infor.com","password");
		
		for (Date tryDate : datesToTry) {
			DeployRequestDto deployRequestDto=this.getTrialRequestFor(user, productVersion, region, tryDate);
			DeploymentStackDto retDto=deploymentService.deployMultipleProducts(getRequestStub(), deployRequestDto);
			TrialInstance instance = trialInstanceDao.findById(retDto.getId());
			assertEquals(instance.getCreatedAt(),tryDate);
			
		}
	}
	
	List<Long[]> productAndVersionIdList=this.getDtoProdAndVersionList(productDao.findAll(), region);
	for (Date deployDate : datesToDeploy) {
		AmazonCredentials amazonCredentials=amazonCredentialsDao.findByUserAndName(user, "Dummy Key");
		assertNotNull(amazonCredentials);
		loginAdminUser();
		activateDeploys(user,productAndVersionIdList);
		login(user.getUsername(),"password");
		DeployRequestDto reqDto=this.getDeployRequestFor(user, productAndVersionIdList, region, amazonCredentials,deployDate);
		DeploymentStackDto result=deploymentService.deployMultipleProducts(getRequestStub(), reqDto);
		DeploymentStack stack=deploymentStackDao.findById(result.getId());
		assertEquals("deployDate and stack created at times should be ==",deployDate,stack.getCreatedAt());
	}
	
	loginAdminUser();
	DashboardRollingDto rollingDto=dashboardService.getRollingList(deployedCountList.length);
	assertEquals("Confirm correct num of months..", deployedCountList.length,(int)rollingDto.getMonthCount());
	int index=0;
	for (DashboardRollingPeriodDto period : rollingDto.getRollingDeployments()) {
		DashboardRollingMonthDto month=(DashboardRollingMonthDto)period;
		assertEquals("Confirm index position", index+1,(int)month.getMonth());
		assertEquals("Confirm total deploys for month (index:"+index+"):"+month.getName(),(long)deployedCountList[index],(long)month.getAws());
		assertEquals("Confirm total trials for month (index:"+index+"):"+month.getName(),(long)(trialCountList[index]*validTrialProductIds.size()),(long)month.getInfor24());
		
		index++;
	}
	
}
private void zeroOutTime(GregorianCalendar calendar) {
	calendar.set(GregorianCalendar.HOUR,0);
	calendar.set(GregorianCalendar.MINUTE, 0);
	calendar.set(GregorianCalendar.SECOND,0);
	calendar.set(GregorianCalendar.MILLISECOND,0);
	calendar.set(GregorianCalendar.HOUR_OF_DAY,0);
}


public List<Date> getDates(Date date, int number) {
	GregorianCalendar calendar=new GregorianCalendar();
	calendar.setTime(date);
	ArrayList<Date> dates=new ArrayList<>();
	for (int i=1;i<=number;i++) {
		calendar.add(GregorianCalendar.DATE,1);
		dates.add(calendar.getTime());
	}
	
	return dates;
}



//=================
private void activateDeploys(User user, List<Long[]> productList) {
	for (Long[] ids : productList) {
		Product product=productDao.findById(ids[0]);
		this.activateDeploy(user.getUsername(), product);
	}
}
private void addAWSCredentials(List<User> users) {
	for (User user : users) {
		addAWSCredentials(user);
	}
}

private void addAWSCredentials(User user) {
	AmazonCredentials amcred=new AmazonCredentials();
	amcred.setAwsKey("DUMMYKEY");
	amcred.setName("Dummy Key");
	amcred.setSecretKey("S3cr3t");
	amcred.setUser(user);
	amazonCredentialsDao.save(amcred);
}
private ProductVersion pickOne(List<ProductVersion> productVersions, Region region, boolean iCareAboutAmis) {
	for (ProductVersion version : productVersions) {
		if ((trialEnvironmentDao.countByProductVersionAndRegionAndAvailable(version,region,true) > 0) ) {
			if (iCareAboutAmis && version.getAmiDescriptors().size()<1) {
				continue;
			}
			return version;
		}
	}
	return null;
}



private List<Long[]> getDtoProdAndVersionList(List<Product> products, Region region) {
	ArrayList<Long[]> list=new ArrayList<>();
	for (Product product : products) {
		ProductVersion version=pickOne(product.getProductVersions(),region,true);
		if (version==null) {
			continue;
		}
		list.add(new Long[]{product.getId(),version.getId()});
	}
	return list;
}
private DeployRequestDto getTrialRequestFor(User user, ProductVersion productVersion,Region region,Date createdAt) {
	DeployRequestDto dto=new DeployRequestDto();
	dto.setUserId(user.getId());
	dto.setDeploymentName(user.getUsername()+":"+productVersion.getProduct().getShortName());
	dto.setDeploymentType(DeploymentType.INFOR24);
	dto.setRegionId(region.getId());
	dto.setCreatedAt(createdAt);
	ArrayList<Long[]> productIds=new ArrayList<>();
	productIds.add(new Long[]{productVersion.getProduct().getId(),productVersion.getId()});
	dto.setProductIds(productIds);
	
	return dto;
}
private DeployRequestDto getDeployRequestFor(User user, List<Long[]> productsAndVersions,Region region, AmazonCredentials amazonCredentials, Date createdAt) {
	DeployRequestDto dto=new DeployRequestDto();
	dto.setUserId(user.getId());
	dto.setProductIds(productsAndVersions);
	dto.setAmazonCredentialsId(amazonCredentials.getId());
	dto.setDeploymentName(guidProvider.generateGuid());
	dto.setDeploymentType(DeploymentType.AWS);
	dto.setRegionId(region.getId());
	dto.setCreatedAt(createdAt);
	return dto;
}

private List<User> createUsers(Date createdAt, String domain, String first, String last, String password, int num) {
	ArrayList<User> users=new ArrayList<>();
	
	for (int i=0; i<num;i++) {
		User user=createUser(first+"."+last+i+"@"+domain,first,last,password,createdAt,Role.ROLE_SALES);
		
		users.add(user);
		logger.info("User created at:"+user.getCreatedAt());
	}
	return users;
}

	
}
