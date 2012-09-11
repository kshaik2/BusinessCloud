package com.infor.cloudsuite.task;

import java.util.Date;

import javax.annotation.Resource;

import com.infor.cloudsuite.dao.DeploymentStackDao;
import com.infor.cloudsuite.dao.ScheduleDao;
import com.infor.cloudsuite.entity.Schedule;
import com.infor.cloudsuite.entity.ScheduleStatus;
import com.infor.cloudsuite.platform.amazon.AwsOperations;
import com.infor.cloudsuite.platform.amazon.StackAndInstanceManager;
import com.infor.cloudsuite.service.component.ScheduleServiceComponent;

public abstract class AbstractScheduleTask implements Runnable{
	
	private Long scheduleId;
	
	@Resource
	protected DeploymentStackDao deploymentStackDao;
	@Resource
	protected ScheduleDao scheduleDao;
	@Resource
	protected AwsOperations awsOperations;
	@Resource
	protected StackAndInstanceManager stackAndInstanceManager;
	@Resource
	protected ScheduleServiceComponent scheduleServiceComponent;
	
	public AbstractScheduleTask(Long scheduleId) {
		this.scheduleId=scheduleId;
	}

	public Long getScheduleId() {
		return scheduleId;
	}
	
	
	private Schedule checkRun() {
		Schedule schedule=scheduleDao.findById(this.scheduleId);

		if (schedule != null && schedule.getStatus()==ScheduleStatus.QUEUED) {
			schedule.setStatus(ScheduleStatus.RUNNING);
			scheduleDao.save(schedule);
			scheduleDao.flush();
		} else {
			
			return null;
		}
			
	   return schedule;
	}
	
	//@Transactional
	public final void run() {
		
		Schedule schedule=checkRun();
		if (schedule != null) {
			
			if (runTask(schedule)) {
				schedule.setStatus(ScheduleStatus.RAN);
			} else {
				schedule.setStatus(ScheduleStatus.ERROR);
			}
			schedule.setUpdatedAt(new Date());
			scheduleDao.save(schedule);
			scheduleDao.flush();
		}
		
	
	}
	
	protected abstract boolean runTask(Schedule schedule);
	
}
