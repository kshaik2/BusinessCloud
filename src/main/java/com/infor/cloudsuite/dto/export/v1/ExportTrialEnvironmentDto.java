package com.infor.cloudsuite.dto.export.v1;
import com.infor.cloudsuite.entity.TrialEnvironment;

public class ExportTrialEnvironmentDto {

	private String environmentId;
	private String productShortName;
	private String url;
	
	private String username;
	private String password;
	
	public ExportTrialEnvironmentDto() {
		
	}
	
	public ExportTrialEnvironmentDto(TrialEnvironment trialEnvironment) {

		this.environmentId=trialEnvironment.getEnvironmentId();
		this.productShortName=trialEnvironment.getProductVersion().getProduct().getShortName();
		this.url=trialEnvironment.getUrl();
		
		this.username=trialEnvironment.getUsername();
		this.password=trialEnvironment.getPassword();
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
	

	
	
	
}
