package com.infor.cloudsuite.dto.dashboard;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DashboardIntervalDto {

    private int days;
    private Map<Long,Long> allDeploymentsByProduct;
    private Map<Long,Long> awsDeploymentsByProduct;
    private Map<Long,Long> infor24DeploymentsByProduct;
    private Long newUsersCount;
    private Date from;
    private Date until;
	
	
	public Date getFrom() {
		return from;
	}

	public void setFrom(Date from) {
		this.from = from;
	}

	public Date getUntil() {
		return until;
	}

	public void setUntil(Date until) {
		this.until = until;
	}

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	public Map<Long, Long> getAllDeploymentsByProduct() {
		if (allDeploymentsByProduct==null) {
			allDeploymentsByProduct=new HashMap<>();
		}
		return allDeploymentsByProduct;
	}

	public void setAllDeploymentsByProduct(Map<Long, Long> allDeploymentsByProduct) {
		this.allDeploymentsByProduct = allDeploymentsByProduct;
	}

	public Map<Long, Long> getAwsDeploymentsByProduct() {
		if (awsDeploymentsByProduct==null) {
			awsDeploymentsByProduct=new HashMap<>();
		}
		return awsDeploymentsByProduct;
	}

	public void setAwsDeploymentsByProduct(Map<Long, Long> awsDeploymentsByProduct) {
		this.awsDeploymentsByProduct = awsDeploymentsByProduct;
	}

	public Map<Long, Long> getInfor24DeploymentsByProduct() {
		if (infor24DeploymentsByProduct==null) {
			infor24DeploymentsByProduct=new HashMap<>();
		}
		return infor24DeploymentsByProduct;
	}

	public void setInfor24DeploymentsByProduct(
			Map<Long, Long> infor24DeploymentsByProduct) {
		this.infor24DeploymentsByProduct = infor24DeploymentsByProduct;
	}

	public Long getNewUsersCount() {
		return newUsersCount;
	}

	public void setNewUsersCount(Long newUsersCount) {
		this.newUsersCount = newUsersCount;
	}
	
	
	
}
