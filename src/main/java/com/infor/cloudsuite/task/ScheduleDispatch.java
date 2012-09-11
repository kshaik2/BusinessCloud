package com.infor.cloudsuite.task;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import com.infor.cloudsuite.entity.Schedule;

public class ScheduleDispatch {
    private static final Logger logger = LoggerFactory.getLogger(ScheduleDispatch.class);

    private List<Schedule> schedules = new ArrayList<>();
    private ThreadPoolTaskScheduler scheduler;
    private ApplicationContext appContext;

    public ScheduleDispatch(Schedule schedule, ThreadPoolTaskScheduler scheduler,ApplicationContext appContext) {
        this.schedules.add(schedule);
        this.scheduler=scheduler;
        this.appContext=appContext;
    }

    public ScheduleDispatch() {

    }

    public void dispatch() {
        if (hasValidSchedule()) {
            try {
                for(Schedule schedule: schedules) {
                    AbstractScheduleTask task=(AbstractScheduleTask)appContext.getBean(schedule.getType().getBeanName(),schedule.getId());
                    scheduler.schedule(task, schedule.getScheduledAt());
                }
            } catch (BeansException e) {
                logger.error("Error dispatching scheduled task in doAction()",e);
            }

        } else {
            logger.error("A no valid schedules");
        }
    }

    public boolean hasValidSchedule() {
        return schedules.size() > 0;
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public ScheduleDispatch addSchedule(Schedule schedule) {
        if(schedule != null) {
            this.schedules.add(schedule);
        }
        return this;
    }

    public ThreadPoolTaskScheduler getScheduler() {
        return scheduler;
    }

    public ScheduleDispatch setScheduler(ThreadPoolTaskScheduler scheduler) {

        this.scheduler = scheduler;
        return this;
    }

    public ApplicationContext getAppContext() {
        return appContext;
    }

    public ScheduleDispatch setAppContext(ApplicationContext appContext) {
        this.appContext = appContext;
        return this;
    }


}
