package com.infor.cloudsuite.dto;

import java.util.Date;

import com.infor.cloudsuite.entity.DeploymentStackLog;
import com.infor.cloudsuite.entity.DeploymentStackLogAction;
import com.infor.cloudsuite.entity.DeploymentState;
import com.infor.cloudsuite.entity.DeploymentStatus;
public class DeploymentStackLogDto {

	private Long id;
	private Date createdAt;
	private Long deploymentStackId;
	private DeploymentStackLogAction logAction;
	private String message;
	private DeploymentStatus status;
	private DeploymentState state;
	private String vpcId;
	
	public DeploymentStackLogDto(Long id, Date createdAt, Long deploymentStackId, DeploymentStackLogAction logAction, String message,
			DeploymentStatus status, DeploymentState state, String vpcId) {
		this.id=id;
		this.createdAt=createdAt;
		this.deploymentStackId=deploymentStackId;
		this.logAction=logAction;
		this.message=message;
		this.status=status;
		this.state=state;
		this.vpcId=vpcId;
		
	}
	public DeploymentStackLogDto() {
		
	}
	
	
	public DeploymentStackLogDto(DeploymentStackLog stackLog) {
		this.id=stackLog.getId();
		this.createdAt=stackLog.getCreatedAt();
		this.deploymentStackId=stackLog.getDeploymentStackId();
		this.logAction=stackLog.getLogAction();
		this.message=stackLog.getMessage();
		this.state=stackLog.getState();
		this.status=stackLog.getStatus();
		this.vpcId=stackLog.getVpcId();
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public Long getDeploymentStackId() {
		return deploymentStackId;
	}
	public void setDeploymentStackId(Long deploymentStackId) {
		this.deploymentStackId = deploymentStackId;
	}
	public DeploymentStackLogAction getLogAction() {
		return logAction;
	}
	public void setLogAction(DeploymentStackLogAction logAction) {
		this.logAction = logAction;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public DeploymentStatus getStatus() {
		return status;
	}
	public void setStatus(DeploymentStatus status) {
		this.status = status;
	}
	public DeploymentState getState() {
		return state;
	}
	public void setState(DeploymentState state) {
		this.state = state;
	}
	public String getVpcId() {
		return vpcId;
	}
	public void setVpcId(String vpcId) {
		this.vpcId = vpcId;
	}
	
	
}
