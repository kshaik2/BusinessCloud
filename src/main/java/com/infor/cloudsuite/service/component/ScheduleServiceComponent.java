package com.infor.cloudsuite.service.component;


import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import com.infor.cloudsuite.dao.ScheduleDao;
import com.infor.cloudsuite.dto.DeployActionScheduleType;
import com.infor.cloudsuite.entity.Schedule;
import com.infor.cloudsuite.entity.ScheduleStatus;
import com.infor.cloudsuite.entity.ScheduleType;
import com.infor.cloudsuite.platform.components.SettingsProvider;
import com.infor.cloudsuite.task.AbstractScheduleTask;
import com.infor.cloudsuite.task.ScheduleDispatch;

@Component
public class ScheduleServiceComponent {
    private static final Logger logger = LoggerFactory.getLogger(ScheduleServiceComponent.class);

    @Resource
    private SettingsProvider settingsProvider;
    @Resource(name = "businessCloudScheduler")
    private ThreadPoolTaskScheduler scheduler; 
    @Resource
    private ApplicationContext appContext;
    @Resource 
    private ScheduleDao scheduleDao;
    
    public void dispatchSchedules(List<Schedule> schedules) {

        for (Schedule schedule : schedules) {
            dispatchSchedule(schedule);
        }

    }

    public void dispatchSchedule(Schedule schedule) {

        AbstractScheduleTask task=(AbstractScheduleTask)appContext.getBean(schedule.getType().getBeanName(),schedule.getId());
        scheduler.schedule(task, schedule.getScheduledAt());
    }

    public Schedule getSchedule(String deploymentStackId, DeployActionScheduleType type, String value, ScheduleType scheduleType, String targetObject,  ScheduleDispatch dispatch) {
        Schedule schedule=null;
        Date scheduledAt=null;
        Date now=new Date();
        if (type==null) {
            type=DeployActionScheduleType.NONE;
        }

        switch(type) {
        case HOURLY:
            int hours=Integer.parseInt(value);
            GregorianCalendar calendar=new GregorianCalendar();
            calendar.setTime(now);
            calendar.add(GregorianCalendar.HOUR, hours);
            scheduledAt=calendar.getTime();
            break;
        case STOP_DATE:
            long date=Long.parseLong(value);
            scheduledAt=new Date(date);
            break;
        default:
            break;
        }

        
        if (scheduledAt != null) {
            schedule=new Schedule();
            schedule.setType(scheduleType);
            schedule.setStatus(ScheduleStatus.QUEUED);
            schedule.setCreatedAt(now);
            schedule.setUpdatedAt(now);
            schedule.setScheduledAt(scheduledAt);
            schedule.setEntityId(deploymentStackId);
            schedule.setTargetObject(targetObject);
            scheduleDao.save(schedule);
            //dispatchSchedule(schedule);

        }
        dispatch.addSchedule(schedule).setAppContext(appContext).setScheduler(scheduler);
        
        return schedule;
    }

    public Schedule createEmailStopNotificationSchedule(Schedule schedule, String targetData, ScheduleDispatch dispatch) {
        Schedule rtn;
        Date scheduledDate;
        GregorianCalendar calendar = new java.util.GregorianCalendar();

        scheduledDate = schedule.getScheduledAt();
        calendar.setTime(scheduledDate);
        calendar.add(java.util.GregorianCalendar.MINUTE, settingsProvider.getScheduleAdvancedEmailWarningTime());
        
        rtn = getSchedule(schedule.getEntityId(), DeployActionScheduleType.STOP_DATE, String.valueOf(calendar.getTimeInMillis()), ScheduleType.EMAIL_NOTIFICATION_SCHEDULE_STOP, targetData, dispatch); 

        return rtn;
    }
}
