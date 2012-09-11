package com.infor.cloudsuite.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.infor.cloudsuite.dao.CompanyDao;
import com.infor.cloudsuite.dao.DeploymentStackDao;
import com.infor.cloudsuite.dao.IndustryDao;
import com.infor.cloudsuite.dao.UserDao;
import com.infor.cloudsuite.dao.UserToUserDao;
import com.infor.cloudsuite.dao.UserTrackingDao;
import com.infor.cloudsuite.dto.AdminUserDto;
import com.infor.cloudsuite.dto.DeploymentStackDto;
import com.infor.cloudsuite.dto.LoginAgg;
import com.infor.cloudsuite.dto.PasswordResetDto;
import com.infor.cloudsuite.dto.UserCreateDto;
import com.infor.cloudsuite.dto.UserLeadStatusUpdateDto;
import com.infor.cloudsuite.dto.UserToUserOpType;
import com.infor.cloudsuite.dto.UserToUserUpdateDto;
import com.infor.cloudsuite.entity.*;
import com.infor.cloudsuite.platform.CSWebApplicationException;
import com.infor.cloudsuite.platform.security.SecurityService;
import com.infor.cloudsuite.service.component.DeploymentServiceComponent;
import com.infor.cloudsuite.service.component.UserServiceComponent;

@Path("/useradmin")
@Service
public class UserAdminService {

	@Resource
	private DeploymentService deploymentService;
	@Resource
	private PasswordResetService passwordResetService;
	
	@Resource
	private UserServiceComponent userServiceComponent;
	@Resource
	private UserDao userDao;
	@Resource
	private UserToUserDao userToUserDao;
	@Resource 
	private CompanyDao companyDao;
	@Resource
	private IndustryDao industryDao;
	@Resource
	private SecurityService securityService;
	@Resource
	private UserTrackingDao userTrackingDao;
	@Resource
	private DeploymentStackDao deploymentStackDao;
	@Resource
	DeploymentServiceComponent deploymentServiceComponent;

    @POST
    @Path("/createNewUser")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional(propagation = Propagation.REQUIRED)
    @Secured(StringDefs.ROLE_SALES)
    public AdminUserDto createNewUser(@Context UriInfo uriInfo, @Context HttpServletRequest request,UserCreateDto userCreateDto) {
	
    	
    	
    	if (userDao.findByUsername(userCreateDto.getEmail()) != null) {
    		throw new CSWebApplicationException(StringDefs.GENERAL_ERROR_CODE,"User with email '"+userCreateDto.getEmail()+"' exists!");
    	}
    
    	Company company;
    	if (userCreateDto.getCompanyId() != null && userCreateDto.getCompanyId() > 0) {
    		company=companyDao.findById(userCreateDto.getCompanyId());
    	} else {
    		company=new Company();
    		company.setIndustry(industryDao.findById(userCreateDto.getIndustryId()));
    		company.setName(userCreateDto.getCompanyName());
    		company.setInforId(userCreateDto.getInforId());
    		companyDao.save(company);
    		companyDao.flush();
    	}
    	

    	User user=new User();
    	user.setActive(true);
    	user.setAddress1(userCreateDto.getAddress1());
    	user.setAddress2(userCreateDto.getAddress2());
    	user.setCountry(userCreateDto.getCountry());
    	user.setCompany(company);
    	Date createdAt=new Date(); //now
    	user.setCreatedAt(createdAt);
    	user.setUpdatedAt(createdAt);
    	
    	user.setFirstName(userCreateDto.getFirstName());
    	user.setLastName(userCreateDto.getLastName());
    	user.setLanguage(userCreateDto.getLanguage());
    	user.setInforCustomer(userCreateDto.getInforCustomer());
    	user.setPassword(securityService.encodePassword("p@$$w0rd", createdAt));
    	user.setUsername(userCreateDto.getEmail());
    	user.setPhone(userCreateDto.getPhone());
    	if (userCreateDto.getRole() != null && securityService.getCurrentUser().isAdmin()) {
    		user.getRoles().add(Role.valueOf(userCreateDto.getRole())); 
    	} else {
    		user.getRoles().add(Role.ROLE_EXTERNAL);
    	}
    	userDao.save(user);
    	userDao.flush();

    	trackAction(user,TrackingType.USER_CREATE);
    	
    	PasswordResetDto prd=new PasswordResetDto();
    	prd.setCreate(true);
    	prd.setEmail(user.getUsername());
    	
    	passwordResetService.resetUserPassword(uriInfo, request, prd);
    	
    	trackAction(user,TrackingType.PASSWORD_SET_NOTIFICATION);
    	
    	
    	return getAdminUserDto(user);
    	
    	
    }

