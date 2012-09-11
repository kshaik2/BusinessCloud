package com.infor.cloudsuite.dto.export.v1;

public class ExportDeploymentDto {

	private String awsKey;
	private String customerId;
	private String deploymentPassword;
	private String deploymentUsername;
	private String productShortName;
	private String username;
	private String url;
	
	public ExportDeploymentDto() {
		
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
	
	
}
