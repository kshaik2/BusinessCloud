package com.infor.cloudsuite.dto.export.v2;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.amazonaws.util.json.JSONObject;
import com.infor.cloudsuite.dto.export.AbstractExportDto;
import com.infor.cloudsuite.entity.TrialInstance;

public class ExportTrialInstanceDto extends AbstractExportDto{
	public static final SimpleDateFormat SDF=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzz");
	private String productShortName;
	private String productVersionName;
	private String domain;
	private String trialUsername;
	private String guid;
	private String environmentId;
	private Date expirationDate;
	private String expirationDateString;
	private String trialPassword;
	private String username;
	private String type;
	private String url;
	private Date createdAt;
	private String createdAtString;
	private Date updatedAt;
	private String updatedAtString;
	private String regionShortName;
	
	public ExportTrialInstanceDto() {
		
	}
	
	public ExportTrialInstanceDto(TrialInstance trialInstance) {
		this.productShortName=trialInstance.getProductVersion().getProduct().getShortName();
		this.productVersionName=trialInstance.getProductVersion().getName();
		this.domain=trialInstance.getDomain();
		this.trialUsername=trialInstance.getUsername();
		this.guid=trialInstance.getGuid();
		this.environmentId=trialInstance.getEnvironmentId();
		this.expirationDate=trialInstance.getExpirationDate();
		this.expirationDateString=SDF.format(this.expirationDate);
		this.createdAt=trialInstance.getCreatedAt();
		this.createdAtString=SDF.format(this.createdAt);
		this.updatedAt=trialInstance.getUpdatedAt();
		if (this.updatedAt != null) {
			this.updatedAtString=SDF.format(this.updatedAt);
		}
		this.regionShortName=trialInstance.getRegion().getShortName();
		
		
		this.trialPassword=trialInstance.getPassword();
		if (trialInstance.getUser()!= null) {
			this.username=trialInstance.getUser().getUsername();
		}
		this.type=trialInstance.getType().name();
		this.url=trialInstance.getUrl();

	}

	public ExportTrialInstanceDto(JSONObject jsonObject) {
		this.productShortName=jsonObject.optString("productShortName");
		this.domain=jsonObject.optString("domain");
		this.trialUsername=jsonObject.optString("trialUsername");
		this.guid=jsonObject.optString("guid");
		this.environmentId=jsonObject.optString("environmentId");
		this.expirationDate=getDate(jsonObject,"expirationDateString","expirationDate");
		this.expirationDateString=jsonObject.optString("expirationDateString");
		this.trialPassword=jsonObject.optString("trialPassword");
		this.username=jsonObject.optString("username");
		this.type=jsonObject.optString("type");
		this.url=jsonObject.optString("url");
		this.createdAt=getDate(jsonObject,"createdAtString","createdAt");
		this.createdAtString=jsonObject.optString("createdAtString");
		this.updatedAt=getDate(jsonObject,"updatedAtString","updatedAt");
		this.updatedAtString=jsonObject.optString("updatedAtString");
		this.regionShortName=jsonObject.optString("regionShortName");
		this.productVersionName=jsonObject.optString("productVersionName");
	}
	public String getProductShortName() {
		return productShortName;
	}

	public void setProductShortName(String productShortName) {
		this.productShortName = productShortName;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getTrialUsername() {
		return trialUsername;
	}

	public void setTrialUsername(String trialUsername) {
		this.trialUsername = trialUsername;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getEnvironmentId() {
		return environmentId;
	}

	public void setEnvironmentId(String environmentId) {
		this.environmentId = environmentId;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getExpirationDateString() {
		return expirationDateString;
	}

	public void setExpirationDateString(String expirationDateString) {
		this.expirationDateString = expirationDateString;
	}

	public String getTrialPassword() {
		return trialPassword;
	}

	public void setTrialPassword(String trialPassword) {
		this.trialPassword = trialPassword;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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
	
	
}