    private AdminUserDto getAdminUserDto(User user) {
    	List<DeploymentStack> deployments=deploymentStackDao.findByUserWithStateNotIn(user, DeploymentState.DEFAULT_EXCLUDES);
        LoginAgg agg = userTrackingDao.getLoginAgg(user.getId());
        
        if (agg == null) {
            agg = new LoginAgg(user.getId(), 0L, user.getCreatedAt());
        }
        
        return new AdminUserDto(user,deploymentServiceComponent.getDtoListForStacks(deployments),agg);
    }

    @POST
    @Path("/updateLeadStatus")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional(propagation = Propagation.REQUIRED)
    @Secured(StringDefs.ROLE_SALES)
    public AdminUserDto updateLeadStatus(UserLeadStatusUpdateDto ulsuDto) {
    	

    	User user=userDao.findById(ulsuDto.getUserId());
    	if (user == null) {
    		throw new CSWebApplicationException(StringDefs.GENERAL_ERROR_CODE,"User does not exist!");
    		
    	}

    	String oldLeadStatus=user.getLeadStatus().name();
    	try {
    		user.setLeadStatus(LeadStatus.valueOf(ulsuDto.getLeadStatus()));
    	
    	} catch (IllegalArgumentException iae) {
    		throw new CSWebApplicationException(StringDefs.GENERAL_ERROR_CODE,"LeadStatus value "+ulsuDto.getLeadStatus()+" is BAD!");
    	}
    	
    	userDao.save(user);
    	userDao.flush();
    	
    	trackAction(user, TrackingType.USER_UPDATE_LEADSTATUS,"username:"+user.getUsername()+",lead status changed from '"+oldLeadStatus+"' to '"+user.getLeadStatus().name()+"'");
    	return getAdminUserDto(user);
    	
    }
    
    @POST
    @Path("/updateUser")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional(propagation = Propagation.REQUIRED)
    @Secured(StringDefs.ROLE_SALES)
    public AdminUserDto updateUser(UserCreateDto userDto) {
    	User user=userDao.findById(userDto.getId());
    	
    	if (user==null) {
    		throw new CSWebApplicationException(StringDefs.GENERAL_ERROR_CODE,"User does not exist!");
    	}
    	
    	user.setAddress1(userDto.getAddress1());
    	user.setAddress2(userDto.getAddress2());
    	user.setCountry(userDto.getCountry());
    	user.setLanguage(userDto.getLanguage());
    	user.setLastName(userDto.getLastName());
    	user.setFirstName(userDto.getFirstName());
    	user.setPhone(userDto.getPhone());
    	Company existingCompany=user.getCompany();
    	Long existingCompanyId=-2L;
    	if (existingCompany != null) {
    		existingCompanyId=existingCompany.getId();
    	}
    	
    	//user.setCompanyName(userDto.getCompanyName());
    	Company company;
    	if (userDto.getCompanyId()!=null && userDto.getCompanyId() > 0) {
    		company=companyDao.findById(userDto.getCompanyId());
    	} else {
    	
    		company=new Company();
    		Industry industry;
    		if (userDto.getIndustryId()!=null) {
    			industry=industryDao.findById(userDto.getIndustryId());
    		} else {
    			industry=industryDao.findByName("Unknown");
    		}
    		
    		company.setIndustry(industry);
    		company.setInforId(userDto.getInforId());
    		company.setName(userDto.getCompanyName());
    		company.setNotes("Company created by sales/admin user");
    		companyDao.save(company);
    		companyDao.flush();
    	}
    		
    	if (!company.getId().equals(existingCompanyId))
    	{
    		user.setCompany(company);
    		
    	}
    	

	
    	user.setFirstName(userDto.getFirstName());
    	user.setLastName(userDto.getLastName());
    	user.setInforCustomer(userDto.getInforCustomer());
    	user.setLanguage(userDto.getLanguage());
    	user.setPhone(userDto.getPhone());
    	user.setUpdatedAt(new Date());
    	if (userDto.getRole() != null && securityService.getCurrentUser().isAdmin()) {
    		user.getRoles().clear();
    		user.getRoles().add(Role.valueOf(userDto.getRole())); 
    	} else {
    		user.getRoles().add(Role.ROLE_EXTERNAL);
    	}
    	userDao.save(user);
    	userDao.flush();

    	if (existingCompany != null) {
    		if (userDao.countUsersForCompanyId(existingCompanyId)<1) {
    			companyDao.delete(existingCompany);
    			companyDao.flush();
    		}
    	}
    	trackAction(user,TrackingType.USER_UPDATE);
    	return getAdminUserDto(user);
    }
    
