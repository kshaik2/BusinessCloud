package com.infor.cloudsuite.dto;

import com.infor.cloudsuite.entity.DeploymentStackLogAction;
import com.infor.cloudsuite.entity.DeploymentState;
import com.infor.cloudsuite.entity.DeploymentStatus;


public class DeploymentStackUpdateDto {

	private String vpcId;
    private DeploymentState state;
    private DeploymentStatus status;
    private DeploymentStackLogAction action;
    private String message;
    private Long deploymentStackId;
	
	
	public DeploymentStackUpdateDto() {
		
	}
	public DeploymentStackUpdateDto(Long deploymentStackId,String vpcId, DeploymentState state,
			DeploymentStatus status, DeploymentStackLogAction action,
			String message) {
		this.deploymentStackId=deploymentStackId;
		this.vpcId = vpcId;
		this.state = state;
		this.status = status;
		this.action = action;
		this.message = message;
	}
	public String getVpcId() {
		return vpcId;
	}
	public void setVpcId(String vpcId) {
		this.vpcId = vpcId;
	}
	public DeploymentState getState() {
		return state;
	}
	public void setState(DeploymentState state) {
		this.state = state;
	}
	public DeploymentStatus getStatus() {
		return status;
	}
	public void setStatus(DeploymentStatus status) {
		this.status = status;
	}
	public DeploymentStackLogAction getAction() {
		return action;
	}
	public void setAction(DeploymentStackLogAction action) {
		this.action = action;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Long getDeploymentStackId() {
		return deploymentStackId;
	}
	public void setDeploymentStackId(Long deploymentStackId) {
		this.deploymentStackId = deploymentStackId;
	}
	
	
	
}
