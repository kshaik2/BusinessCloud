package com.infor.cloudsuite.service.component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.infor.cloudsuite.dao.AmazonCredentialsDao;
import com.infor.cloudsuite.dao.DeploymentStackDao;
import com.infor.cloudsuite.dao.DeploymentStackLogDao;
import com.infor.cloudsuite.dao.ProductDao;
import com.infor.cloudsuite.dao.ProductVersionDao;
import com.infor.cloudsuite.dao.RegionDao;
import com.infor.cloudsuite.dao.ScheduleDao;
import com.infor.cloudsuite.dao.UserDao;
import com.infor.cloudsuite.dto.DeployActionDto;
import com.infor.cloudsuite.dto.DeployActionScheduleType;
import com.infor.cloudsuite.dto.DeployRequestDto;
import com.infor.cloudsuite.dto.DeploymentStackDto;
import com.infor.cloudsuite.dto.TargetData;
import com.infor.cloudsuite.entity.*;
import com.infor.cloudsuite.platform.CSWebApplicationException;
import com.infor.cloudsuite.platform.amazon.StackBuilder;
import com.infor.cloudsuite.platform.components.GuidProvider;
import com.infor.cloudsuite.platform.json.CSObjectMapper;
import com.infor.cloudsuite.platform.security.SecurityService;
import com.infor.cloudsuite.service.StringDefs;
import com.infor.cloudsuite.task.ScheduleDispatch;

@Component
public class DeploymentServiceComponent {
    private static final Logger logger = LoggerFactory.getLogger(DeploymentServiceComponent.class);

    @Resource
    private ProductVersionDao productVersionDao;
    @Resource
    private ScheduleServiceComponent scheduleServiceComponent;
    @Resource
    private DeploymentStackDao deploymentStackDao;
    @Resource
    private AmazonCredentialsDao amazonCredentialsDao;
    @Resource
    private GuidProvider guidProvider;
    @Resource
    private StackBuilder stackBuilder;
    @Resource
    private ScheduleDao scheduleDao;
    @Resource
    private SecurityService securityService;
    @Resource
    private UserDao userDao;
    @Resource
    private DeploymentStackLogDao deploymentStackLogDao;
    @Resource
    private RegionDao regionDao;
    @Resource
    private ProductDao productDao;
    @Resource
    private CSObjectMapper objectMapper;

