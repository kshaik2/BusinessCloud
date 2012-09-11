package com.infor.cloudsuite.service.component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.infor.cloudsuite.dao.*;
import com.infor.cloudsuite.dto.AmazonCredentialsDto;
import com.infor.cloudsuite.dto.ContraintViolationDto;
import com.infor.cloudsuite.dto.DeploymentStackDto;
import com.infor.cloudsuite.dto.ProductUserProductDto;
import com.infor.cloudsuite.dto.ProductVersionDto;
import com.infor.cloudsuite.dto.RegistrationCompleteDto;
import com.infor.cloudsuite.dto.TrialsAndDeploymentsDto;
import com.infor.cloudsuite.dto.UserProductUpdateDto;
import com.infor.cloudsuite.dto.UserProductUpdateType;
import com.infor.cloudsuite.entity.*;
import com.infor.cloudsuite.platform.CSWebApplicationException;
import com.infor.cloudsuite.platform.components.RequestServices;
import com.infor.cloudsuite.platform.components.ValidationProvider;
import com.infor.cloudsuite.platform.security.SecurityService;
import com.infor.cloudsuite.platform.security.SecurityUser;
import com.infor.cloudsuite.service.StringDefs;

@Component
public class UserServiceComponent {
	private static final Logger logger=LoggerFactory.getLogger(UserServiceComponent.class);
    @Resource
    private UserDao userDao;
    @Resource
    private SecurityService securityService;
    @Resource
    private RequestServices requestServices;
    @Resource
    private ValidationProvider validationProvider;
    @Resource
    private ProductDao productDao;
    @Resource
    private IndustryDao industryDao;
    @Resource
    private UserProductDao userProductDao;
    @Resource
    private TrialInstanceDao trialInstanceDao;
    @Resource
    private DeploymentStackDao deploymentStackDao;
    @Resource
    private DeploymentStackLogDao deploymentStackLogDao;
    @Resource
    private AmazonCredentialsDao amazonCredentialsDao;
    @Resource
    private ProductDescriptionDao productDescriptionDao;
    @Resource
    private CompanyDao companyDao;
    @Resource ScheduleDao scheduleDao;

    public RegistrationCompleteDto updateUserSettings(HttpServletResponse response, RegistrationCompleteDto dto) {
        final ContraintViolationDto<RegistrationCompleteDto> validate = validationProvider.validate(dto);
        if (validate.isHasViolations()) {
            throw new CSWebApplicationException(StringDefs.VALIDATION_ERROR_CODE, validate);
        }
        User user = userDao.findOne(securityService.getCurrentUser().getId());
        boolean updatedPassword=false;
        if (dto.getPassword() != null) {
            String encodedPassword = securityService.encodePassword(dto.getPassword().trim(), user.getCreatedAt());
            user.setPassword(encodedPassword);
            updatedPassword=true;
        }

        user.setLanguage(dto.getLanguage());
        Company company=null;
        if (dto.getCompanyId()!=null && dto.getCompanyId()>0) {
        	company=companyDao.findById(dto.getCompanyId());
        } 
        
        if (company==null) {
        		company=new Company();
        		company.setName(dto.getCompanyName());
        		company.setIndustry(industryDao.findById(dto.getIndustryId()));
        		company.setNotes("user entered.");
        		company.setInforId(dto.getInforId());
        		companyDao.save(company);
        		companyDao.flush();
        }
        
        user.setCompany(company);
       	user.setAddress1(dto.getAddress1());
        user.setAddress2(dto.getAddress2());
        user.setPhone(dto.getPhone());
        user.setCountry(dto.getCountry());
        user.setUpdatedAt(new Date());
        userDao.save(user);
        userDao.flush();
        if (user.getLanguage() != null) {
            //logger.debug("Setting language to: " + user.getLanguage());
            securityService.getCurrentUser().setLanguage(StringUtils.parseLocaleString(user.getLanguage()));
            //logger.debug("Check language: " + securityService.getCurrentUser().getLanguage().toString());
            //logger.debug("Setting the cookie");
            requestServices.setLocaleCookie(response, user.getLanguage());
        }
        Long validationId=dto.getValidationId();
        dto=new RegistrationCompleteDto(user);
        dto.setValidationId(validationId);
        if (updatedPassword) {
        	dto.setPassword("*UP*");
        	dto.setPassword2("*UP*");
        }
        return dto;
        
    }

    
    public void activateAllUserProductsForUser(User user) {

        List<Product> products = productDao.findAll();
        for (Product product : products) {
            UserProductUpdateDto update = new UserProductUpdateDto();
            update.setType(UserProductUpdateType.DEPLOY_TYPE);
            update.setUserId(user.getId());
            update.setProductId(product.getId());
            update.setActive(true);
            updateUserProduct(update);
            update.setType(UserProductUpdateType.TRIAL_TYPE);
            updateUserProduct(update);
        }

    }


