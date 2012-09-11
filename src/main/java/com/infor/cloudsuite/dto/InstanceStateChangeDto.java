package com.infor.cloudsuite.dto;

import java.util.List;

public class InstanceStateChangeDto {

	private AmazonCredentialsDto credentials;
	private List<String> instanceIds;
	private InstanceActionType stateChange;
	private DeployActionScheduleType scheduleType;
    private String scheduleValue;
    private String url;
    private Long regionId;
		
	public List<String> getInstanceIds() {
		return instanceIds;
	}
	public void setInstanceIds(List<String> instanceIds) {
		this.instanceIds = instanceIds;
	}
	public InstanceActionType getStateChange() {
		return stateChange;
	}
	public void setStateChange(InstanceActionType stateChange) {
		this.stateChange = stateChange;
	}
	public AmazonCredentialsDto getCredentials() {
		return credentials;
	}
	public void setCredentials(AmazonCredentialsDto credentials) {
		this.credentials = credentials;
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
    public Long getRegionId() {
        return regionId;
    }
    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }
}
