package com.infor.cloudsuite.dto.export.v1;

public class ExportUserProductDto {

	private String productShortName;
	private Boolean trialAvailable;
	private Boolean launchAvailable;

	public ExportUserProductDto() {
		
	}
	
	public ExportUserProductDto(String productShortName, Boolean trialAvailable, Boolean launchAvailable) {
		this.productShortName=productShortName;
		this.trialAvailable=trialAvailable;
		this.launchAvailable=launchAvailable;
		
	}


	public String getProductShortName() {
		return productShortName;
	}

	public void setProductShortName(String productShortName) {
		this.productShortName = productShortName;
	}

	public Boolean getTrialAvailable() {
		return trialAvailable;
	}

	public void setTrialAvailable(Boolean trialAvailable) {
		this.trialAvailable = trialAvailable;
	}

	public Boolean getLaunchAvailable() {
		return launchAvailable;
	}

	public void setLaunchAvailable(Boolean launchAvailable) {
		this.launchAvailable = launchAvailable;
	}
	
	
}
