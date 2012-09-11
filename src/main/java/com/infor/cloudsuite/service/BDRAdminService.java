package com.infor.cloudsuite.service;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.infor.cloudsuite.dao.TrialInstanceDao;
import com.infor.cloudsuite.dao.UserDao;
import com.infor.cloudsuite.dto.BDRAdminUserDto;
import com.infor.cloudsuite.dto.BdrLeadDetails;
import com.infor.cloudsuite.dto.BdrTrialInfoDto;
import com.infor.cloudsuite.entity.Role;
import com.infor.cloudsuite.entity.TrialInstance;
import com.infor.cloudsuite.entity.User;
import com.infor.cloudsuite.entity.UserProduct;
import com.infor.cloudsuite.platform.CSWebApplicationException;
import com.infor.cloudsuite.platform.components.SettingsProvider;

/**
 * User: bcrow
 * Date: 11/7/11 4:00 PM
 */
@Path("/bdradmin")
@Service
public class BDRAdminService {
    private static final Logger logger = LoggerFactory.getLogger(BDRAdminService.class);

    @Resource
    private UserDao userDao;
    @Resource
    private TrialInstanceDao trialInstanceDao;
    @Resource
    private SettingsProvider settingsProvider;
    
    
    private final Sort.Order dateOrder = new Sort.Order(Sort.Direction.DESC, "createdAt");
    private final Sort.Order lastNameOrder =new Sort.Order(Sort.Direction.ASC,"lastName");
    private final Sort.Order firstNameOrder =new Sort.Order(Sort.Direction.ASC,"firstName");
    private final Sort.Order userNameOrder =new Sort.Order(Sort.Direction.ASC,"username");

    private final Sort userSort=new Sort(dateOrder,lastNameOrder,firstNameOrder,userNameOrder);
  
    
    private final Set<Role> acceptableRoles=EnumSet.of(Role.ROLE_EXTERNAL);
 


    @GET
    @Path("/getAllLeads")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    @Secured(StringDefs.ROLE_SALES)
    public List<BDRAdminUserDto> getAllLeads(@QueryParam("pageNum") Integer pageNum,
                                             @QueryParam("numPerPage") Integer numPerPage) {

    	if (pageNum == null) {
            logger.debug("PageNum was null setting to first page");
            pageNum = 0;
        }
        if (numPerPage == null) {
            logger.debug("PageNum was null setting to 25");
            numPerPage = 25;
        }

        final PageRequest pageRequest = new PageRequest(pageNum, numPerPage, userSort);

    	List<BDRAdminUserDto> dtos;
    	
        if (settingsProvider.isExcludeInforEmailsFromLeads()){
        	dtos = userDao.findLeadsExcludeInfor(acceptableRoles,"%infor.com",pageRequest).getContent();
        } else {
        	dtos = userDao.findLeads(acceptableRoles,pageRequest).getContent();
        }

        return dtos;
    }
    
    @GET
    @Path("/getLeadDetails/{userId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(StringDefs.ROLE_SALES)
    @Transactional(readOnly = true)
    public BdrLeadDetails getLeadDetails(@PathParam(value = "userId")Long userIdDto) {
        if (userIdDto == null) {
            logger.debug("Passed userID is null.");
            throw new CSWebApplicationException(Response.Status.BAD_REQUEST, "User id passed is null");
        }
        User user = userDao.findById(userIdDto);
        if (user == null) {
            throw new CSWebApplicationException(Response.Status.BAD_REQUEST, "User not found");
        }
        BdrLeadDetails leadDetails = createLeadDetails(user);
        
        final Map<Long,UserProduct> userProducts = user.getUserProducts();
        for (Map.Entry<Long, UserProduct> productEntry : userProducts.entrySet()) {

            final List<TrialInstance> trials = trialInstanceDao.findByUserAndProductVersion_Product(user, productEntry.getValue().getProduct());
            for (TrialInstance trialInstance : trials) {
            
            	if (trialInstance != null) {
            		leadDetails.getTrials().add(new BdrTrialInfoDto(trialInstance));
            		}
            }
        }
        return leadDetails;
    }

    private BdrLeadDetails createLeadDetails(User user) {
        BdrLeadDetails leadDetails = new BdrLeadDetails();
        leadDetails.setUserName(user.getUsername());
        leadDetails.setPhone(user.getPhone());
        leadDetails.setAddress1(user.getAddress1());
        leadDetails.setAddress2(user.getAddress2());
        leadDetails.setCountry(user.getCountry());
        if (user.getCompany() != null) {
        	leadDetails.setCompanyName(user.getCompany().getName());
        }
        
        leadDetails.setInforCustomer(user.getInforCustomer());
        leadDetails.setCreatedAt(user.getCreatedAt());
        return leadDetails;
    }
}
