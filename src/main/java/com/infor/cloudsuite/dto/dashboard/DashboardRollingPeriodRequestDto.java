package com.infor.cloudsuite.dto.dashboard;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnore;

public class DashboardRollingPeriodRequestDto {

	private Date startDate=BEFORE_ALL;
	private Date endDate=sixtyDaysFromNow();
	private String filter;
	private String name;

	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getFilter() {
		return filter;
	}
	public void setFilter(String filter) {
		this.filter = filter;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@JsonIgnore
	private static final Date BEFORE_ALL=new Date(0L);
	
	@JsonIgnore
	private static Date sixtyDaysFromNow() {
		return new Date(System.currentTimeMillis()+SIXTY_DAYS);
	}
	@JsonIgnore
	private static final Long SIXTY_DAYS=(60*24*60*60*1000L);
}