    @Transactional
    public void runActionSwitch(DeploymentStack stack, DeployActionDto deployActionDto, ScheduleDispatch dispatch) {
        AmazonCredentials amCred = stack.getAmazonCredentials();
        String vpcId = stack.getVpcId();
        Schedule schedule; 


        TargetData target = new TargetData();
        target.setAmznCredId(stack.getAmazonCredentials().getId());
        target.setUserId(stack.getUser().getId());
        target.setDeploymentName(stack.getDeploymentName());
        target.setUrl(deployActionDto.getUrl());

        switch (deployActionDto.getType()) {
        case START:
            stack.setDeploymentStatus(DeploymentStatus.STARTING);
            stack.setDeploymentState(DeploymentState.NOT_AVAILABLE);
            schedule = scheduleServiceComponent.getSchedule(stack.getId().toString(), deployActionDto.getScheduleType(), deployActionDto.getScheduleValue(), ScheduleType.STOP_STACK, convertToString(target), dispatch);

            if (schedule != null) {
                stack.setScheduleId(schedule.getId());
                target.setScheduledAtDate(schedule.getScheduledAt());
                scheduleServiceComponent.createEmailStopNotificationSchedule(schedule, convertToString(target), dispatch);
            }
            deploymentStackDao.save(stack);

            stackBuilder.startStack(amCred.getAwsKey(), amCred.getSecretKey(), vpcId, stack.getRegion().getId());
            break;

        case STOP:
            stack.setDeploymentStatus(DeploymentStatus.DEPLOYED);
            stack.setDeploymentState(DeploymentState.NOT_AVAILABLE);

            scheduleDao.updateScheduleStatus(ScheduleStatus.CANCELED, new Date(), ScheduleStatus.QUEUED, String.valueOf(stack.getId()), ScheduleType.STOP_STACK);
            scheduleDao.updateScheduleStatus(ScheduleStatus.CANCELED, new Date(), ScheduleStatus.QUEUED, String.valueOf(stack.getId()), ScheduleType.EMAIL_NOTIFICATION_SCHEDULE_STOP);

            deploymentStackDao.save(stack);
            stackBuilder.stopStack(amCred.getAwsKey(), amCred.getSecretKey(), vpcId, stack.getRegion().getId() );
            break;

        case RESTART:
            stack.setDeploymentStatus(DeploymentStatus.DEPLOYED);
            stack.setDeploymentState(DeploymentState.NOT_AVAILABLE);

            scheduleDao.updateScheduleStatus(ScheduleStatus.CANCELED, new Date(), ScheduleStatus.QUEUED, String.valueOf(stack.getId()), ScheduleType.STOP_STACK);
            scheduleDao.updateScheduleStatus(ScheduleStatus.CANCELED, new Date(), ScheduleStatus.QUEUED, String.valueOf(stack.getId()), ScheduleType.EMAIL_NOTIFICATION_SCHEDULE_STOP);

            schedule = scheduleServiceComponent.getSchedule(stack.getId().toString(), deployActionDto.getScheduleType(), deployActionDto.getScheduleValue(), ScheduleType.STOP_STACK, convertToString(target), dispatch);
            if (schedule != null) {
                stack.setScheduleId(schedule.getId());
                target.setScheduledAtDate(schedule.getScheduledAt());
                scheduleServiceComponent.createEmailStopNotificationSchedule(schedule, convertToString(target), dispatch);
            }

            deploymentStackDao.save(stack);
            stackBuilder.restartStack(amCred.getAwsKey(), amCred.getSecretKey(), vpcId, stack.getRegion().getId());
            break;

        case TERMINATE:
                scheduleDao.updateScheduleStatus(ScheduleStatus.CANCELED, new Date(), ScheduleStatus.QUEUED, String.valueOf(stack.getId()), ScheduleType.STOP_STACK);
                scheduleDao.updateScheduleStatus(ScheduleStatus.CANCELED, new Date(), ScheduleStatus.QUEUED, String.valueOf(stack.getId()), ScheduleType.EMAIL_NOTIFICATION_SCHEDULE_STOP);

                addScheduledTerminate(stack, dispatch);
            break;

        case CHANGE_SCHEDULE:
            scheduleDao.updateScheduleStatus(ScheduleStatus.CANCELED, new Date(), ScheduleStatus.QUEUED, String.valueOf(stack.getId()), ScheduleType.STOP_STACK);
            scheduleDao.updateScheduleStatus(ScheduleStatus.CANCELED, new Date(), ScheduleStatus.QUEUED, String.valueOf(stack.getId()), ScheduleType.EMAIL_NOTIFICATION_SCHEDULE_STOP);

            schedule = scheduleServiceComponent.getSchedule(stack.getId().toString(), deployActionDto.getScheduleType(), deployActionDto.getScheduleValue(), ScheduleType.STOP_STACK, convertToString(target), dispatch);

            if (schedule != null) {
                stack.setScheduleId(schedule.getId());
                target.setScheduledAtDate(schedule.getScheduledAt());
                scheduleServiceComponent.createEmailStopNotificationSchedule(schedule, convertToString(target), dispatch);
            }
            deploymentStackDao.save(stack);
            break;
        default:
            break;
        }
        // update the scheduled stop time on schedule entity.
        List<Schedule> stopSchedules =
                scheduleDao.findByEntityIdAndTypeAndStatus(
                        String.valueOf(stack.getId()),
                        ScheduleType.STOP_STACK,
                        ScheduleStatus.QUEUED);
        Date scheduledStopAt = null;
        if (stopSchedules.size() > 0) {
            scheduledStopAt = stopSchedules.get(0).getScheduledAt();
        }
        stack.setScheduledStopAt(scheduledStopAt);
        deploymentStackDao.save(stack);
    }

