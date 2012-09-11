package com.infor.cloudsuite.platform.amazon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceState;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.StateReason;
import com.infor.cloudsuite.dao.AmazonCredentialsDao;
import com.infor.cloudsuite.dao.DeploymentStackDao;
import com.infor.cloudsuite.dao.RegionDao;
import com.infor.cloudsuite.dao.ScheduleDao;
import com.infor.cloudsuite.dto.AmazonCredentialsDto;
import com.infor.cloudsuite.dto.DeploymentStackUpdateDto;
import com.infor.cloudsuite.dto.InstanceDto;
import com.infor.cloudsuite.dto.InstanceStateDto;
import com.infor.cloudsuite.entity.AmazonCredentials;
import com.infor.cloudsuite.entity.DeploymentStackLogAction;
import com.infor.cloudsuite.entity.DeploymentStatus;
import com.infor.cloudsuite.entity.Region;
import com.infor.cloudsuite.entity.Schedule;
import com.infor.cloudsuite.entity.ScheduleStatus;
import com.infor.cloudsuite.entity.ScheduleType;
import com.infor.cloudsuite.platform.CSWebApplicationException;
import com.infor.cloudsuite.service.StringDefs;

@Component
public class StackAndInstanceManager {
    private static Logger logger = LoggerFactory.getLogger(StackAndInstanceManager.class);

    @Resource
    private AwsOperations awsOperations;
    @Resource
    private DeploymentStackListener stackListener;
    @Resource
    private AmazonCredentialsDao amazonCredentialsDao;
    @Resource
    private DeploymentStackDao deploymentStackDao;
    @Resource
    private StackBuilder stackBuilder;
    @Resource
    private ScheduleDao scheduleDao;
    @Resource
    private RegionDao regionDao;

    private String getRegionEndPoint(Long regionId) {
        if(regionId == null) {
            throw new CSWebApplicationException(StringDefs.GENERAL_ERROR_CODE,"Invalid region id!");
        }
        Region region = regionDao.findById(regionId);
        return region.getEndPoint();
    }

    public List<InstanceDto> getInstances(Long amazonCredentialsId, List<String> ids, Long regionId) {
        ArrayList<InstanceDto> instances=new ArrayList<>();

        AmazonCredentials amCred = amazonCredentialsDao.findById(amazonCredentialsId);
        if (amCred==null) {
            throw new CSWebApplicationException(StringDefs.GENERAL_ERROR_CODE,"Bad amazon credentials id passed in!");
        }

        AmazonCredentialsDto credDto = new AmazonCredentialsDto();
        credDto.setAwsKey(amCred.getAwsKey());
        credDto.setSecretKey(amCred.getSecretKey());

        AmazonEC2 amazonEC2 = getAmazonEC2(credDto, regionId);

        DescribeInstancesResult result;
        try {
            if(ids != null) {
                result = awsOperations.describeInstances(amazonEC2, ids);
            }
            else {
                result = awsOperations.describeInstances(amazonEC2);
            }
        }
        catch(AmazonClientException e) {
            logger.error("Error retreiving Instance List ", e);
            throw e;
        }

        final Set<String> deploymentVpc = new HashSet<>(deploymentStackDao.getVpcIdsForDeployments());
        final Map<String,String> nameTags = awsOperations.getNameTags(amazonEC2);
        for (Reservation reservation  : result.getReservations()) {
            List<Instance> awsInstances=reservation.getInstances();
            String reservationId=reservation.getReservationId();

            for (Instance instance : awsInstances) {
                if (deploymentVpc.contains(instance.getVpcId())) {
                    //Skip instances in any deployments.
                    continue;
                }
                InstanceDto dto=new InstanceDto();
                dto.setReservationId(reservationId);
                dto.setId(instance.getInstanceId());
                dto.setName(nameTags.get(instance.getInstanceId()));
                dto.setImageId(instance.getImageId());
                dto.setPrivateDnsName(instance.getPrivateDnsName());
                dto.setPrivateIpAddress(instance.getPrivateIpAddress());

                dto.setPublicDnsName(instance.getPublicDnsName());
                dto.setPublicIpAddress(instance.getPublicIpAddress());

                dto.setArchitecture(instance.getArchitecture());
                dto.setType(instance.getInstanceType());
                dto.setLifecycle(instance.getInstanceLifecycle());
                dto.setVpcId(instance.getVpcId());
                dto.setSubnetId(instance.getSubnetId());

                InstanceStateDto state=new InstanceStateDto();
                InstanceState is=instance.getState();
                if (is != null) {
                    state.setStateName(is.getName());
                    state.setStateCode(is.getCode());

                    if(state.getStateName().equalsIgnoreCase(com.infor.cloudsuite.dto.InstanceState.RUNNING.toString())) {
                        dto.setInstanceState(com.infor.cloudsuite.dto.InstanceState.AVAILABLE.toString());
                    }
                    else {
                        dto.setInstanceState(com.infor.cloudsuite.dto.InstanceState.NOT_AVAILABLE.toString());
                    }

                }

                StateReason reason=instance.getStateReason();
                if (reason != null) {
                    state.setStateReasonCode(reason.getCode());
                    state.setStateReasonMessage(reason.getMessage());
                }

                state.setStateTransitionReason(instance.getStateTransitionReason());

                dto.setState(state);
                dto.setLaunchTime(instance.getLaunchTime());


                // update the scheduled stop time on InstanceDto.
                List<Schedule> stopSchedules = scheduleDao.findByEntityIdAndTypeAndStatus( 
                        instance.getInstanceId(), 
                        ScheduleType.STOP_INSTANCE, 
                        ScheduleStatus.QUEUED);

                // For instances we assume only one scheduled stop.
                if(stopSchedules.size() > 0) {
                    dto.setScheduledStopAt(stopSchedules.get(0).getScheduledAt());
                }

                instances.add(dto);
            }
        }

        logger.info("Returning "+instances.size()+" instances for key '"+ amCred.getAwsKey()+"'");
        return instances;
    }

