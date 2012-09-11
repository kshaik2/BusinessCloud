package com.infor.cloudsuite.service.component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.eclipse.jetty.server.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.infor.cloudsuite.dao.ProductDao;
import com.infor.cloudsuite.dao.TrialInstanceDao;
import com.infor.cloudsuite.dao.UserDao;
import com.infor.cloudsuite.dto.DeployActionDto;
import com.infor.cloudsuite.dto.DeploymentStackDto;
import com.infor.cloudsuite.dto.TrialInstanceUpdateDto;
import com.infor.cloudsuite.entity.DeploymentState;
import com.infor.cloudsuite.entity.DeploymentStatus;
import com.infor.cloudsuite.entity.Product;
import com.infor.cloudsuite.entity.TrialInstance;
import com.infor.cloudsuite.entity.User;
import com.infor.cloudsuite.platform.CSWebApplicationException;
import com.infor.cloudsuite.service.StringDefs;

@Component
public class TrialExtensionComponent {
private static final Logger logger = LoggerFactory.getLogger(TrialExtensionComponent.class);
    @Resource
    private ProductDao productDao;

    @Resource
    private UserDao userDao;

    @Resource 
    private TrialInstanceDao trialInstanceDao;
    @Resource
    private TrialExtensionComponent trialExtensionComponent;
    @Resource
    private TrialInstanceDao trailInstanceDao;

    public DeploymentStackDto getDeploymentStackDto(Long id) {
        TrialInstance trialInstance = trailInstanceDao.findById(id);
        DeploymentStackDto stackDto;
        //Set appropriate status and state if trial is expired for front 
        //end to display.
        if( trialInstance.getExpirationDate().compareTo(new Date()) < 1 ) {   
            stackDto = new DeploymentStackDto(trialInstance);
            stackDto.setDeploymentState(DeploymentState.DELETED.toString());
            stackDto.setDeploymentStatus(DeploymentStatus.UNKNOWN.toString());
            return stackDto;
        }
        else {
            return new DeploymentStackDto(trialInstance);
        }
    }

    @Transactional
    public void doAction(Long id, DeployActionDto deployActionDto) {
        TrialInstance trialInstance;
        switch(deployActionDto.getType()) {
        case TERMINATE:
            trialInstance = trailInstanceDao.findById(id);
            trialInstance.setExpirationDate(new Date());
            break;
        case EXTEND:
            trialInstance = trailInstanceDao.findById(id);
            TrialInstanceUpdateDto trialUpdate = new TrialInstanceUpdateDto(trialInstance);
            trialUpdate.setDaysToExtend(14);
            trialExtensionComponent.extendTrialExpiration("BYINSTANCEID", trialUpdate);
            break;
        default: 
            break;
        }
    }