    @Transactional
    public void addScheduledTerminate(DeploymentStack stack, ScheduleDispatch dispatch) {
        List<Schedule> schedules = scheduleDao.findByEntityIdAndTypeAndStatus(stack.getId().toString(), ScheduleType.TERMINATE_STACK, ScheduleStatus.QUEUED);

        if (schedules.size() > 0) {
            logger.info("Terminate called for stack:" + stack.getId() + ", vpcId:" + stack.getVpcId() + " and there was(were) " + schedules.size() + " task(s) scheduled already");
            return;
        }
        schedules = scheduleDao.findByEntityIdAndTypeAndStatus(stack.getId().toString(), ScheduleType.TERMINATE_STACK, ScheduleStatus.RUNNING);
        if (schedules.size() > 0) {
            logger.info("Terminate called for stack:" + stack.getId() + ", vpcId:" + stack.getVpcId() + " and there was(were) " + schedules.size() + " task(s) *running* already");
            return;
        } 
        scheduleServiceComponent.getSchedule(stack.getId().toString(), DeployActionScheduleType.STOP_DATE, String.valueOf(System.currentTimeMillis()), ScheduleType.TERMINATE_STACK, null, dispatch);
    }

    @Transactional
    public DeploymentStack createAndSaveStack(DeployRequestDto deployRequestDto,
            Set<AmiDescriptor> amisHashSet,
            Collection<String> productNames,
            ScheduleDispatch dispatch) throws Exception {

        if (deployRequestDto.getProductIds().size() < 1) {
            throw new Exception("Empty product list");
        }

        AmazonCredentials amCred = amazonCredentialsDao.findById(deployRequestDto.getAmazonCredentialsId());

        if (amCred == null) {
            throw new Exception("Amazon credentials null");
        }

        DeploymentStack deploymentStack = new DeploymentStack();
        if (deployRequestDto.getCreatedAt()!=null) {
        	deploymentStack.setCreatedAt(deployRequestDto.getCreatedAt());
        } else {
        	deploymentStack.setCreatedAt(new Date());
        }
        deploymentStack.setAmazonCredentials(amazonCredentialsDao.findById(deployRequestDto.getAmazonCredentialsId()));
        deploymentStack.setCreatedByUser(userDao.findById(securityService.getCurrentUser().getId()));
        deploymentStack.setDeploymentName(deployRequestDto.getDeploymentName());
        Long userId = deployRequestDto.getUserId();
        if (userId == null) {
            deploymentStack.setUser(deploymentStack.getCreatedByUser());
        } else {
            deploymentStack.setUser(userDao.findById(userId));
        }
        long regionId = 4L;  //US EAST 1
        if (deployRequestDto.getRegionId() != null) {
            regionId = deployRequestDto.getRegionId();
        }

        deploymentStack.setRegion(regionDao.findById(regionId));
        List<Product> deployedProducts=new ArrayList<>();
        List<ProductVersion> deployedProductVersions=new ArrayList<>();
        
        for (Long[] arr : deployRequestDto.getProductIds()) {
        	Product depProd=productDao.findById(arr[0]);
        	if (depProd != null) {
        		deployedProducts.add(depProd);
        	}
        	ProductVersion depProdVers=productVersionDao.findById(arr[1]);
        	if (depProdVers != null) {
        		deployedProductVersions.add(depProdVers);
        	}
        }

        deploymentStack.setDeployedProductVersions(deployedProductVersions);
        deploymentStack.setDeploymentState(DeploymentState.NOT_AVAILABLE);
        deploymentStack.setDeploymentStatus(DeploymentStatus.DEPLOY_INITIATED);
        deploymentStack.setElasticIp("n/a");
        deploymentStack.setUrl("n/a");
        deploymentStack.setVpcId("VPC-" + guidProvider.generateGuid());
        deploymentStack.setUpdatedAt(new Date());
        deploymentStackDao.save(deploymentStack);

        //hydrate the object in the Tx
        int index=0;
        for (ProductVersion productVersion : deploymentStack.getDeployedProductVersions()) {
        	Product product=deployedProducts.get(index);
        	index++;
            amisHashSet.addAll(productVersion.getAmiDescriptors());
            productNames.add(product.getName()+" "+productVersion.getName());
        } 

        TargetData target = new TargetData();
        target.setAmznCredId(deploymentStack.getAmazonCredentials().getId());
        target.setUserId(deploymentStack.getUser().getId());
        target.setUrl(deployRequestDto.getUrl());
        target.setDeploymentName(deploymentStack.getDeploymentName());

        Schedule schedule = scheduleServiceComponent.getSchedule(deploymentStack.getId().toString(), deployRequestDto.getScheduleType(), deployRequestDto.getScheduleValue(), ScheduleType.STOP_STACK, convertToString(target), dispatch);

        if (schedule != null) {
            deploymentStack.setScheduleId(schedule.getId());
            deploymentStack.setScheduledStopAt(schedule.getScheduledAt());
            target.setScheduledAtDate(schedule.getScheduledAt());
            scheduleServiceComponent.createEmailStopNotificationSchedule(schedule, convertToString(target), dispatch);

        }
        deploymentStack.setNumServers(amisHashSet.size());
        deploymentStackDao.save(deploymentStack);

        return deploymentStack;

    }

