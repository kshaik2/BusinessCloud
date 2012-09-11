package com.infor.cloudsuite.dto;

/**
 * User: bcrow
 * Date: 12/2/11 3:03 PM
 */
public class TrialEnvironmentDto {
    private String product;
    private String environmentId;
    private String url;
    private String cloudAlias;

    public TrialEnvironmentDto() {

    }

    public TrialEnvironmentDto(String product, String environmentId, String url, String cloudAlias) {
        this.product = product;
        this.environmentId = environmentId;
        this.url = url;
        this.cloudAlias=cloudAlias;
    }
/*
    public TrialEnvironmentDto(String environmentId, String url) {
        this.environmentId = environmentId;
        this.url = url;
    }
*/
    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
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

	public String getCloudAlias() {
		return cloudAlias;
	}

	public void setCloudAlias(String cloudAlias) {
		this.cloudAlias = cloudAlias;
	}
    
    
}
