package com.infor.cloudsuite.service.component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.infor.cloudsuite.dao.ProductDao;
import com.infor.cloudsuite.dao.ProductVersionDao;
import com.infor.cloudsuite.dao.RegionDao;
import com.infor.cloudsuite.dao.TrialEnvironmentDao;
import com.infor.cloudsuite.dao.TrialInstanceDao;
import com.infor.cloudsuite.dao.TrialProductChildDao;
import com.infor.cloudsuite.dao.UserDao;
import com.infor.cloudsuite.dao.UserProductDao;
import com.infor.cloudsuite.dto.DeployRequestDto;
import com.infor.cloudsuite.dto.DeploymentStackDto;
import com.infor.cloudsuite.dto.TrialDto;
import com.infor.cloudsuite.entity.*;
import com.infor.cloudsuite.platform.CSWebApplicationException;
import com.infor.cloudsuite.platform.components.GuidProvider;
import com.infor.cloudsuite.platform.components.RequestServices;
import com.infor.cloudsuite.platform.components.SettingsProvider;
import com.infor.cloudsuite.platform.security.SecurityService;
import com.infor.cloudsuite.service.SeedService;
import com.infor.cloudsuite.service.StringDefs;

@Component
public class TrialObjectsCreatorComponent {
    private static final Logger logger = LoggerFactory.getLogger(TrialObjectsCreatorComponent.class);

    @Resource
    private TrialEnvironmentDao trialEnvironmentDao;
    @Resource
    private ProductDao productDao;
    @Resource
    private GuidProvider guidProvider;
    @Resource
    private SettingsProvider settingsProvider;
    @Resource
    private TrialEmailComponent trialEmailComponent;
    @Resource
    private TrialInstanceDao trialInstanceDao;
    @Resource
    private UserProductDao userProductDao;
    @Resource
    private SeedService seedService;
    @Resource
    private RequestServices requestServices;
    @Resource
    private TrialProductChildDao trialProductChildDao;
    @Resource
    private SecurityService securityService;
    @Resource
    private UserDao userDao;
    @Resource
    private RegionDao regionDao;
    @Resource
    private ProductVersionDao productVersionDao;


    public TrialEnvironment buildEnvironment(TrialDto trialDto) {
        TrialEnvironment te = new TrialEnvironment();
        te.setId(trialDto.getId());
        te.setPassword(trialDto.getPassword());
        te.setProductVersion(productVersionDao.getReference(trialDto.getProductVersionId()));
        te.setUrl(trialDto.getUrl());
        te.setUsername(trialDto.getUsername());
        return te;
    }

    public void createAndInsertTrialEnvironment(TrialDto trialDto) {
        TrialEnvironment environment = buildEnvironment(trialDto);
        trialEnvironmentDao.save(environment);
    }

    public synchronized DeploymentStackDto launchTrial(HttpServletRequest request, DeployRequestDto deployRequestDto) {
        
    	Date createdAt=deployRequestDto.getCreatedAt();
    	if (createdAt==null) {
    		createdAt=new Date();
    	}
    	Long userId=deployRequestDto.getUserId();
        Locale currentUserLocale=securityService.getCurrentUser().getLanguage();
        User secUser=userDao.findById(securityService.getCurrentUser().getId());
        User user;
        if (userId==null) {
            user=secUser;
        } else {
            user=userDao.findById(userId);
        }

        if (user==null) {
            throw new CSWebApplicationException(StringDefs.GENERAL_ERROR_CODE,"User does not exist!");
        }
        if (deployRequestDto.getProductIds()==null || deployRequestDto.getProductIds().size()==0 ) {
            throw new CSWebApplicationException(StringDefs.GENERAL_ERROR_CODE,"Must specify one product!");
        }

        Product product=productDao.findById(deployRequestDto.getProductIds().get(0)[0]);
        if (product==null) {
            throw new CSWebApplicationException(StringDefs.GENERAL_ERROR_CODE,"Product specified does not exist!");
        }
        
        ProductVersion productVersion=productVersionDao.findById(deployRequestDto.getProductIds().get(0)[1]);
        
        if (deployRequestDto.getRegionId()==null) {
            throw new CSWebApplicationException(StringDefs.GENERAL_ERROR_CODE,"Region id null!");

        }
        Region region=regionDao.findById(deployRequestDto.getRegionId());
        if (region==null) {
            throw new CSWebApplicationException(StringDefs.GENERAL_ERROR_CODE,"Region does not exist!");
        }

        TrialDto trialDto=launchTrial(request,productVersion,user,region,currentUserLocale,deployRequestDto.getDeploymentName(), createdAt);

        return new DeploymentStackDto(trialDto,productVersion,secUser,user);
    }