    public RegistrationCompleteDto createRegistrationCompleteDto(User user) {
        return new RegistrationCompleteDto(user);
    }

    public ProductUserProductDto updateUserProductOwned(ProductUserProductDto pupDto) {

        User user = userDao.findOne(securityService.getCurrentUser().getId());
        if (user == null) {
            throw new CSWebApplicationException(StringDefs.GENERAL_ERROR_CODE, "User not found");
        }
        UserProductUpdateDto updateDto = new UserProductUpdateDto();
        updateDto.setType(UserProductUpdateType.OWNED_TYPE);
        updateDto.setUserId(user.getId());
        updateDto.setActive(pupDto.getOwned());
        updateDto.setProductId(pupDto.getId());

        updateDto = updateUserProduct(updateDto);

        return productDao.findProductUserProductByUserIdAndProductId(user.getId(), updateDto.getProductId());
      
       
    }

    public UserProductUpdateDto updateUserProduct(UserProductUpdateDto update) {

        if (null == update.getType() ||
                null == update.getUserId() ||
                null == update.getProductId() ||
                null == update.getActive()) {
            throw new CSWebApplicationException(StringDefs.VALIDATION_ERROR_CODE, "Invalid update object.");
        }

        User userRef = userDao.getReference(update.getUserId());
        Product productRef = productDao.getReference(update.getProductId());
        UserProduct userProduct = userProductDao.findById(new UserProductKey(userRef, productRef));
        if (userProduct == null) {
            userProduct = new UserProduct(userRef, productRef);
        }

        switch (update.getType()) {
            case TRIAL_TYPE:
                userProduct.setTrialAvailable(update.getActive());
                break;
            case DEPLOY_TYPE:
                userProduct.setLaunchAvailable(update.getActive());
                break;
            case OWNED_TYPE:
                userProduct.setOwned(update.getActive());
                break;
            default:
        }
        userProductDao.save(userProduct);
        return update;
    }
    
    public boolean updateOwnedForTrialsDeployments(User user, Product product, boolean owned) {

    	try {
    		UserProduct userProduct = userProductDao.findById(new UserProductKey(user,product));
    	
    		if (userProduct == null) {
    			userProduct=new UserProduct(user,product);
    		}
    	
    		userProduct.setOwned(owned);
    		userProductDao.save(userProduct);
    		userProductDao.flush();
    	} catch (Exception e) {
    		logger.error("Encountered error updating owned for user/product",e);
    		return false;
    	}
    	
    	return true;
    }



    public List<ProductUserProductDto> addDescriptionInfo(List<ProductUserProductDto> pupDtos, Locale language) {

        for (ProductUserProductDto dto : pupDtos) {
            addProductDescriptions(dto, language);
        }
        return pupDtos;
    }

    private void addProductDescriptions(ProductUserProductDto dto, Locale language) {
        if (dto.getOwned() == null) {
            dto.setOwned(false);
        }

    	Product product = productDao.getReference(dto.getId());
        if (product==null) {
        	logger.info("addProductDescriptions(dto,language)--product == null");
        }
        CSLocale csLocale=new CSLocale(language);
        //logger.info("csLocale for product with id '"+((product==null)?"NULL":product.getId())+"':\n"+csLocale.debugOutput());
        
        final List<ProductDescription> descriptions = productDescriptionDao.findByProductAndLocale(product, csLocale);
        //logger.info("addProductDescriptions(dto,language)--descriptions.size()=="+((descriptions==null)?"NULL":descriptions.size()));
        for (ProductDescription description : descriptions) {
            dto.getDescriptions().put(description.getDescKey().name(), description.getText());
        }
    }

    public List<ProductUserProductDto> getOwnedProducts() {
        final SecurityUser user = securityService.getCurrentUser();
        if (user == null) {
        	logger.error("Null user in UserServiceComponent.getOwnedProducts()!");
            return null;
        }

        List<ProductUserProductDto> ownedDtos = productDao.findOwnedProductUserProductByUserId(user.getId());
        return addDescriptionInfo(ownedDtos, user.getLanguage());
    }


