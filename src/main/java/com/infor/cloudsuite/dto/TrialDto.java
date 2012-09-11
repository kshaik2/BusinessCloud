package com.infor.cloudsuite.dto;

import java.util.Date;

import com.infor.cloudsuite.entity.TrialInstance;

/**
 * User: bcrow
 * Date: 10/24/11 2:39 PM
 */
public class TrialDto {
    private Long id;
    private String guid;
    private Long regionId = 0L; //default to NAM
    private Long productId;
    private Long productVersionId;
    private Long userId;
    private String environmentId;
    private String url;
    private String username;
    private String password;
    private Date expirationDate;
    private String comment;
	private Date createdAt;
	private Date updatedAt;
	private String name;
	private String domain;
	
	
    public TrialDto() {
    	
    }
    
    public TrialDto(TrialInstance trialInstance) {
    	this.id=trialInstance.getId();
    	this.guid=trialInstance.getGuid();
    	this.productId=trialInstance.getProductVersion().getProduct().getId();
    	this.productVersionId=trialInstance.getProductVersion().getId();
        this.regionId=(trialInstance.getRegion() == null) ? 0L: trialInstance.getRegion().getId();
    	this.environmentId = trialInstance.getEnvironmentId();
        this.userId=trialInstance.getUser().getId();
    	this.url=trialInstance.getUrl();
    	this.username=trialInstance.getUsername();
    	this.password=trialInstance.getPassword();
    	this.expirationDate=trialInstance.getExpirationDate();
    	this.createdAt=trialInstance.getCreatedAt();
    	if (trialInstance.getUpdatedAt() != null) {
    		this.updatedAt=trialInstance.getUpdatedAt();
    	}
    	this.name=trialInstance.getName();
    	this.domain=trialInstance.getDomain();
    	//environmentId and regionId not set here
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }
    
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId(String environmentId) {
        this.environmentId = environmentId;
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

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public Long getProductVersionId() {
		return productVersionId;
	}

	public void setProductVersionId(Long productVersionId) {
		this.productVersionId = productVersionId;
	}
    
    
}