    public ProductVersion latestProductVersion(List<ProductVersion> productVersions) {
    	ProductVersion prodVersion=null;
    	for (ProductVersion version :productVersions) {
    		if (prodVersion==null || prodVersion.getCreatedAt().before(version.getCreatedAt())) {
    			prodVersion=version;
    			
    		}
    	}
    	return prodVersion;
    }
    
    public List<DeploymentStackDto> getDtoListForStacks(List<DeploymentStack> stacks) {
        ArrayList<DeploymentStackDto> deploymentStackDtos=new ArrayList<>();
        for (DeploymentStack stack : stacks) {
            deploymentStackDtos.add(getDtoForStack(stack));
        }
        return deploymentStackDtos;
    }

    public List<DeploymentStackDto> getDeploymentStackDtoListForTrialInstances(List<TrialInstance> trialInstances) {
        ArrayList<DeploymentStackDto> deploymentStackDtos=new ArrayList<>();
        for (TrialInstance trialInstance : trialInstances) {
            deploymentStackDtos.add(getDtoForTrialInstance(trialInstance));
        }
        return deploymentStackDtos;
    }

    @Transactional(readOnly = true)
    public DeploymentStackDto getDtoForTrialInstance(TrialInstance trialInstance) {
        if (trialInstance==null) {
            throw new CSWebApplicationException(StringDefs.GENERAL_ERROR_CODE,"TrialInstance does not exist!");
        }
        return new DeploymentStackDto(trialInstance);
    }
    @Transactional(readOnly = true)
    public DeploymentStackDto getDtoForStack(DeploymentStack stack) {
        if (stack == null) {
            throw new CSWebApplicationException(StringDefs.GENERAL_ERROR_CODE, "DeploymentStack does not exist!");
        }
        DeploymentStackDto dto = new DeploymentStackDto(stack);
        Date lastStartedAt = deploymentStackLogDao.getMaxDateByDeploymentStackIdAndState(stack.getId(), DeploymentState.AVAILABLE);
        dto.setLastStartedAt((lastStartedAt == null ? stack.getCreatedAt() : lastStartedAt));
        return dto;
    }

    @Transactional(readOnly = true)
    public DeploymentStackDto getDtoForStack(Long deploymentStackId) {
        return getDtoForStack(deploymentStackDao.findById(deploymentStackId));
    }

    private String convertToString(TargetData targetData) {
        String data = null;
        try {
            data = objectMapper.writeValueAsString(targetData);
        } catch (IOException e) {
            logger.error("Failed to parse targetData");
        }
        return data;
    }

}
