package com.infor.cloudsuite.service;

import static org.junit.Assert.*;
import javax.servlet.http.*;
import org.springframework.mock.web.*;
import javax.annotation.Resource;
import javax.ws.rs.core.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.infor.cloudsuite.AbstractTest;
import com.infor.cloudsuite.entity.*;
import com.infor.cloudsuite.service.component.DeploymentServiceComponent;
import com.infor.cloudsuite.service.component.ImportExportComponent;
import com.infor.cloudsuite.service.component.ImportFileTypeEnum;
import com.infor.cloudsuite.dto.*;
import com.infor.cloudsuite.dao.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Transactional(propagation = Propagation.REQUIRED)
public class UserAdminServiceTest extends AbstractTest {

	@Resource
	UserAdminService userAdminService;
	@Resource
	ProductDao productDao;
	@Resource
	AmazonCredentialsDao amazonCredentialsDao;
	@Resource
	IndustryDao industryDao;
	@Resource
	CompanyDao customerDao;
	@Resource
	UserDao userDao;
	@Resource
	ImportExportComponent importExportComponent; 
	@Resource
	UserTrackingDao userTrackingDao;
	@Resource
	DeploymentStackDao deploymentStackDao;
	@Resource
	DeploymentServiceComponent deploymentServiceComponent;
	
	HttpServletRequest request;
	UriInfo uriInfo;
	
	@Before
	public void setUp() {
        HttpSession session = new MockHttpSession();
        request = getRequestStub(session);
        uriInfo = getUriInfoStub();
        
	}
	
	
	@Test
	public void testAddUserAndNewCustomer() throws Exception {
	
		loginAdminUser();
		long trackCount=userTrackingDao.count();
		UserCreateDto userCreateDto=getTestUserCreateDto();
		userCreateDto.setCompanyName("N00Customer");
		userCreateDto.setInforId("fakeCustomerId-1");
		
		long customerSize=customerDao.count();
		long userSize=userDao.count();
		
		AdminUserDto userDto=userAdminService.createNewUser(uriInfo,request,userCreateDto);
		assertNotNull(userDto);
		assertNotNull(userDto.getId());
		
		assertEquals("Customer size should have increased by 1",customerSize+1,customerDao.count());
		assertEquals("User size should have increased by 1",userSize+1,userDao.count());
		
		assertEquals("User tracking size should have increased by 2",trackCount+2,userTrackingDao.count());
		
	}
	private Industry getIndustry() {
		Industry industry=industryDao.findByName("Other");
		if (industry==null) {
			industry=industryDao.findAll().get(0);
		}
		return industry;
	}
	
	private UserCreateDto getTestUserCreateDto() {
		
		
		UserCreateDto userCreateDto=new UserCreateDto();
		
		userCreateDto.setEmail("billybob@thornton.com");
		userCreateDto.setAddress1("address1");
		userCreateDto.setAddress2("address2");
		userCreateDto.setCountry("US");
		userCreateDto.setLanguage("en");
		userCreateDto.setFirstName("Billy Bob");
		userCreateDto.setLastName("Thornton");

		userCreateDto.setIndustryId(getIndustry().getId());
		userCreateDto.setInforCustomer(true);
		userCreateDto.setInforId("someInforId-1");
		userCreateDto.setPhone("1-900-MIX-ALOT");
		userCreateDto.setRole(Role.ROLE_SALES.name());
		return userCreateDto;
	}
	
	@Test
	public void testAddUserWithExistingCustomer() throws Exception {
	
		loginAdminUser();
		long trackCount=userTrackingDao.count();
		Company customer=new Company();
		customer.setName("OLD Customer");
		customer.setIndustry(getIndustry());
		customer.setInforId("fakeCustomerId-0");
		customerDao.save(customer);
		customerDao.flush();
		assertNotNull(customer.getId());
		
		UserCreateDto userCreateDto=getTestUserCreateDto();
		userCreateDto.setCompanyId(customer.getId());
		
		long customerSize=customerDao.count();
		long userSize=userDao.count();
		
		AdminUserDto userDto=userAdminService.createNewUser(uriInfo,request,userCreateDto);
		assertNotNull(userDto);
		assertNotNull(userDto.getId());
		
		assertEquals("Customer size should NOT have increased",customerSize,customerDao.count());
		assertEquals("User size should have increased by 1",userSize+1,userDao.count());
	
		assertEquals("User tracking size should have increased by 2",trackCount+2,userTrackingDao.count());
		//added as admin, role sales should take.
		assertEquals(Role.ROLE_SALES.name(),userDto.getRole());
	}
	
