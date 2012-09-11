package com.infor.cloudsuite.service.component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.infor.cloudsuite.dao.ScheduleDao;
import com.infor.cloudsuite.dto.AmazonCredentialsDto;
import com.infor.cloudsuite.dto.DeployActionScheduleType;
import com.infor.cloudsuite.dto.InstanceDto;
import com.infor.cloudsuite.dto.InstanceStateChangeDto;
import com.infor.cloudsuite.dto.TargetData;
import com.infor.cloudsuite.entity.Schedule;
import com.infor.cloudsuite.entity.ScheduleStatus;
import com.infor.cloudsuite.entity.ScheduleType;
import com.infor.cloudsuite.platform.CSWebApplicationException;
import com.infor.cloudsuite.platform.amazon.StackAndInstanceManager;
import com.infor.cloudsuite.platform.json.CSObjectMapper;
import com.infor.cloudsuite.task.ScheduleDispatch;

@Component
public class InstanceServiceComponent {
    private static final Logger logger = LoggerFactory.getLogger(InstanceServiceComponent.class);

    @Resource
    private CSObjectMapper objectMapper;
    @Resource
    private StackAndInstanceManager stackAndInstanceManager;
    @Resource
    ScheduleServiceComponent scheduleServiceComponent;
    @Resource
    private ScheduleDao scheduleDao;

    public List<InstanceDto> getInstances(Long id, List<String> instanceIds, Long regionId) {
        List<InstanceDto> instances = stackAndInstanceManager.getInstances(id, instanceIds, regionId);

        for(InstanceDto anInstance : instances) {
            // update the scheduled stop time on InstanceDto.
            List<Schedule> stopSchedules = 
                    scheduleDao.findByEntityIdAndTypeAndStatus( anInstance.getId(), ScheduleType.STOP_INSTANCE, 
                            ScheduleStatus.QUEUED);
            Date scheduledStopAt;
            //For instances we assume only one scheduled stop.
            if(stopSchedules.size() > 0) {
                scheduledStopAt = stopSchedules.get(0).getScheduledAt();
                anInstance.setScheduledStopAt(scheduledStopAt);
            }
        }
        return instances;
    }

    @Transactional
    public void updateInstancesStatus(InstanceStateChangeDto stateChange, ScheduleDispatch dispatch) {
        AmazonCredentialsDto creds =  stateChange.getCredentials();
        List<String> idsToUpdate = stateChange.getInstanceIds();

        switch (stateChange.getStateChange()) {
        case START: 
            stackAndInstanceManager.startInstances(creds, idsToUpdate, stateChange.getRegionId());
            scheduleInstances(stateChange, ScheduleType.STOP_INSTANCE, dispatch);
            break;
        case STOP:
            stackAndInstanceManager.stopInstances(creds, idsToUpdate, stateChange.getRegionId());
            changeScheduleStatus(idsToUpdate, ScheduleStatus.CANCELED, ScheduleStatus.QUEUED, ScheduleType.STOP_INSTANCE);
            changeScheduleStatus(idsToUpdate, ScheduleStatus.CANCELED, ScheduleStatus.QUEUED, ScheduleType.EMAIL_NOTIFICATION_SCHEDULE_STOP);
            break;
        case EDIT:
            changeScheduleStatus(idsToUpdate, ScheduleStatus.CANCELED, ScheduleStatus.QUEUED, ScheduleType.STOP_INSTANCE);
            changeScheduleStatus(idsToUpdate, ScheduleStatus.CANCELED, ScheduleStatus.QUEUED, ScheduleType.EMAIL_NOTIFICATION_SCHEDULE_STOP);
            scheduleInstances(stateChange, ScheduleType.STOP_INSTANCE, dispatch);
            break;
        case TERMINATE:
            stackAndInstanceManager.terminateInstances(creds, idsToUpdate, stateChange.getRegionId());
            changeScheduleStatus(idsToUpdate, ScheduleStatus.CANCELED, ScheduleStatus.QUEUED, ScheduleType.STOP_INSTANCE);
            changeScheduleStatus(idsToUpdate, ScheduleStatus.CANCELED, ScheduleStatus.QUEUED, ScheduleType.EMAIL_NOTIFICATION_SCHEDULE_STOP);
            break;
        default: 
            throw new CSWebApplicationException(Status.BAD_REQUEST, stateChange); 
        }
    }

    private void changeScheduleStatus(List<String> ids, ScheduleStatus toStatus, ScheduleStatus fromStatus, ScheduleType type) {
        for(String id: ids) {
            scheduleDao.updateScheduleStatus(toStatus, new Date(), fromStatus, id, type);
        }
    }

    private List<Schedule> scheduleInstances(InstanceStateChangeDto stateChange, ScheduleType schedType, ScheduleDispatch dispatch) {
        List <Schedule> schedules = new ArrayList<>();
        List<String> idsToUpdate = stateChange.getInstanceIds();
        DeployActionScheduleType deployType = stateChange.getScheduleType();
        String timeValue = stateChange.getScheduleValue(); 
        Long amznCredId = stateChange.getCredentials().getId();
        Long userId = stateChange.getCredentials().getUserId();
        String url = stateChange.getUrl();
        Long regionId = stateChange.getRegionId();


        for(String instanceId: idsToUpdate) {
            TargetData target = new TargetData();
            target.setAmznCredId(amznCredId);
            target.setUserId(userId);
            target.setEntityId(instanceId);
            target.setUrl(url);
            target.setRegionId(regionId);
            Schedule schedule = scheduleServiceComponent.getSchedule(instanceId, deployType, timeValue, schedType, convertToString(target), dispatch);  
            schedules.add(schedule);

            if(schedule != null) {
                target.setScheduledAtDate(schedule.getScheduledAt());
                scheduleServiceComponent.createEmailStopNotificationSchedule(schedule, convertToString(target), dispatch);
            }   
        }
        return schedules;
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
