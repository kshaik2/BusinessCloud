package com.infor.cloudsuite.dto.export.v2;

import com.amazonaws.util.json.JSONObject;
import com.infor.cloudsuite.dto.export.AbstractExportDto;
import com.infor.cloudsuite.entity.TrialEnvironment;

public class ExportTrialEnvironmentDto extends AbstractExportDto{

	private String environmentId;
	private String productShortName;
	private String productVersionName;
	private String url;	
	private String username;
	private String password;	
	private String regionShortName;
	private Boolean available;
	public ExportTrialEnvironmentDto() {
		
	}
	
	public ExportTrialEnvironmentDto(TrialEnvironment trialEnvironment) {

		this.regionShortName=trialEnvironment.getRegion().getShortName();
		this.environmentId=trialEnvironment.getEnvironmentId();
		this.productShortName=trialEnvironment.getProductVersion().getProduct().getShortName();
		this.productVersionName=trialEnvironment.getProductVersion().getName();
		this.available=trialEnvironment.isAvailable();
		this.url=trialEnvironment.getUrl();
		this.username=trialEnvironment.getUsername();
		this.password=trialEnvironment.getPassword();
	}
	
	public ExportTrialEnvironmentDto(JSONObject jsonObject) {
		this.environmentId=jsonObject.optString("environmentId");
		this.productShortName=jsonObject.optString("productShortName");
		this.productVersionName=jsonObject.optString("productVersionName");
		this.url=jsonObject.optString("url");
		this.username=jsonObject.optString("username");
		this.password=jsonObject.optString("password");
		this.regionShortName=jsonObject.optString("regionShortName");
		this.available=jsonObject.optBoolean("available");
	}
	
	
	public String getEnvironmentId() {
		return environmentId;
	}

	public void setEnvironmentId(String environmentId) {
		this.environmentId = environmentId;
	}

	public String getProductShortName() {
		return productShortName;
	}

	public void setProductShortName(String productShortName) {
		this.productShortName = productShortName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public String getRegionShortName() {
		return regionShortName;
	}

	public void setRegionShortName(String regionShortName) {
		this.regionShortName = regionShortName;
	}

	public String getProductVersionName() {
		return productVersionName;
	}

	public void setProductVersionName(String productVersionName) {
		this.productVersionName = productVersionName;
	}

	public StringBuilder loggableOutput() {
		StringBuilder builder=new StringBuilder();
		builder.append("environmentId:").append(environmentId).append(LS);
		builder.append("regionShortName:").append(regionShortName).append(LS);
		builder.append("productShortName:").append(productShortName).append(LS);
		builder.append("url:").append(url).append(LS);
		builder.append("username:").append(username).append(LS);
		builder.append("password:").append(password).append(LS);
		builder.append("available:").append(available).append(LS);
		
		return builder;
	}

	public Boolean getAvailable() {
		return available;
	}

	public void setAvailable(Boolean available) {
		this.available = available;
	}
	
	
}