	@Test
	public void testLeadStatusServices() throws Exception {
		
		loginAdminUser();
		testAddUserAndNewCustomer();
		
		
		User user=userDao.findByUsername("billybob@thornton.com");
		assertEquals(LeadStatus.NONE,user.getLeadStatus());
		
		List<AdminUserDto> usersWithNone=userAdminService.getUsersWithLeadStatus(LeadStatus.NONE);
		assertEquals(5,usersWithNone.size());
		long trackCount=userTrackingDao.count();
		UserLeadStatusUpdateDto ulsuDto=new UserLeadStatusUpdateDto(user.getId(),LeadStatus.QUALIFIED.name());
		
		AdminUserDto billy=userAdminService.updateLeadStatus(ulsuDto);
		assertNotNull(billy);
		assertEquals("User tracking should increase by 1",trackCount+1,userTrackingDao.count());
		trackCount++;
		
		List<AdminUserDto> usersWithQual=userAdminService.getUsersWithLeadStatus(LeadStatus.QUALIFIED);
		assertEquals(1,usersWithQual.size());
		
		ulsuDto.setLeadStatus(LeadStatus.DISQUALIFIED.name());
		billy=userAdminService.updateLeadStatus(ulsuDto);
		assertNotNull(billy);
		assertEquals("User tracking should increase by 1",trackCount+1,userTrackingDao.count());
		trackCount++;
		List<AdminUserDto> usersWithDisQual=userAdminService.getUsersWithLeadStatus(LeadStatus.DISQUALIFIED);
		
		assertEquals(1,usersWithDisQual.size());
		
		
	}

	List<User> findAusers() {
		ArrayList<User> list=new ArrayList<User>();
		
		List<User> all=userDao.findAll();
		for (User user : all) {
			if (user.getUsername().contains("auser")) {
				list.add(user);
			}
		}
		
		
		return list;
	}
	
	List<AdminUserDto> findAuserDtos() {
		ArrayList<AdminUserDto> list=new ArrayList<AdminUserDto>();
		
		List<User> all=userDao.findAll();
		for (User user : all) {
			if (user.getUsername().contains("auser")) {
				list.add(getAdminUserDto(user));
			}
		}
		
		
		return list;
	}
    private AdminUserDto getAdminUserDto(User user) {
    	List<DeploymentStack> deployments=deploymentStackDao.findByUserWithStateNotIn(user, DeploymentState.DEFAULT_EXCLUDES);
        LoginAgg agg = userTrackingDao.getLoginAgg(user.getId());
        
        if (agg == null) {
            agg = new LoginAgg(user.getId(), 0L, user.getCreatedAt());
        }
        
        return new AdminUserDto(user,deploymentServiceComponent.getDtoListForStacks(deployments),agg);
    }
	
	@Test
	public void testGetUsersByStringList() {
		
		importExportComponent.importFromJsonFile("useradminservicetest.json", ImportFileTypeEnum.IN_ARCHIVE, null);
		
		login("asalesuser@infor.com","useruser");
		List<AdminUserDto> auserDtos=findAuserDtos();
		//confirm import as expected
		assertEquals(10,auserDtos.size());
		
		HashSet<Long> auserIds=new HashSet<Long>();
		for (AdminUserDto auserDto : auserDtos) {
			auserIds.add(auserDto.getId());
		}
		

	}
	
