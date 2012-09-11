package com.infor.cloudsuite.entity;

public enum ScheduleType {

	STOP_STACK("stopStackTask"),
	STOP_INSTANCE("stopInstanceTask"),
	TERMINATE_STACK("terminateStackTask"),
	EMAIL_NOTIFICATION_SCHEDULE_STOP("emailNotificationScheduleStopTask");
	
	
	private String beanName;
	
	ScheduleType(String beanName) {
		this.beanName=beanName;
	}

	public String getBeanName() {
		return beanName;
	}
	
	
}