    private AmazonEC2 getAmazonEC2(AmazonCredentialsDto credDto, Long regionId) {
        AmazonCredentials amCred;
        BasicAWSCredentials basicCreds; 

        if(credDto.getId() != null) {
            amCred = amazonCredentialsDao.findById(credDto.getId());
            if (amCred==null) {
                throw new CSWebApplicationException(StringDefs.GENERAL_ERROR_CODE,"Bad amazon credentials id passed in!");
            }
            basicCreds = new BasicAWSCredentials(amCred.getAwsKey(), amCred.getSecretKey());
        }
        else {
            basicCreds = new BasicAWSCredentials(credDto.getAwsKey(), credDto.getSecretKey());
        }

        return awsOperations.getAmazonEC2(basicCreds, regionId);
    }

    private void logInstanceStatusChange(List<String> ids, DeploymentStatus action) { 

        for(String instanceId: ids) {
            DeploymentStackUpdateDto updateDto = new DeploymentStackUpdateDto();
            updateDto.setAction(DeploymentStackLogAction.CHANGE_IN_INSTANCE_STATUS);
            updateDto.setMessage(action.toString() + " Instance with InstancedId " + instanceId);
            updateDto.setStatus(action);
            updateDto.setVpcId(instanceId);
            stackListener.logAction(updateDto);
        }

    }

    public void terminateInstances(AmazonCredentialsDto credDto, List<String> instanceIds, Long regionId) {
        try {
            AmazonEC2 ec2= getAmazonEC2(credDto, regionId);
            awsOperations.terminateInstances(ec2, instanceIds);
        }
        catch(AmazonClientException e) {
            logger.error("Error terminating Instance List ", e);
            throw e;
        }
        logInstanceStatusChange(instanceIds, DeploymentStatus.TERMINATED);
    }

    public void startInstances(AmazonCredentialsDto credDto, List<String> instanceIds, Long regionId) {
        try {
            awsOperations.startInstances(getAmazonEC2(credDto, regionId), instanceIds);
        }
        catch(AmazonClientException e) {
            logger.error("Error starting Instance List ", e);
            throw e;
        }
        logInstanceStatusChange(instanceIds, DeploymentStatus.STARTED);
    }

    public void stopInstances(AmazonCredentialsDto credDto, List<String> instanceIds, Long regionId) {
        try {
            awsOperations.stopInstances(getAmazonEC2(credDto, regionId), instanceIds);
        }
        catch(AmazonClientException e) {
            logger.error("Error stopping Instance List ", e);
            throw e;
        }
        logInstanceStatusChange(instanceIds, DeploymentStatus.STOPPED);
    }

    public Future<Boolean> stopStack(String awsKey, String awsSecretKey, String vpcId, Long regionId) {
        return stackBuilder.stopStack(awsKey, awsSecretKey, vpcId, regionId);
    }

    public Future<String> terminateStack(String awsKey, String awsSecretKey, String vpcId, Long regionId) {
        return stackBuilder.tearDownStack(awsKey, awsSecretKey, vpcId, regionId);
    }
}