package com.infor.cloudsuite.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.infor.cloudsuite.entity.Schedule;
import com.infor.cloudsuite.entity.ScheduleStatus;
import com.infor.cloudsuite.entity.ScheduleType;
import com.infor.cloudsuite.platform.jpa.ExtJpaRepository;

public interface ScheduleDao extends ExtJpaRepository<Schedule, Long> {

	@SuppressWarnings("SpringDataJpaMethodInconsistencyInspection")
    public List<Schedule> findByScheduledAtIn(List<ScheduleType> types);
	
	public List<Schedule> findByStatus(ScheduleStatus status);

	public List<Schedule> findByEntityIdAndTypeAndStatus(String entityId, ScheduleType type, ScheduleStatus status);
	
	@Modifying
	@Query("update Schedule sched set sched.status=?1, sched.updatedAt=?2 WHERE sched.status=?3 AND sched.entityId=?4 AND sched.type=?5")
	public void updateScheduleStatus(ScheduleStatus setStatus, Date updatedAt, ScheduleStatus checkStatus, String entityId, ScheduleType type);

}
