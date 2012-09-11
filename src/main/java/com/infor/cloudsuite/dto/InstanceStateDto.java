package com.infor.cloudsuite.dto;

public class InstanceStateDto {
	private String stateName;
	private Integer stateCode;
	
	private String stateReasonCode;
	private String stateReasonMessage;
	private String stateTransitionReason;
	
	public String getStateName() {
		return stateName;
	}
	public void setStateName(String stateName) {
	    if(stateName != null) {
	        char[] chars = stateName.toCharArray();
	        chars[0] = Character.toUpperCase(chars[0]);
	        this.stateName = new String(chars);
	    }
	}
	
	public String getStateReasonMessage() {
		return stateReasonMessage;
	}
	public void setStateReasonMessage(String stateReasonMessage) {
		this.stateReasonMessage = stateReasonMessage;
	}

	public Integer getStateCode() {
		return stateCode;
	}
	public void setStateCode(Integer stateCode) {
		this.stateCode = stateCode;
	}

	public String getStateReasonCode() {
		return stateReasonCode;
	}
	public void setStateReasonCode(String stateReasonCode) {
		this.stateReasonCode = stateReasonCode;
	}
	public String getStateTransitionReason() {
		return stateTransitionReason;
	}
	public void setStateTransitionReason(String stateTransitionReason) {
		this.stateTransitionReason = stateTransitionReason;
	}

	
	
}
