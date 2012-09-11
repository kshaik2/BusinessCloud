package com.infor.cloudsuite.dto;

public class DeployActionDto {

	private String vpcId;
	private Long deploymentStackId;
	private String instanceId;
	private DeployActionType type;
	private DeployActionScheduleType scheduleType;
	private String scheduleValue;
	private String url;
	private DeploymentType deploymentType;
	
	private boolean async=true;
	
	public DeployActionDto() {
		
	}
	public DeployActionDto(String vpcOrInstanceId, DeployActionType type, boolean vpcOrInstance) {
		this.type=type;
		if (vpcOrInstance) {
			this.vpcId=vpcOrInstanceId;
		} else {
			this.instanceId=vpcOrInstanceId;
		}
	}
	
	public DeployActionDto(String vpcId, DeployActionType type) {
		this(vpcId,type,true);
	}
	public DeployActionDto(Long deploymentStackId, DeployActionType type) {
		this.type=type;
		this.deploymentStackId=deploymentStackId;
	}
	public String getVpcId() {
		return vpcId;
	}
	public void setVpcId(String vpcId) {
		this.vpcId = vpcId;
	}
	public Long getDeploymentStackId() {
		return deploymentStackId;
	}
	public void setDeploymentStackId(Long deploymentStackId) {
		this.deploymentStackId = deploymentStackId;
	}
	public DeployActionType getType() {
		return type;
	}
	public void setType(DeployActionType type) {
		this.type = type;
	}
	public boolean isAsync() {
		return async;
	}
	public void setAsync(boolean async) {
		this.async = async;
	}
	public String getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}
	public DeployActionScheduleType getScheduleType() {
		return scheduleType;
	}
	public void setScheduleType(DeployActionScheduleType scheduleType) {
		this.scheduleType = scheduleType;
	}
	public String getScheduleValue() {
		return scheduleValue;
	}
	public void setScheduleValue(String scheduleValue) {
		this.scheduleValue = scheduleValue;
	}
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public DeploymentType getDeploymentType() {
        return deploymentType;
    }
    public void setDeploymentType(DeploymentType depolymentType) {
        this.deploymentType = depolymentType;
    }

	
	
	
}