    @POST
    @Path("/updateUserToUser")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional(propagation = Propagation.REQUIRED)
    @Secured(StringDefs.ROLE_SALES)
    public AdminUserDto updateUserToUser(UserToUserUpdateDto updateDto) {
    	
    	if (updateDto.getUserId().equals(updateDto.getRelatedUserId())) {
    		throw new CSWebApplicationException(StringDefs.GENERAL_ERROR_CODE,"Cannot assign user to self!");
    	}
    	
    	User user=userDao.findById(updateDto.getUserId());
    	if (user == null) {
    		throw new CSWebApplicationException(StringDefs.GENERAL_ERROR_CODE,"User does not exist");
    		
    	}
    	
    	if (userDao.findById(updateDto.getRelatedUserId())==null) {
    		throw new CSWebApplicationException(StringDefs.GENERAL_ERROR_CODE,"Related user does not exist!!");
    	}
    	
    	UserToUser utu=userToUserDao.findByUserIdAndTargetUserIdAndRelationType(user.getId(),updateDto.getRelatedUserId(),updateDto.getRelationType());
  
    	if (updateDto.getOpType() == UserToUserOpType.RELATE) {
    		
    		if (utu == null) {
    			utu=new UserToUser(user.getId(),updateDto.getRelatedUserId(),updateDto.getRelationType());
    			userToUserDao.save(utu);
    		}
    		
    	} else if (updateDto.getOpType() == UserToUserOpType.UNRELATE) {
    		if (utu != null) {
    			userToUserDao.delete(utu);
    		}
    	}
    	
    	if (utu != null) {
    		userToUserDao.flush();
    	}

    	trackAction(user,TrackingType.USER_TO_USER_MAP,"added to username:"+user.getUsername());

    	return getAdminUserDto(user);
    	
    }
    @GET
    @Path("/getUsersWithLeadStatus")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional(propagation = Propagation.REQUIRED)
    @Secured(StringDefs.ROLE_SALES)
    public List<AdminUserDto> getUsersWithLeadStatus(@QueryParam("leadStatus") LeadStatus leadStatus) {
    	
    	
    	List<User> users=userDao.findByLeadStatus(leadStatus);
    	
    	ArrayList<AdminUserDto> userDtos=new ArrayList<>();
    	for (User user : users) {
    		userDtos.add(getAdminUserDto(user));
    	}
    	
    	
    	
    	return userDtos;
    	
    }
    
    @GET
    @Path("/getUsersByIdList")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional(propagation = Propagation.REQUIRED)
    @Secured(StringDefs.ROLE_SALES)
    public List<AdminUserDto> getUsersByIdSetList(@QueryParam("idList") Set<Long> listSet) {
    	
    	ArrayList<AdminUserDto> userDtos=new ArrayList<>();
    	for (Long id : listSet) {
    		User user=userDao.findById(id);
    		if (user != null) {
    			userDtos.add(getAdminUserDto(user));
    		}
    	}
    	
    	return userDtos;
    }
    
    @GET
    @Path("/getUserDtoById")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional(propagation = Propagation.REQUIRED)
    @Secured(StringDefs.ROLE_SALES)
    public AdminUserDto getUserDtoById(@QueryParam("id") Long id) {

  
    	User user=userDao.findById(id);
    	if (user==null) {
    		throw new CSWebApplicationException(StringDefs.GENERAL_ERROR_CODE,"User with id '"+id+"' does not exist!");
    		
    	}
 
    	return getAdminUserDto(user);
    }
    
    
    private void trackAction(User target, TrackingType type) {
    	trackAction(target,type,null);
    }
    private void trackAction(User target, TrackingType type, String other) {
    	UserTracking track=new UserTracking();
    	track.setTargetObject(target.getId());
    	track.setUser(getLoggedInUser());
    	track.setTrackingType(type);
    	track.setTimestamp(new Date());
    	if (other == null) {
    		track.setOtherData("username:"+target.getUsername());
    	} else {
    		track.setOtherData(other);
    	}
    	userTrackingDao.save(track);
    	userTrackingDao.flush();
    }
    
    private User getLoggedInUser() {
    	return userDao.findById(securityService.getCurrentUser().getId());
    	
    }
    
    
    @Path("/getDeployments")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(StringDefs.ROLE_SALES)
    public List<DeploymentStackDto> getDeployments(@QueryParam("userId") Long userId) {

    	User user=userDao.findById(userId);
    	
        return userServiceComponent.getDeployments(user);
    }
}