    public void extendTrialExpiration(String extendType,TrialInstanceUpdateDto trialInstanceUpdateDto) {

        Assert.notNull(trialInstanceUpdateDto);
        Assert.notNull(extendType);

        if (trialInstanceUpdateDto.getUpdatedAt()==null) {
            trialInstanceUpdateDto.setUpdatedAt(new Date());
        }
        TrialServiceCommandSwitch commandSwitch;

        try {
            commandSwitch=TrialServiceCommandSwitch.valueOf(extendType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CSWebApplicationException(e,Response.SC_BAD_REQUEST,e.getMessage());
        }

        switch (commandSwitch){ 
        case BYUSER: {
            extendTrialExpirationByUser(trialInstanceUpdateDto);
            break;
        }

        case BYGUID: {
            extendTrialByGuid(trialInstanceUpdateDto);
            break;
        }

        case ALLTRIALS: {
            extendAllTrials(trialInstanceUpdateDto);
            break;
        }

        case BYDOMAIN: {
            extendTrialByDomain(trialInstanceUpdateDto);
            break;
        }

        case ALLINFOR: {
            trialInstanceUpdateDto.setDomain(StringDefs.INFOR_DOMAIN);
            extendTrialByDomain(trialInstanceUpdateDto);
            break;
        }
        case BYINSTANCEID: {
            extendTrialById(trialInstanceUpdateDto);
            break;
        }

        default: break;
        }

    }

    private boolean isNullOrEmpty(String s) {
        return ((s==null)||s.trim().isEmpty());
    }

    private Product getTrialExtensionProduct(TrialInstanceUpdateDto trialInstanceUpdateDto){
        Product product=null;
        if (isNullOrEmpty(trialInstanceUpdateDto.getProductShortName())) {
            if (trialInstanceUpdateDto.getProductId() != null) {
                if (productDao.exists(trialInstanceUpdateDto.getProductId())){
                    product=productDao.getReference(trialInstanceUpdateDto.getProductId());
                }
            }
        } else {
            product=productDao.findByShortName(trialInstanceUpdateDto.getProductShortName());
        }

        return product;
    }

    private User getTrialExtensionUser(TrialInstanceUpdateDto trialInstanceUpdateDto) {
        User user=null;
        if (isNullOrEmpty(trialInstanceUpdateDto.getUserName())) {
            final Long userId = trialInstanceUpdateDto.getUserId();
            if (userId != null) {
                if (userDao.exists(userId)) {
                    user=userDao.getReference(userId);
                }
            }
        } else {
            user=userDao.findByUsername(trialInstanceUpdateDto.getUserName());
        }

        if (user!=null && !userDao.exists(user.getId())) {
            user=null;
        }

        return user;
    }
    //Commands

    private void extendTrialExpirationByUser(TrialInstanceUpdateDto trialInstanceUpdateDto) {

        User user=getTrialExtensionUser(trialInstanceUpdateDto);
        if (user==null) {
            throw new CSWebApplicationException(StringDefs.GENERAL_ERROR_CODE,"User does not exist!");
        }

        Product product=getTrialExtensionProduct(trialInstanceUpdateDto);

        if (product == null) {
            throw new CSWebApplicationException(StringDefs.GENERAL_ERROR_CODE,"Product does not exist!");
        }

        List<TrialInstance> trialInstances=trialInstanceDao.findByUserAndProductVersion_Product(user, product);
        extendTrialInstanceExpiration(trialInstances,trialInstanceUpdateDto);

    }

    private void extendTrialById(TrialInstanceUpdateDto trialInstanceUpdateDto) {

        TrialInstance trialInstance=trialInstanceDao.findById(trialInstanceUpdateDto.getId());
        if (trialInstance==null || !trialInstanceDao.exists(trialInstance.getId())) {
            throw new CSWebApplicationException(StringDefs.GENERAL_ERROR_CODE,"Instance does not exist!");
        }
        extendSingleTrialInstanceExpiration(trialInstance,daysToExtendFromDto(trialInstanceUpdateDto));

        trialInstance.setUpdatedAt(trialInstanceUpdateDto.getUpdatedAt());
        trialInstanceDao.save(trialInstance);
        trialInstanceDao.flush();
    }


    private void extendTrialByGuid(TrialInstanceUpdateDto trialInstanceUpdateDto) {

        TrialInstance trialInstance=trialInstanceDao.findByGuid(trialInstanceUpdateDto.getGuid());
        if (trialInstance==null || !trialInstanceDao.exists(trialInstance.getId())) {
            throw new CSWebApplicationException(StringDefs.GENERAL_ERROR_CODE,"GUID does not exist!");
        }
        extendSingleTrialInstanceExpiration(trialInstance,daysToExtendFromDto(trialInstanceUpdateDto));

        trialInstance.setUpdatedAt(trialInstanceUpdateDto.getUpdatedAt());
        trialInstanceDao.save(trialInstance);
        trialInstanceDao.flush();

    }

    private void extendTrialByDomain(TrialInstanceUpdateDto trialInstanceUpdateDto) {

        if (isNullOrEmpty(trialInstanceUpdateDto.getDomain())){
            throw new CSWebApplicationException(StringDefs.GENERAL_ERROR_CODE,"Domain value empty!");
        }

        Product product=getTrialExtensionProduct(trialInstanceUpdateDto);

        if (product==null) {
            throw new CSWebApplicationException(StringDefs.GENERAL_ERROR_CODE,"Product does not exist!");
        }

        List<TrialInstance> trialInstances=trialInstanceDao.findTrialInstancesForMatchingDomainAndProduct(trialInstanceUpdateDto.getDomain(), product);
        extendTrialInstanceExpiration(trialInstances,trialInstanceUpdateDto);  	

    }

    private void extendAllTrials(TrialInstanceUpdateDto trialInstanceUpdateDto) {

        extendTrialInstanceExpiration(trialInstanceDao.findAll(),trialInstanceUpdateDto);


    }

    private Integer daysToExtendFromDto(TrialInstanceUpdateDto trialInstanceUpdateDto) {
        if (trialInstanceUpdateDto.getDaysToExtend() == null) {
            return StringDefs.TRIAL_LENGTH;
        }

        return trialInstanceUpdateDto.getDaysToExtend();
    }
    //end command methods

    //will update full list and then save with DAO in bulk
    private void extendTrialInstanceExpiration(List<TrialInstance> trialInstances, TrialInstanceUpdateDto trialInstanceUpdateDto) {

        if (trialInstances==null) {
            return;
        }

        Integer days=daysToExtendFromDto(trialInstanceUpdateDto);

        for (TrialInstance trialInstance : trialInstances) {
            extendSingleTrialInstanceExpiration(trialInstance,days);
            trialInstance.setUpdatedAt(trialInstanceUpdateDto.getUpdatedAt());
        }

        trialInstanceDao.save(trialInstances);


    }

    private void extendSingleTrialInstanceExpiration(TrialInstance trialInstance, int days) {
        if (trialInstance==null) {
            return;
        }
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(trialInstance.getExpirationDate());
        calendar.add(Calendar.DATE, days);
        trialInstance.setExpirationDate(calendar.getTime());

    }

    private enum TrialServiceCommandSwitch {
        BYUSER,BYDOMAIN,ALLTRIALS,ALLINFOR,BYGUID,BYINSTANCEID
    }
}
