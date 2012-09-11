package com.infor.cloudsuite.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import com.infor.cloudsuite.AbstractTest;
import com.infor.cloudsuite.dao.ProductDao;
import com.infor.cloudsuite.dao.ProductVersionDao;
import com.infor.cloudsuite.dao.RegionDao;
import com.infor.cloudsuite.dao.TrialInstanceDao;
import com.infor.cloudsuite.dao.UserDao;
import com.infor.cloudsuite.dto.TrialInstanceUpdateDto;
import com.infor.cloudsuite.entity.Product;
import com.infor.cloudsuite.entity.Region;
import com.infor.cloudsuite.entity.Role;
import com.infor.cloudsuite.entity.User;
import com.infor.cloudsuite.platform.CSWebApplicationException;
import com.infor.cloudsuite.platform.components.NullEmailProvider;
import com.infor.cloudsuite.platform.components.SettingsProvider;
import com.infor.cloudsuite.service.component.DeploymentServiceComponent;
import com.infor.cloudsuite.service.component.TrialEmailComponent;

import static junit.framework.Assert.assertEquals;

@Transactional
public class TrialServiceExtensionTest extends AbstractTest {


    @Resource
    private TrialService trialService;
    @Resource
    private UserDao userDao;
    @Resource
    private ProductDao productDao;
    @Resource
    private ProductVersionDao productVersionDao;
    
    @Resource
    private TrialInstanceDao trialInstanceDao;
    @Resource
    private RegionDao regionDao;
    @Resource
    private TrialEmailComponent trialEmailComponent;
    @Resource
    private DeploymentServiceComponent deploymentServiceComponent;
    
    @Resource
    private SettingsProvider settingsProvider;


    private boolean start_recycleInforTrials;
    private boolean start_recycleAllTrials;
    private boolean start_currentMode;


    @Override
    @Before
    public void before() {
        super.before();

        this.start_recycleInforTrials = settingsProvider.isTrialRecycleInforDomain();
        this.start_recycleAllTrials = settingsProvider.isTrialRecycleAllDomains();
        this.start_currentMode = settingsProvider.isProductionMode();


    }

    @Override
    @After
    public void after() {
        super.after();

        settingsProvider.setTrialRecycleInforDomain(start_recycleInforTrials);
        settingsProvider.setTrialRecycleAllDomains(start_recycleAllTrials);
        settingsProvider.setProductionMode(start_currentMode);


    }

