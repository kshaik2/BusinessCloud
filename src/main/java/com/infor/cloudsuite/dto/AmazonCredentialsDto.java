package com.infor.cloudsuite.dto;

import com.infor.cloudsuite.entity.AmazonCredentials;

public class AmazonCredentialsDto {
	private Long id;		
	private Long userId;
	private String name;
	private String awsKey;
	private String secretKey;
	public AmazonCredentialsDto() {
		
	}
	public AmazonCredentialsDto(Long id, Long userId, String name, String awsKey, String secretKey) {
		this.id = id;
		this.userId = userId;
		this.name = name;
		this.awsKey = awsKey;
		this.secretKey = secretKey;
	}
	
	public AmazonCredentialsDto(AmazonCredentials amazonCredentials) {
		this.id=amazonCredentials.getId();
		this.userId=amazonCredentials.getUser().getId();
		this.name=amazonCredentials.getName();
		this.awsKey=amazonCredentials.getAwsKey();
		this.secretKey=amazonCredentials.getSecretKey();
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAwsKey() {
		return awsKey;
	}
	public void setAwsKey(String awsKey) {
		this.awsKey = awsKey;
	}
	public String getSecretKey() {
		return secretKey;
	}
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
	
}