	@Test
	public void testGetUsersBySetList() {
		
		importExportComponent.importFromJsonFile("useradminservicetest.json", ImportFileTypeEnum.IN_ARCHIVE, null);
		
		login("asalesuser@infor.com","useruser");
		List<AdminUserDto> auserDtos=findAuserDtos();
		//confirm import as expected
		assertEquals(10,auserDtos.size());
		
		HashSet<Long> auserIds=new HashSet<Long>();
		for (AdminUserDto auserDto : auserDtos) {
			auserIds.add(auserDto.getId());
		}
		
		List<AdminUserDto> auserListBySet=userAdminService.getUsersByIdSetList(auserIds);
		assertEquals(auserDtos.size(),auserListBySet.size());
	}
	
	@Test
	public void testRoleCases() throws Exception{
		importExportComponent.importFromJsonFile("useradminservicetest.json", ImportFileTypeEnum.IN_ARCHIVE, null);
		
		login("asalesuser@infor.com","useruser");
		//test add a sales user.
		UserCreateDto dto=getTestUserCreateDto();
		assertEquals(Role.ROLE_SALES.name(),dto.getRole());
		AdminUserDto result=userAdminService.createNewUser(uriInfo, request, dto);
		
		assertEquals(Role.ROLE_EXTERNAL.name(),result.getRole());
		loginAdminUser();
		dto.setId(result.getId());
		result=userAdminService.updateUser(dto);
		assertEquals(Role.ROLE_SALES.name(),result.getRole());
		
		
	}
	
	@Test
	public void testUserUpdate() throws Exception {
		importExportComponent.importFromJsonFile("useradminservicetest.json", ImportFileTypeEnum.IN_ARCHIVE, null);
		
		
		User salesuser=userDao.findByUsername("asalesuser@infor.com");
		assertNotNull(salesuser);
		User salesuser2=userDao.findByUsername("asalesuser2@infor.com");
		assertNotNull(salesuser2);
		
		login("asalesuser2@infor.com","useruser");
	
		
		 // Add tested elsewhere, but want to make sure
		 // when update is done only one remains

		int count=0;
		
		long trackCount=userTrackingDao.count();
		//add
		
		assertEquals("Track count should have increased by "+count,trackCount+count,userTrackingDao.count());
		salesuser=userDao.findByUsername("asalesuser@infor.com");
		assertNotNull(salesuser);
		UserCreateDto salesuserDto=new UserCreateDto();
		salesuserDto.setId(salesuser.getId());
		
		salesuserDto.setAddress1("UPDATE ADDR1");
		salesuserDto.setAddress2("UPDATE ADDR2");
		salesuserDto.setCountry("France");
		salesuserDto.setLanguage("FR");
		salesuserDto.setFirstName("Super Awesome");
		salesuserDto.setIndustryId(getIndustry().getId());
		salesuserDto.setCompanyName("Uber partner");
		salesuserDto.setInforId("F@K3-1NF0R");
		salesuserDto.setPhone("1-900-909-JEFF");
		salesuserDto.setLastName("Sales Fiend");
		salesuserDto.setRole(Role.ROLE_SUPERADMIN.name());
		trackCount=userTrackingDao.count();
		
		AdminUserDto updated=userAdminService.updateUser(salesuserDto);
		assertNotNull(updated);
		CompanyDto custFromUpd=updated.getCompany();
		assertNotNull(custFromUpd);
		assertEquals("Uber partner",custFromUpd.getName());
		assertEquals(getIndustry().getId(),custFromUpd.getIndustryId());
		assertEquals("F@K3-1NF0R",custFromUpd.getInforId());
		
		assertEquals("Super Awesome",updated.getUser().getFirstName());
		assertEquals("FR",updated.getUser().getLanguage());
		assertEquals("France",updated.getUser().getCountry());
		assertEquals("UPDATE ADDR1",updated.getUser().getAddress1());
		assertEquals("UPDATE ADDR2",updated.getUser().getAddress2());
		assertEquals("1-900-909-JEFF",updated.getUser().getPhone());
		assertEquals("Sales Fiend",updated.getUser().getLastName());
		
		assertEquals("User Tracking count should increase by 1",trackCount+1,userTrackingDao.count());
		assertFalse(updated.getRole().equals(Role.ROLE_SUPERADMIN.name()));
		
	}

	
}
