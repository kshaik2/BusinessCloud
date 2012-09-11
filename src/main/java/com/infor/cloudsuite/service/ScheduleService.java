package com.infor.cloudsuite.service;

import javax.annotation.Resource;
import javax.ws.rs.Path;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.infor.cloudsuite.dao.ScheduleDao;
import com.infor.cloudsuite.entity.ScheduleStatus;
import com.infor.cloudsuite.service.component.ScheduleServiceComponent;

@Service
@Transactional(propagation = Propagation.REQUIRED)
@Path("/schedule")
public class ScheduleService {

	@Resource
	private ScheduleDao scheduleDao;
	@Resource
	private ScheduleServiceComponent scheduleServiceComponent;
	
	public void loadSchedules() {
		
		scheduleServiceComponent.dispatchSchedules(scheduleDao.findByStatus(ScheduleStatus.QUEUED));
		
	}
	
}
