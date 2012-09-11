package com.infor.cloudsuite.dto.export.v2;

import com.amazonaws.util.json.JSONObject;
import com.infor.cloudsuite.dto.export.AbstractExportDto;
import com.infor.cloudsuite.entity.AmazonCredentials;

public class ExportAmazonCredentialsDto extends AbstractExportDto{

	private String awsKey;
	private String name;
	private String secretKey;
	private String username;

	public ExportAmazonCredentialsDto() {
		
	}
	
	public ExportAmazonCredentialsDto(AmazonCredentials amazonCredentials) {
		
		this.awsKey=amazonCredentials.getAwsKey();
		this.name=amazonCredentials.getName();
		this.secretKey=amazonCredentials.getSecretKey();
		this.username=amazonCredentials.getUser().getUsername();
		
		
	}

	public ExportAmazonCredentialsDto(JSONObject jsonObject) {
		this.awsKey=jsonObject.optString("awsKey");
		this.name=jsonObject.optString("name");
		this.secretKey=jsonObject.optString("secretKey");
		this.username=jsonObject.optString("username");
		
	}
	public String getAwsKey() {
		return awsKey;
	}

	public void setAwsKey(String awsKey) {
		this.awsKey = awsKey;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	
	
}
