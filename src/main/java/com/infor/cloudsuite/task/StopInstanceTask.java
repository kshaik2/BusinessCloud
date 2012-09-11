package com.infor.cloudsuite.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.amazonaws.AmazonClientException;
import com.infor.cloudsuite.dao.AmazonCredentialsDao;
import com.infor.cloudsuite.dto.AmazonCredentialsDto;
import com.infor.cloudsuite.dto.TargetData;
import com.infor.cloudsuite.entity.AmazonCredentials;
import com.infor.cloudsuite.entity.Schedule;
import com.infor.cloudsuite.platform.CSWebApplicationException;
import com.infor.cloudsuite.platform.json.CSObjectMapper;

@Component(value="stopInstanceTask")
@Scope("prototype")
public class StopInstanceTask extends AbstractScheduleTask  {
    private final Logger logger=LoggerFactory.getLogger(StopInstanceTask.class);

    @Resource
    private CSObjectMapper objectMapper;
    @Resource
    private AmazonCredentialsDao amazonCredentialsDao;
    
    public StopInstanceTask(Long scheduleId) {
        super(scheduleId);
    }
    @Override
    public boolean runTask(Schedule schedule) {
        boolean rtn = true;

        logger.info(">>run() called for instance with id:"+schedule.getEntityId());

        List<String> instanceId = new ArrayList<>();
        instanceId.add(schedule.getEntityId());
        TargetData deploymentData;
        
        try {
            deploymentData = objectMapper.readValue(schedule.getTargetObject(), TargetData.class);
            
            AmazonCredentialsDto amzCredsDto = new AmazonCredentialsDto();
            AmazonCredentials amzCreds = amazonCredentialsDao.findById(deploymentData.getAmznCredId());
            
            if(amzCreds == null) {
                throw new CSWebApplicationException(new NullPointerException("Amazon Credentials not found"));
            }
            
            amzCredsDto.setAwsKey(amzCreds.getAwsKey());
            amzCredsDto.setSecretKey(amzCreds.getSecretKey());
            stackAndInstanceManager.stopInstances(amzCredsDto, instanceId, deploymentData.getRegionId());
        } catch (AmazonClientException | CSWebApplicationException | IOException e) {
            logger.error("Encountered exception while calling stopInstances",e);
            rtn = false;
        }
        return rtn;
    }
}
