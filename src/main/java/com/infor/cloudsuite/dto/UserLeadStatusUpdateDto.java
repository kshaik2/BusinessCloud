package com.infor.cloudsuite.dto;

public class UserLeadStatusUpdateDto {

	private Long userId;
	private String leadStatus;
	
	public UserLeadStatusUpdateDto() {
		
	}
	
	public UserLeadStatusUpdateDto(Long userId, String leadStatus) {
		this.userId=userId;
		this.leadStatus=leadStatus;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getLeadStatus() {
		return leadStatus;
	}

	public void setLeadStatus(String leadStatus) {
		this.leadStatus = leadStatus;
	}
	
	
}