    public synchronized TrialDto launchTrial(HttpServletRequest request, ProductVersion productVersion, User user, Region region, Locale locale, Date createdAt) {
        return launchTrial(request,productVersion,user,region,locale,productVersion.getProduct().getShortName()+"-"+guidProvider.generateGuid(), createdAt);
    }

    public synchronized TrialDto launchTrial(HttpServletRequest request, ProductVersion productVersion, User user, Region region, Locale locale, String name, Date createdAt) {
        TrialInstance useTrialInstance = null;
        String domain = user.getUsername().split("@")[1];
        Product product=productVersion.getProduct();
        final boolean recyleBasedOnDomain = (settingsProvider.isTrialRecycleInforDomain() && domain.equals(StringDefs.INFOR_DOMAIN)) ||
                settingsProvider.isTrialRecycleAllDomains() && !domain.equals(StringDefs.INFOR_DOMAIN);
        if (recyleBasedOnDomain) {
            useTrialInstance = trialInstanceDao.findByProductVersion_Product_ShortNameAndTypeAndDomain(product.getShortName(), TrialInstanceType.DOMAIN, domain);
        }
        TrialInstance trialInstance;
        if (useTrialInstance != null) {
            trialInstance = createTrialInstance(useTrialInstance, user, region,name,createdAt);
        } else {
            //final List<TrialEnvironment> environments = trialEnvironmentDao.findByProductAndRegionAndAvailable(product, region, true);
            final List<TrialEnvironment> environments=trialEnvironmentDao.findByProductVersion_ProductAndRegionAndAvailable(product, region,true);
            if (environments.isEmpty()) {
                trialEmailComponent.sendTrialsGoneEmail(productVersion);
                throw new CSWebApplicationException(StringDefs.GENERAL_ERROR_CODE, "No trials available.");
            }
            final TrialEnvironment environment = environments.get(0);
            
            List<TrialEnvironment> deletedEnvironments = deleteEnvironmentAndRelations(productVersion,region,environment);
            logger.info("deletedEnvironments.size():"+deletedEnvironments.size());
            for (TrialEnvironment deletedEnvironment : deletedEnvironments) {
                final ProductVersion currProductVersion = deletedEnvironment.getProductVersion();
                Long count = trialEnvironmentDao.countByProductVersionAndRegionAndAvailable(currProductVersion, region,true);
                logger.info("COUNT!::"+currProductVersion.getName()+":"+count);
                if (settingsProvider.isProductionMode()) {
                    logger.debug("Production Mode : " + count);
                    if (count <= 0) {
                        trialEmailComponent.sendTrialsGoneEmail(currProductVersion);
                    } else if (count <= 5) {
                        trialEmailComponent.sendTrialsLowEmail(currProductVersion, count);
                    }
                } else {
                    //Demo mode never runs out of trials.
                    if (count <= 1) {
                        seedService.seedProductTrials(currProductVersion, region);
                    }
                }
            }
            trialInstance = createTrialInstance(environment, user, productVersion, region,name,createdAt);

            if (recyleBasedOnDomain) {
                TrialInstance domainInstance = createTrialInstance(trialInstance, null, region,name,createdAt);
                domainInstance.setType(TrialInstanceType.DOMAIN);
                domainInstance.setDomain(domain);
                trialInstanceDao.save(domainInstance);
            }
        }

        UserProduct userProduct = userProductDao.findById(new UserProductKey(user, product));
        if (userProduct == null) {
            userProduct = new UserProduct(user, product);
        }

        //we already have an "owned".
        userProduct.setOwned(true);
        //userServiceComponent.updateOwnedForTrialsDeployments(user, product, true);
        trialInstanceDao.save(trialInstance);
        userProductDao.save(userProduct);

        String proxyUrl=requestServices.getContextUriBuilder(request).path("t/").queryParam("g", trialInstance.getGuid()).build().toString();
  
        TrialDto dto = new TrialDto(trialInstance);
        trialEmailComponent.sendTrialConfirmationEmail(user, locale, product, dto, proxyUrl);

        return dto;
    }