    private Date getDaysOut(int daysOut) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, daysOut);
        return cal.getTime();
    }

    private void extendTrialWithDtoList(String type, int daysOutStart, long expectedDifference, List<TrialInstanceUpdateDto> dtosIn) {

        long startCount = trialInstanceDao.findByExpirationDateLessThan(getDaysOut(daysOutStart)).size();

        loginAdminUser();


        for (TrialInstanceUpdateDto dtoIn : dtosIn) {

            trialService.extendTrialExpiration(type, dtoIn);

        }

        assertEquals("Difference should match on extend", expectedDifference, startCount - trialInstanceDao.findByExpirationDateLessThan(getDaysOut(daysOutStart)).size());


    }

    private void extendSingleTrialInstanceUpdateDto(String type, int daysOutStart, long expectedDifference, TrialInstanceUpdateDto dtoIn) {
        long startCount = trialInstanceDao.findByExpirationDateLessThan(getDaysOut(daysOutStart)).size();
        loginAdminUser();
        trialService.extendTrialExpiration(type, dtoIn);
        assertEquals("Difference should match on extend{single dto}", expectedDifference, startCount - trialInstanceDao.findByExpirationDateLessThan(getDaysOut(daysOutStart)).size());

    }


    @Test
    public void testExtendTrialByUserIdAndProductId() {

        int daysToExtend = 15;
        int daysOutStart = 31;
        List<User> users = addUsersAndLaunchTrials();
        List<Product> products = productDao.findByTrialsAvailable(true);
        //TODO WS does not have trials
        products.remove(productDao.findByShortName("WS"));
        products.remove(productDao.findByShortName("HR Service Delivery"));
        products.remove(productDao.findByShortName("Infor10 SoftBrands HMS"));

        ArrayList<TrialInstanceUpdateDto> updateDtos = new ArrayList<TrialInstanceUpdateDto>();
        for (User user : users) {
            for (Product product : products) {

                TrialInstanceUpdateDto dtoIn = new TrialInstanceUpdateDto();

                dtoIn.setUserId(user.getId());
                dtoIn.setDaysToExtend(daysToExtend);
                dtoIn.setProductId(product.getId());

                updateDtos.add(dtoIn);
            }
        }

        extendTrialWithDtoList("byUser", daysOutStart, updateDtos.size(), updateDtos);


    }

    @Test
    public void testExtendTrialByUserIdAndProductShortName() {

        int daysToExtend = 30;
        int daysOutStart = 31;

        List<User> users = addUsersAndLaunchTrials();
        List<Product> products = productDao.findByTrialsAvailable(true);
        //TODO WS does not have trials
        products.remove(productDao.findByShortName("WS"));
        products.remove(productDao.findByShortName("HR Service Delivery"));
        products.remove(productDao.findByShortName("Infor10 SoftBrands HMS"));
        
        ArrayList<TrialInstanceUpdateDto> updateDtos = new ArrayList<TrialInstanceUpdateDto>();
        for (User user : users) {
            for (Product product : products) {

                TrialInstanceUpdateDto dtoIn = new TrialInstanceUpdateDto();

                dtoIn.setUserId(user.getId());
                //test default behavior (30)
                //dtoIn.setDaysToExtend(30);
                dtoIn.setProductShortName(product.getShortName());

                updateDtos.add(dtoIn);
            }
        }

        extendTrialWithDtoList("byUser", daysOutStart, updateDtos.size(), updateDtos);

    }

    @Test
    public void testExtendTrialByUserNameAndProductId() {
        int daysToExtend = 15;
        int daysOutStart = 31;
        List<User> users = addUsersAndLaunchTrials();
        List<Product> products = productDao.findByTrialsAvailable(true);
        //TODO WS does not have trials
        products.remove(productDao.findByShortName("WS"));
        products.remove(productDao.findByShortName("HR Service Delivery"));
        products.remove(productDao.findByShortName("Infor10 SoftBrands HMS"));
        
        ArrayList<TrialInstanceUpdateDto> updateDtos = new ArrayList<TrialInstanceUpdateDto>();
        for (User user : users) {
            for (Product product : products) {

                TrialInstanceUpdateDto dtoIn = new TrialInstanceUpdateDto();

                dtoIn.setUserName(user.getUsername());
                dtoIn.setDaysToExtend(daysToExtend);
                dtoIn.setProductId(product.getId());

                updateDtos.add(dtoIn);
            }
        }

        extendTrialWithDtoList("byUser", daysOutStart, updateDtos.size(), updateDtos);


    }

    @Test
    public void testExtendTrialByUserNameAndProductShortName() {

        int daysToExtend = 15;
        int daysOutStart = 31;

        List<User> users = addUsersAndLaunchTrials();
        List<Product> products = productDao.findByTrialsAvailable(true);
        //TODO WS does not have trials
        products.remove(productDao.findByShortName("WS"));
        products.remove(productDao.findByShortName("HR Service Delivery"));
        products.remove(productDao.findByShortName("Infor10 SoftBrands HMS"));
        
        ArrayList<TrialInstanceUpdateDto> updateDtos = new ArrayList<TrialInstanceUpdateDto>();
        for (User user : users) {
            for (Product product : products) {

                TrialInstanceUpdateDto dtoIn = new TrialInstanceUpdateDto();

                dtoIn.setUserName(user.getUsername());
                dtoIn.setDaysToExtend(daysToExtend);
                dtoIn.setProductShortName(product.getShortName());

                updateDtos.add(dtoIn);
            }
        }

        extendTrialWithDtoList("byUser", daysOutStart, updateDtos.size(), updateDtos);


    }

    private static HashMap<String, Integer> countDomains(List<User> users) {

        HashMap<String, Integer> map = new HashMap<String, Integer>();
        for (User user : users) {
            String key = user.getUsername().split("@")[1];

            Integer val = map.get(key);
            if (val == null || val < 1) {
                val = 1;
            } else {
                val++;
            }
            map.put(key, val);
        }

        return map;
    }


    @Test
    public void testExtendTrialByDomainAndProductId() {

        int daysOutStart = 31;
        //checking default here
        int daysToExtend = 30;

        List<User> users = addUsersAndLaunchTrials();

        //domain keyed count of users
        HashMap<String, Integer> domainMap = countDomains(users);
        assertEquals("Domain map size", 3, domainMap.size());
        List<Product> products = productDao.findByTrialsAvailable(true);
        //TODO WS does not have trials
        products.remove(productDao.findByShortName("WS"));
        products.remove(productDao.findByShortName("HR Service Delivery"));
        products.remove(productDao.findByShortName("Infor10 SoftBrands HMS"));
        
        for (String domain : domainMap.keySet()) {

            ArrayList<TrialInstanceUpdateDto> updateDtos = new ArrayList<TrialInstanceUpdateDto>();
            for (Product product : products) {

                TrialInstanceUpdateDto updateDto = new TrialInstanceUpdateDto();
                updateDto.setDomain(domain);
                updateDto.setProductId(product.getId());
                updateDtos.add(updateDto);
            }

            int expectedDifference = products.size() + (products.size() * domainMap.get(domain));

            extendTrialWithDtoList("byDomain", daysOutStart, expectedDifference, updateDtos);
        }


    }

    @Test
    public void testExtendTrialByDomainAndProductShortName() {
        int daysOutStart = 31;
        //checking default here
        int daysToExtend = 30;

        List<User> users = addUsersAndLaunchTrials();

        //domain keyed count of users
        HashMap<String, Integer> domainMap = countDomains(users);
        assertEquals("Domain map size", 3, domainMap.size());
        List<Product> products = productDao.findByTrialsAvailable(true);
        //TODO WS does not have trials
        products.remove(productDao.findByShortName("WS"));
        products.remove(productDao.findByShortName("HR Service Delivery"));
        products.remove(productDao.findByShortName("Infor10 SoftBrands HMS"));
        
        for (String domain : domainMap.keySet()) {

            ArrayList<TrialInstanceUpdateDto> updateDtos = new ArrayList<TrialInstanceUpdateDto>();
            for (Product product : products) {

                TrialInstanceUpdateDto updateDto = new TrialInstanceUpdateDto();
                updateDto.setDomain(domain);
                updateDto.setProductShortName(product.getShortName());
                updateDtos.add(updateDto);
            }

            int expectedDifference = products.size() + (products.size() * domainMap.get(domain));

            extendTrialWithDtoList("byDomain", daysOutStart, expectedDifference, updateDtos);
        }


    }


    @Test
    public void testExtendTrialByGuid() {

        int daysToExtend = 30;
        int daysOutStart = 31;

        addUsersAndLaunchTrials();
        for (String guid : trialInstanceDao.findAllGuids()) {

            TrialInstanceUpdateDto updateDto = new TrialInstanceUpdateDto();
            updateDto.setGuid(guid);

            extendSingleTrialInstanceUpdateDto("byGuid", daysOutStart, 1, updateDto);

        }


    }

    @Test
    public void testExtendAllTrials() {

        int daysOutStart = 31;
        int daysToExtend = 15;

        TrialInstanceUpdateDto updateDto = new TrialInstanceUpdateDto();
        updateDto.setDaysToExtend(daysToExtend);
        addUsersAndLaunchTrials();

        extendSingleTrialInstanceUpdateDto("allTrials", daysOutStart, trialInstanceDao.count(), updateDto);

    }

    @Test
    public void testExtendAllInforByProductId() {

        int daysOutStart = 31;
        //checking default here
        int daysToExtend = 30;

        List<User> users = addUsersAndLaunchTrials();

        //domain keyed count of users
        HashMap<String, Integer> domainMap = countDomains(users);
        assertEquals("Domain map size", 3, domainMap.size());
        List<Product> products = productDao.findByTrialsAvailable(true);
        //TODO WS does not have trials
        products.remove(productDao.findByShortName("WS"));
        products.remove(productDao.findByShortName("HR Service Delivery"));
        products.remove(productDao.findByShortName("Infor10 SoftBrands HMS"));
        ArrayList<TrialInstanceUpdateDto> updateDtos = new ArrayList<TrialInstanceUpdateDto>();

        for (Product product : products) {
            TrialInstanceUpdateDto updateDto = new TrialInstanceUpdateDto();

            updateDto.setProductId(product.getId());
            updateDtos.add(updateDto);
        }

        int expectedDifference = products.size() + (products.size() * domainMap.get(StringDefs.INFOR_DOMAIN));

        extendTrialWithDtoList("allInfor", daysOutStart, expectedDifference, updateDtos);

    }

    @Test
    public void testExtendAllInforByProductShortName() {
        int daysOutStart = 31;
        //checking default here
        int daysToExtend = 30;

        List<User> users = addUsersAndLaunchTrials();

        //domain keyed count of users
        HashMap<String, Integer> domainMap = countDomains(users);
        assertEquals("Domain map size", 3, domainMap.size());
        List<Product> products = productDao.findByTrialsAvailable(true);
        //TODO WS does not have trials
        products.remove(productDao.findByShortName("WS"));
        products.remove(productDao.findByShortName("HR Service Delivery"));
        products.remove(productDao.findByShortName("Infor10 SoftBrands HMS"));
        
        ArrayList<TrialInstanceUpdateDto> updateDtos = new ArrayList<TrialInstanceUpdateDto>();

        for (Product product : products) {
            TrialInstanceUpdateDto updateDto = new TrialInstanceUpdateDto();

            updateDto.setProductShortName(product.getShortName());
            updateDtos.add(updateDto);
        }

        int expectedDifference = products.size() + (products.size() * domainMap.get(StringDefs.INFOR_DOMAIN));
        extendTrialWithDtoList("allInfor", daysOutStart, expectedDifference, updateDtos);
    }

    private Exception confirmCorrectCSWebApplicationException(CSWebApplicationException except, String match) {

        Object entity = except.getResponse().getEntity();

        if (entity != null && entity.toString().contains(match)) {
            return except;
        }

        return new Exception("Expected CSWebApplicationException containing string '" + match + "', but it did not:\n" + entity);

    }

    private void testBadTrialInstanceExtension(String extendType, String matchException, TrialInstanceUpdateDto trialInstanceUpdateDto) throws Exception {
        loginAdminUser();
        try {
            trialService.extendTrialExpiration(extendType, trialInstanceUpdateDto);
        } catch (CSWebApplicationException exception) {

            throw confirmCorrectCSWebApplicationException(exception, matchException);
        }
    }

    @Test(expected = CSWebApplicationException.class)
    public void testBadTrialExtensionCallWithBadExtendType() throws Exception {

        TrialInstanceUpdateDto dto = new TrialInstanceUpdateDto();
        testBadTrialInstanceExtension("extendMeOrDie", "No enum constant", dto);

    }


    @Test(expected = IllegalArgumentException.class)
    public void testBadTrialExtensionCallWithNullExtendType() throws Exception {

        TrialInstanceUpdateDto dto = new TrialInstanceUpdateDto();
        loginAdminUser();
        trialService.extendTrialExpiration(null, dto);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadTrialExtensionCallWithNullDto() throws Exception {


        loginAdminUser();
        trialService.extendTrialExpiration("extendAll", null);

    }

    @Test(expected = CSWebApplicationException.class)
    public void testBadTrialExtensionWithBadUserId() throws Exception {

        TrialInstanceUpdateDto dto = new TrialInstanceUpdateDto();
        dto.setProductId(productDao.findByTrialsAvailable(true).get(0).getId());
        dto.setUserId(-42L);
        testBadTrialInstanceExtension("byUser", "User does not exist", dto);
    }

    @Test(expected = CSWebApplicationException.class)
    public void testBadTrialExtensionWithBadUsername() throws Exception {

        TrialInstanceUpdateDto dto = new TrialInstanceUpdateDto();
        dto.setProductId(productDao.findByTrialsAvailable(true).get(0).getId());
        dto.setUserName("bobvila@thisoldhouse.com");
        testBadTrialInstanceExtension("byUser", "User does not exist", dto);

    }

    @Test(expected = CSWebApplicationException.class)
    public void testBadTrialExtensionWithNullUserNameAndId() throws Exception {
        TrialInstanceUpdateDto dto = new TrialInstanceUpdateDto();
        dto.setProductId(productDao.findByTrialsAvailable(true).get(0).getId());
        dto.setUserName(null);
        dto.setUserId(null);
        testBadTrialInstanceExtension("byUser", "User does not exist", dto);

    }

    @Test(expected = CSWebApplicationException.class)
    public void testBadTrialExtensionWithBadProductId() throws Exception {

        TrialInstanceUpdateDto dto = new TrialInstanceUpdateDto();
        dto.setProductId(-42L);
        dto.setUserId(userDao.findAll().get(0).getId());
        testBadTrialInstanceExtension("byUser", "Product does not exist", dto);

    }

    @Test(expected = CSWebApplicationException.class)
    public void testBadTrialExtensionWithBadProductShortName() throws Exception {
        TrialInstanceUpdateDto dto = new TrialInstanceUpdateDto();
        dto.setProductShortName("BUBBA");
        dto.setUserId(userDao.findAll().get(0).getId());
        testBadTrialInstanceExtension("byUser", "Product does not exist", dto);

    }

    @Test(expected = CSWebApplicationException.class)
    public void testBadTrialExtensionWithNullProductShortNameAndId() throws Exception {
        TrialInstanceUpdateDto dto = new TrialInstanceUpdateDto();
        dto.setProductShortName(null);
        dto.setProductId(null);
        dto.setUserId(userDao.findAll().get(0).getId());
        testBadTrialInstanceExtension("byUser", "Product does not exist", dto);

    }

    @Test(expected = CSWebApplicationException.class)
    public void testBadTrialExtensionWithBadProductIdByDomain() throws Exception {
        TrialInstanceUpdateDto dto = new TrialInstanceUpdateDto();
        dto.setProductId(-1L);
        dto.setDomain("infor.com");
        testBadTrialInstanceExtension("byDomain", "Product does not exist", dto);

    }

    @Test(expected = CSWebApplicationException.class)
    public void testBadTrialExtensionWithEmptyDomainString() throws Exception {

        TrialInstanceUpdateDto dto = new TrialInstanceUpdateDto();
        dto.setProductId(productDao.findByTrialsAvailable(true).get(0).getId());
        dto.setDomain("");
        testBadTrialInstanceExtension("byDomain", "Domain value empty", dto);
    }

    @Test(expected = CSWebApplicationException.class)
    public void testBadTrialExtensionWithNullDomainString() throws Exception {

        TrialInstanceUpdateDto dto = new TrialInstanceUpdateDto();
        dto.setProductId(productDao.findByTrialsAvailable(true).get(0).getId());
        dto.setDomain(null);
        testBadTrialInstanceExtension("byDomain", "Domain value empty", dto);
    }

    @Test(expected = CSWebApplicationException.class)
    public void testBadTrialExtensionWithBadGuid() throws Exception {

        TrialInstanceUpdateDto dto = new TrialInstanceUpdateDto();
        dto.setGuid("bob's big fun house");
        testBadTrialInstanceExtension("byGuid", "GUID does not exist!", dto);
    }


    private static final String[] USER_LIST = new String[]{"myinfor1@infor.com|Infor|One", "myinfor2@infor.com|Infor|Two", "myinfor3@infor.com|Infor|Three", "mysomeone1@somecompany.com|Someone|One", "mysomeone2@somecompany.com|Someone|Two", "mysomeone3@somecompany.com|Someone|Three", "mysomeone@myothercompany.com|Someone|Else"};

    private static final String USER_LIST_PASSWORD = "password";

    private ArrayList<User> addUsersAndLaunchTrials() {

        trialEmailComponent.setEmailProvider(new NullEmailProvider());
        return addUsersAndLaunchTrials(USER_LIST, USER_LIST_PASSWORD);

    }

    private ArrayList<User> addUsersAndLaunchTrials(String[] user_list, String password) {
        long startCountUsers = userDao.count();
        long startTrialInstanceCount = trialInstanceDao.count();

        settingsProvider.setProductionMode(true);
        settingsProvider.setTrialRecycleAllDomains(true);
        settingsProvider.setTrialRecycleInforDomain(true);
        Region region = regionDao.getReference(2L);
        List<Product> products = productDao.findByTrialsAvailable(true);
        int startProdCount=products.size();
        HashSet<String> domainSet = new HashSet<String>();
        products.remove(productDao.findByShortName("WS"));
        products.remove(productDao.findByShortName("HR Service Delivery"));
        products.remove(productDao.findByShortName("Infor10 SoftBrands HMS"));
        int productsWithTrialsAvailableButNot=startProdCount-products.size();
        
        ArrayList<User> usersToReturn = new ArrayList<User>();

        for (String userString : user_list) {


            String[] userData = userString.split("\\|");

            //easy counting no matter how many users we add to test
            domainSet.add(userData[0].split("@")[1]);

            User user = createUser(userData[0], userData[1], userData[2], password, Role.ROLE_EXTERNAL);
            userDao.save(user);
            usersToReturn.add(user);
     
            
            for (Product product : products) {
            
            trialService.launchTrial(getRequestStub(), deploymentServiceComponent.latestProductVersion(product.getProductVersions()), user, region, Locale.US);
            
            }
        }


        assertEquals("User count test", userDao.count() - startCountUsers, user_list.length);                                             //TODO WS does not have trials
        assertEquals("Trial instance count should be (user_list*PRODUCTS)+(PRODUCTS*DOMAINS)", ((productDao.findByTrialsAvailable(true).size()- productsWithTrialsAvailableButNot) * user_list.length) + ((productDao.findByTrialsAvailable(true).size()- productsWithTrialsAvailableButNot) * domainSet.size()), trialInstanceDao.count() - startTrialInstanceCount);
        assertEquals("Users to return array should be same size as 'user_list'", user_list.length, usersToReturn.size());

        return usersToReturn;
    }
}
