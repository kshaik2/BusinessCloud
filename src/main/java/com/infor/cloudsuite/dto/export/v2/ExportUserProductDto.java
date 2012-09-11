package com.infor.cloudsuite.dto.export.v2;

import com.amazonaws.util.json.JSONObject;
import com.infor.cloudsuite.dto.export.AbstractExportDto;
import com.infor.cloudsuite.entity.UserProduct;

public class ExportUserProductDto extends AbstractExportDto{

	private String productShortName;
	private Boolean trialAvailable;
	private Boolean launchAvailable;
	private Boolean owned;

	public ExportUserProductDto() {
		
	}
	
	public ExportUserProductDto(UserProduct userProduct) {
		this.productShortName=userProduct.getId().getProduct().getShortName();
		this.trialAvailable=userProduct.getTrialAvailable();
		this.launchAvailable=userProduct.getLaunchAvailable();
		this.owned=userProduct.getOwned();
		
	}
	
	public ExportUserProductDto(JSONObject jsonObject) {
		this.productShortName=jsonObject.optString("productShortName");
		this.trialAvailable=jsonObject.optBoolean("trialAvailable");
		this.launchAvailable=jsonObject.optBoolean("launchAvailable");
		this.owned=jsonObject.optBoolean("owned");
		
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

	public Boolean getOwned() {
		return owned;
	}

	public void setOwned(Boolean owned) {
		this.owned = owned;
	}
	
	
}