    public List<TrialEnvironment> deleteEnvironmentAndRelations(ProductVersion productVersion, Region region, TrialEnvironment environment) {
        List<TrialEnvironment> deletions = new ArrayList<>();
        deletions.add(environment);                             
        List<TrialProductChild> children = trialProductChildDao.findByRegionAndParentVersion(region, productVersion);
        for (TrialProductChild child : children) {
            deletions.addAll(trialEnvironmentDao.findByProductVersionAndEnvironmentIdAndAvailable(child.getChildVersion(), environment.getEnvironmentId(), true));
        }
        List<TrialProductChild> parents = trialProductChildDao.findByRegionAndChildVersion(region, productVersion);
        for (TrialProductChild parent : parents) {
            deletions.addAll(trialEnvironmentDao.findByProductVersionAndEnvironmentIdAndAvailable(parent.getParentVersion(), environment.getEnvironmentId(), true));
        }
        for(TrialEnvironment notAvailable: deletions) {
            notAvailable.setAvailable(false);
            trialEnvironmentDao.save(notAvailable);
        }
        return deletions;
    }

    private TrialInstance createTrialInstance(TrialEnvironment environment, User user, ProductVersion productVersion, Region region, String name, Date createdAt) {
        TrialInstance trialInstance = new TrialInstance();
        trialInstance.setUser(user);
        trialInstance.setGuid(getGuidProvider().generateGuid());
        trialInstance.setProductVersion(productVersion);
        trialInstance.setRegion(region);
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(createdAt);
        trialInstance.setCreatedAt(calendar.getTime());
        calendar.add(Calendar.DATE, StringDefs.TRIAL_LENGTH);
        calendar.set(Calendar.HOUR, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        trialInstance.setExpirationDate(calendar.getTime());
        trialInstance.setEnvironmentId(environment.getEnvironmentId());
        trialInstance.setUrl(environment.getUrl());
        trialInstance.setUsername(environment.getUsername());
        trialInstance.setPassword(environment.getPassword());
        trialInstance.setName(name);
        return trialInstance;
    }

    private TrialInstance createTrialInstance(TrialInstance instance, User user, Region region, String name, Date createdAt) {
        TrialInstance trialInstance = new TrialInstance();
        trialInstance.setUser(user);
        trialInstance.setGuid(generateGuid());
        trialInstance.setProductVersion(instance.getProductVersion());
        trialInstance.setRegion(region);
        trialInstance.setCreatedAt(createdAt);
        trialInstance.setExpirationDate(instance.getExpirationDate());
        trialInstance.setEnvironmentId(instance.getEnvironmentId());
        trialInstance.setUrl(instance.getUrl());
        trialInstance.setUsername(instance.getUsername());
        trialInstance.setPassword(instance.getPassword());
        trialInstance.setName(name);
        return trialInstance;
    }

    private String generateGuid() {
        return guidProvider.generateGuid();
    }


    public void setGuidProvider(GuidProvider guidProvider) {
        this.guidProvider = guidProvider;
    }

    public GuidProvider getGuidProvider() {
        return guidProvider;
    }

	public TrialEmailComponent getTrialEmailComponent() {
		return trialEmailComponent;
	}
    
    

}
