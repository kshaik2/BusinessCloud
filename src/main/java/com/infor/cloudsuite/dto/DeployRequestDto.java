package com.infor.cloudsuite.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class DeployRequestDto {

    private List<Long[]> productIds;
    private String deploymentName;
    private Long regionId;
    private Long userId;
    private Long amazonCredentialsId;
    private DeployActionScheduleType scheduleType=DeployActionScheduleType.NONE;
    private String scheduleValue;
    private boolean async=true;
	private String url;
	private DeploymentType deploymentType;
	private Date createdAt=null;
	
	public DeployRequestDto() {
		
	}

	
	public Long getUserId() {
		return userId;
	}


	public void setUserId(Long userId) {
		this.userId = userId;
	}


	public List<Long[]> getProductIds() {
		if (productIds==null) {
			productIds=new ArrayList<>();
		}
		return productIds;
	}

	public void setProductIds(List<Long[]> productIds) {
		this.productIds = productIds;
	}

    public String getDeploymentName() {
        return deploymentName;
    }

    public void setDeploymentName(String deploymentName) {
        this.deploymentName = deploymentName;
    }

    public Long getRegionId() {
		return regionId;
	}

	public void setRegionId(Long regionId) {
		this.regionId = regionId;
	}


	public Long getAmazonCredentialsId() {
		return amazonCredentialsId;
	}


	public void setAmazonCredentialsId(Long amazonCredentialsId) {
		this.amazonCredentialsId = amazonCredentialsId;
	}


	public boolean isAsync() {
		return async;
	}


	public void setAsync(boolean async) {
		this.async = async;
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


	public void setDeploymentType(DeploymentType deploymentType) {
		this.deploymentType = deploymentType;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt=createdAt;
	}

	public Date getCreatedAt() {

		return this.createdAt;
	}



	
	
}
