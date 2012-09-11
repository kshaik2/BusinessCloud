package com.infor.cloudsuite.dto.dashboard;

import java.util.ArrayList;
import java.util.List;

public class DashboardRollingDto {

	private Integer monthCount;
	private List<DashboardRollingPeriodDto> rollingDeployments;

	
	public DashboardRollingDto() {
	}
	public Integer getMonthCount() {
		return monthCount;
	}
	public void setMonthCount(Integer monthCount) {
		this.monthCount = monthCount;
	}
	public List<DashboardRollingPeriodDto> getRollingDeployments() {
		if (rollingDeployments==null) {
			
			rollingDeployments=new ArrayList<>();
		}
		return rollingDeployments;
	}
	public void setRollingDeployments(List<DashboardRollingPeriodDto> rollingDeployments) {
		this.rollingDeployments = rollingDeployments;
	}
	
	
	
}
