package com.infor.cloudsuite.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.infor.cloudsuite.dto.DeploymentStackLogDto;
import com.infor.cloudsuite.entity.DeploymentStackLog;
import com.infor.cloudsuite.entity.DeploymentState;
import com.infor.cloudsuite.platform.jpa.ExtJpaRepository;

public interface DeploymentStackLogDao extends
		ExtJpaRepository<DeploymentStackLog, Long> {


	@SuppressWarnings("SpringDataJpaMethodInconsistencyInspection")
    @Query("SELECT new com.infor.cloudsuite.dto.DeploymentStackLogDto (dsl.id,dsl.createdAt,dsl.deploymentStackId,dsl.logAction,dsl.message,dsl.status,dsl.state,dsl.vpcId) FROM DeploymentStackLog dsl WHERE dsl.id=?1")
	public List<DeploymentStackLogDto> getLogsById(Long id);
	
	@SuppressWarnings("SpringDataJpaMethodInconsistencyInspection")
    @Query("SELECT new com.infor.cloudsuite.dto.DeploymentStackLogDto (dsl.id,dsl.createdAt,dsl.deploymentStackId,dsl.logAction,dsl.message,dsl.status,dsl.state,dsl.vpcId) FROM DeploymentStackLog dsl WHERE dsl.vpcId=?1")
	public List<DeploymentStackLogDto> getLogsByVpcId(String vpcId);

	@SuppressWarnings("SpringDataJpaMethodInconsistencyInspection")
    @Query("SELECT new com.infor.cloudsuite.dto.DeploymentStackLogDto (dsl.id,dsl.createdAt,dsl.deploymentStackId,dsl.logAction,dsl.message,dsl.status,dsl.state,dsl.vpcId) FROM DeploymentStackLog dsl WHERE dsl.deploymentStackId=?1")
	public List<DeploymentStackLogDto> getLogsByDeploymentStackId(Long deploymentStackId);

	@SuppressWarnings("SpringDataJpaMethodInconsistencyInspection")
    @Query("SELECT max(log.createdAt) from DeploymentStackLog log WHERE log.deploymentStackId=?1 AND log.state=?2")
	public Date getMaxDateByDeploymentStackIdAndState(Long depStackId, DeploymentState state);
}