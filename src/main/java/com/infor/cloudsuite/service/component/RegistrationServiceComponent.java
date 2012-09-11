package com.infor.cloudsuite.service.component;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.infor.cloudsuite.dao.CompanyDao;
import com.infor.cloudsuite.dao.IndustryDao;
import com.infor.cloudsuite.dao.UserDao;
import com.infor.cloudsuite.dao.ValidationDao;
import com.infor.cloudsuite.dto.RegistrationCompleteDto;
import com.infor.cloudsuite.entity.Company;
import com.infor.cloudsuite.entity.Industry;
import com.infor.cloudsuite.entity.Role;
import com.infor.cloudsuite.entity.User;
import com.infor.cloudsuite.entity.Validation;
import com.infor.cloudsuite.entity.ValidationType;
import com.infor.cloudsuite.platform.components.EmailProvider;
import com.infor.cloudsuite.platform.components.MessageProvider;
import com.infor.cloudsuite.platform.components.RequestServices;
import com.infor.cloudsuite.platform.components.TemplateProvider;
import com.infor.cloudsuite.platform.security.SecurityService;
import com.infor.cloudsuite.service.StringDefs;

@Component
public class RegistrationServiceComponent {

	@Resource
	private ValidationDao validationDao;
	
	@Resource
	private SecurityService securityService;
	
	@Resource
	private RequestServices requestServices;
	
	@Resource
	private UserDao userDao;
	
	@Resource
	private TemplateProvider templateProvider;
	
	@Resource
	private EmailProvider emailProvider;
	
	@Resource
	private MessageProvider messageProvider;
	
	@Resource
	private UserServiceComponent userServiceComponent;
	
	@Resource
	private CompanyDao companyDao;
	
	@Resource
	private IndustryDao industryDao;
	
	
	private static final Logger logger=LoggerFactory.getLogger(RegistrationServiceComponent.class);

	public Validation registerUser(UriInfo uriInfo, Validation validation) {
	
        if (null != userDao.findByUsername(validation.getEmail())) {
            return validation;
        }

        validation.setType(ValidationType.REGISTRATION);
        validation.setCreateDate(new Date());
        String validationKey = securityService.encodePassword((validation.getEmail() + validation.getType()), validation.getCreateDate());
        validation.setValidationKey(validationKey);
        Validation oldValidation = validationDao.findByEmailAndType(validation.getEmail(), validation.getType());
        if(oldValidation != null) {
            logger.debug("Deleting prior a prior validation for {}", validation.getEmail());
            validationDao.delete(oldValidation);
            validationDao.flush();
        }
        validationDao.save(validation);
        sendValidationEmail(validation.getEmail(), validation.getFirstName(), uriInfo.getBaseUri().toString(), validation.getId(), validation.getValidationKey(), validation.getLanguage());
        return validation;
	}
	
	
	 public void completeRegistration(HttpServletResponse response, RegistrationCompleteDto complete) {
		 
		 Validation validation = validationDao.findById(complete.getValidationId());
	        validation.setCompany(complete.getCompanyName());
	        validation.setLanguage(complete.getLanguage());
	        User user = createUserFromValidation(validation,complete.getIndustryId(),complete.getInforId());
	        logger.debug("User created {}:{}", user.getId(), user.getUsername());
	        String encodedPassword = securityService.encodePassword(complete.getPassword().trim(), user.getCreatedAt());
	        logger.debug("Password: {}, encoded:{}", complete.getPassword(), encodedPassword);
	        user.setPassword(encodedPassword);
	        user.setAddress1(complete.getAddress1());
	        user.setAddress2(complete.getAddress2());
	        user.setPhone(complete.getPhone());
	        user.setCountry(complete.getCountry());

	        userDao.save(user);
	        if (StringDefs.INFOR_DOMAIN.equals(user.getUsername().split("@")[1])){
	        	user.getRoles().clear();
                user.getRoles().add(Role.ROLE_SALES);
                userServiceComponent.activateAllUserProductsForUser(user);
	        }
	        
	        logger.debug("user inserted.");
	        validationDao.delete(validation);
	        validationDao.flush();
	        logger.debug("validation deleted.");
	        securityService.fullAccessLogin(user.getUsername(), complete.getPassword());
	        if (user.getLanguage() != null) {
	            requestServices.setLocaleCookie(response, user.getLanguage());
	        }
	        logger.debug("Logged in.");
		 
	 }
	
    /**
     * Create and insert a User from Validation information
     * @param validation object
     * @return The newly created user.
     */
    private synchronized User createUserFromValidation(Validation validation,Long industryId, String inforId) {
        User user = new User();
        user.setFirstName(validation.getFirstName());
        user.setLastName(validation.getLastName());
        user.setUsername(validation.getEmail());
        if (validation.getCompany()!=null) {
        	Company company=new Company();
        	company.setInforId(inforId);
        	Industry industry;
        	if (industryId != null) {
        		industry=industryDao.findById(industryId);
       		} else {
       			industry=industryDao.findByName("Other");
       		}
       		company.setName(validation.getCompany());
       		company.setIndustry(industry);
       		company.setNotes("Company created by 'external user'");
       		companyDao.save(company);
       		companyDao.flush();
        	
        	user.setCompany(company);
        }
        user.setLanguage(validation.getLanguage());
        user.setCreatedAt(new Date());
        user.getRoles().add(Role.ROLE_EXTERNAL);
        user.setActive(true);
        return user;
    }
    
    /**
     * Send the validation email asynchronously.
     * @param address email address
     * @param firstName first name to include in the email body.
     * @param baseUri the base uri for the reply address
     * @param id validation object id
     * @param key validation key
     * @param language key for locale.
     * @return Success/Failure string(Future used to stop unittests from killing the async process before finished.
     */
    public Future<String> sendValidationEmail(String address, String firstName ,String baseUri, Long id, String key, String language) {
        final Locale locale = StringUtils.parseLocaleString(language);
        logger.debug("Sending validation email to {} with the key: {}", address, key);

        final String activationUrl = baseUri + "registration/validate/" + id + "/" + key;
        final String subject = messageProvider.getMessage(StringDefs.MESSAGE_ACTIVATION_SUBJECT, locale);
        Map<String, Object> map = new HashMap<>(2);
        map.put("firstName", firstName);
        map.put("activationUrl", activationUrl);
        String body = templateProvider.processTemplate(StringDefs.MESSAGE_ACTIVATION_TEMPATE, locale, map);
        if (body == null) {
            body = activationUrl;
        }

        return emailProvider.sendEmailAsync(address, subject, body, true);
    }

    @Resource
    public void setEmailProvider(EmailProvider emailProvider) {
        this.emailProvider = emailProvider;
    }

}
