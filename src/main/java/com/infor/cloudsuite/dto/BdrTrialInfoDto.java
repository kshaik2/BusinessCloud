package com.infor.cloudsuite.dto;

import java.util.Date;

import com.infor.cloudsuite.entity.TrialInstance;

/**
 * User: bcrow
 * Date: 12/29/11 9:56 AM
 */
public class BdrTrialInfoDto {
    private Long trialId;
    private String productShortName;
    private String productVersionName;
    private Date expirationDate;

    public BdrTrialInfoDto() {
    }

    public BdrTrialInfoDto(TrialInstance trialInstance) {
    	this.trialId=trialInstance.getId();
    	this.productShortName=trialInstance.getProductVersion().getProduct().getShortName();
    	this.productVersionName=trialInstance.getProductVersion().getName();
    	this.expirationDate=trialInstance.getExpirationDate();
    }
    public BdrTrialInfoDto(Long trialId, String productShortName, Date expirationDate) {
        this.trialId = trialId;
        this.productShortName = productShortName;
        this.expirationDate = expirationDate;
    }

    public Long getTrialId() {
        return trialId;
    }

    public void setTrialId(Long trialId) {
        this.trialId = trialId;
    }

    public String getProductShortName() {
        return productShortName;
    }

    public void setProductShortName(String productShortName) {
        this.productShortName = productShortName;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

	public String getProductVersionName() {
		return productVersionName;
	}

	public void setProductVersionName(String productVersionName) {
		this.productVersionName = productVersionName;
	}
    
}
