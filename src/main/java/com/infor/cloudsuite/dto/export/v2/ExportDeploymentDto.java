package com.infor.cloudsuite.dto.export.v2;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.amazonaws.util.json.JSONObject;
import com.infor.cloudsuite.dto.export.AbstractExportDto;

public class ExportDeploymentDto extends AbstractExportDto{
	private static final SimpleDateFormat SDF=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzz");
	
	
	private Date createdAt;
	private String createdAtString;
	
	private Date updatedAt;
	private String updatedAtString;
	
	private String awsKey;
	private String customerId;
	private String deploymentPassword;
	private String deploymentUsername;
	private String productShortName;
	private String productVersionName;
	private String username;
	private String url;
	
	private String amazonDeploymentId;
	private String deployStatus;
	private String deployStatusReason;
	private Boolean isComplete;
	private Boolean isDeployed;
	public ExportDeploymentDto() {
		
	}

	public ExportDeploymentDto(JSONObject jsonObject) {
		this.createdAtString=jsonObject.optString("createdAtString");
		this.createdAt=getDate(jsonObject, "createdAtString", "createdAt");
		this.updatedAtString=jsonObject.optString("updatedAtString");
		this.updatedAt=getDate(jsonObject,"updatedAtString","updatedAt");
		
		this.deployStatus=jsonObject.optString("deployStatus");
		this.deployStatusReason=jsonObject.optString("deployStatusReason");
		this.amazonDeploymentId=jsonObject.optString("amazonDeploymentId");
		this.awsKey=jsonObject.optString("awsKey");
		this.customerId=jsonObject.optString("customerId");
		this.deploymentPassword=jsonObject.optString("deploymentPassword");
		this.deploymentUsername=jsonObject.optString("deploymentUsername");
		this.productShortName=jsonObject.optString("productShortName");
		this.productVersionName=jsonObject.optString("productVersionName");
		this.username=jsonObject.optString("username");
		this.url=jsonObject.optString("url");
		
	}

	public String getAwsKey() {
		return awsKey;
	}

	public void setAwsKey(String awsKey) {
		this.awsKey = awsKey;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getDeploymentPassword() {
		return deploymentPassword;
	}

	public void setDeploymentPassword(String deploymentPassword) {
		this.deploymentPassword = deploymentPassword;
	}

	public String getDeploymentUsername() {
		return deploymentUsername;
	}
	
	public void setDeploymentUsername(String deploymentUsername) {
		this.deploymentUsername=deploymentUsername;
	}
	public String getProductShortName() {
		return productShortName;
	}

	public void setProductShortName(String productShortName) {
		this.productShortName = productShortName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getAmazonDeploymentId() {
		return amazonDeploymentId;
	}

	public void setAmazonDeploymentId(String amazonDeploymentId) {
		this.amazonDeploymentId = amazonDeploymentId;
	}

	public String getDeployStatus() {
		return deployStatus;
	}

	public void setDeployStatus(String deployStatus) {
		this.deployStatus = deployStatus;
	}

	public String getDeployStatusReason() {
		return deployStatusReason;
	}

	public void setDeployStatusReason(String deployStatusReason) {
		this.deployStatusReason = deployStatusReason;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getCreatedAtString() {
		return createdAtString;
	}

	public void setCreatedAtString(String createdAtString) {
		this.createdAtString = createdAtString;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getUpdatedAtString() {
		return updatedAtString;
	}

	public void setUpdatedAtString(String updatedAtString) {
		this.updatedAtString = updatedAtString;
	}

	public Boolean getIsComplete() {
		return isComplete;
	}

	public void setIsComplete(Boolean isComplete) {
		this.isComplete = isComplete;
	}

	public Boolean getIsDeployed() {
		return isDeployed;
	}

	public void setIsDeployed(Boolean isDeployed) {
		this.isDeployed = isDeployed;
	}

	public String getProductVersionName() {
		return productVersionName;
	}

	public void setProductVersionName(String productVersionName) {
		this.productVersionName = productVersionName;
	}
	
	
}
