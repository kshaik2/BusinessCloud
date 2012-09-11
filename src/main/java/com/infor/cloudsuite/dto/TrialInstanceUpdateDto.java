package com.infor.cloudsuite.dto;

import java.util.Date;

import com.infor.cloudsuite.entity.TrialInstance;

public class TrialInstanceUpdateDto {

    private Long userId;
    private String userName;
    private String guid;
    private String domain;
    private String productShortName;
    private Long productId;    
    private Long productVersionId;
    private String productVersionName;
    
    private Integer daysToExtend;
    private Date updatedAt;
    private Long id;

    public TrialInstanceUpdateDto() {
        super();
    }
    
    public TrialInstanceUpdateDto(TrialInstance trialInstance) {
        if(trialInstance.getUser() != null) {
            this.userId = trialInstance.getUser().getId();
            this.userName = trialInstance.getUser().getUsername();
        }
        else {
            this.userId = null;
            this.userName = null;
        }
        
        this.guid = trialInstance.getGuid();
        this.domain = trialInstance.getDomain();
        if (trialInstance.getProductVersion()!=null) {
        	this.productVersionId=trialInstance.getProductVersion().getId();
        	this.productVersionName=trialInstance.getProductVersion().getName();
        	
        	if(trialInstance.getProductVersion().getProduct() != null) {
                this.productShortName = trialInstance.getProductVersion().getProduct().getShortName();
                this.productId = trialInstance.getProductVersion().getProduct().getId();
            }
            else {
                this.productShortName = null;
                this.productId = null;
            }
        }
        
        
        this.daysToExtend = null;
        this.updatedAt = null;
        this.id = trialInstance.getId();
    }
    
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getProductShortName() {
        return productShortName;
    }

    public void setProductShortName(String productShortName) {
        this.productShortName = productShortName;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getDaysToExtend() {
        return daysToExtend;
    }

    public void setDaysToExtend(Integer daysToExtend) {
        this.daysToExtend = daysToExtend;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long instanceId) {
        this.id = instanceId;
    }

	public Long getProductVersionId() {
		return productVersionId;
	}

	public void setProductVersionId(Long productVersionId) {
		this.productVersionId = productVersionId;
	}

	public String getProductVersionName() {
		return productVersionName;
	}

	public void setProductVersionName(String productVersionName) {
		this.productVersionName = productVersionName;
	}


}