    @Transactional
    public List<ProductUserProductDto> getAllProducts() {
        final SecurityUser user = securityService.getCurrentUser();
        if (user == null) {
        	logger.error("null user in UserServiceComponent.getAllProducts()");
            return null;
        }

        List<ProductUserProductDto> allProductDtos = productDao.findProductUserProduct(user.getId());
        for (ProductUserProductDto productDto : allProductDtos) {
        	Product product=productDao.findById(productDto.getId());
        	for (ProductVersion version : product.getProductVersions()) {
        		productDto.getVersions().add(new ProductVersionDto(version));
        	}
        }
        return addDescriptionInfo(allProductDtos, user.getLanguage());
    }

    public List<AmazonCredentialsDto> getAmazonCredentials() {

        SecurityUser user = securityService.getCurrentUser();

        if (user == null) {
            throw new CSWebApplicationException(StringDefs.GENERAL_ERROR_CODE, "User not found");
        }

        return amazonCredentialsDao.getByUserId(user.getId());


    }

    @Transactional
    public AmazonCredentialsDto updateAmazonCredentials(AmazonCredentialsDto dto) {

        User user = userDao.findOne(securityService.getCurrentUser().getId());
        if (user == null) {
            throw new CSWebApplicationException(StringDefs.GENERAL_ERROR_CODE, "User not found");
        }

        AmazonCredentials credentials;
        if (dto.getId() != null) {

            credentials = amazonCredentialsDao.findById(dto.getId());
            if (credentials == null) {
                throw new CSWebApplicationException(StringDefs.GENERAL_ERROR_CODE, "Amazon Credentials not found by ID:" + dto.getId());
            }

        } else {
            credentials = new AmazonCredentials();
            credentials.setUser(user);
        }

        credentials.setName(dto.getName());
        credentials.setAwsKey(dto.getAwsKey());
        credentials.setSecretKey(dto.getSecretKey());

        amazonCredentialsDao.save(credentials);
        amazonCredentialsDao.flush();


        return new AmazonCredentialsDto(credentials);
    }

    @Transactional
    public void deleteAmazonCredentials(Long amazonCredentialsId) {

        User user = userDao.findOne(securityService.getCurrentUser().getId());
        if (user == null) {
            throw new CSWebApplicationException(StringDefs.GENERAL_ERROR_CODE, "User not found");
        }


        amazonCredentialsDao.deleteByUserAndId(user, amazonCredentialsId);

    }
    
    @Transactional
    public TrialsAndDeploymentsDto getTrialsAndDeployments(User user) {
    	
    
    	List<TrialInstance> trialInstances= trialInstanceDao.findByUserId(user.getId());
    	List<DeploymentStack> deployments= deploymentStackDao.findByUserWithStateNotIn(user, DeploymentState.DEFAULT_EXCLUDES);
    	
    	TrialsAndDeploymentsDto tadd= new TrialsAndDeploymentsDto(user,trialInstances,deployments);
    	updateDeploymentStackDtos(tadd.getDeployments());
    	return tadd;
    }

    private void updateDeploymentStackDtos(List<DeploymentStackDto> depStackDtos) {
    
    	for (DeploymentStackDto dto : depStackDtos) {
    		Date lastStartedAt=deploymentStackLogDao.getMaxDateByDeploymentStackIdAndState(dto.getId(), DeploymentState.AVAILABLE);
    		dto.setLastStartedAt((lastStartedAt==null)?dto.getCreatedAt():lastStartedAt);
    		}
    }

    @Transactional
	public List<DeploymentStackDto> getDeployments(User user) {

		List<DeploymentStack> deploymentStacks= deploymentStackDao.findByUserWithStateNotIn(user, DeploymentState.DEFAULT_EXCLUDES);
		List<TrialInstance> trialInstances=trialInstanceDao.findByUserIdAndExpirationDateGreaterThan(user.getId(), new Date());
		
		ArrayList<DeploymentStackDto> dtos=new ArrayList<>();
		for (DeploymentStack stack : deploymentStacks) {
			DeploymentStackDto dto=new DeploymentStackDto(stack);
			Date lastStartedAt=deploymentStackLogDao.getMaxDateByDeploymentStackIdAndState(dto.getId(), DeploymentState.AVAILABLE);
			dto.setLastStartedAt((lastStartedAt==null)?dto.getCreatedAt():lastStartedAt);
			
			dtos.add(dto);
		}

		for (TrialInstance trialInstance : trialInstances) {
			dtos.add(new DeploymentStackDto(trialInstance));
		}
		
		return dtos;
	}
    
}