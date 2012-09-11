package com.infor.cloudsuite.dto.export.v2;

import com.amazonaws.util.json.JSONObject;
import com.infor.cloudsuite.dto.export.AbstractExportDto;
import com.infor.cloudsuite.entity.TrialProductChild;

public class ExportTrialProductChildDto extends AbstractExportDto {

	private String parentProductVersionName;
	private String childProductVersionName;
	private String regionShortName;
	private String childProductShortName;
	private String parentProductShortName;
	
	public ExportTrialProductChildDto() {
		
	}
	
	public ExportTrialProductChildDto(TrialProductChild trialProductChild) {
		this.parentProductVersionName=trialProductChild.getParentVersion().getName();
		this.childProductVersionName=trialProductChild.getChildVersion().getName();
		this.regionShortName=trialProductChild.getRegion().getShortName();
		this.childProductShortName=trialProductChild.getChildVersion().getProduct().getShortName();
		this.parentProductShortName=trialProductChild.getParentVersion().getProduct().getShortName();
		
	}
	
	public ExportTrialProductChildDto(JSONObject jsonObject) {
		this.parentProductVersionName=jsonObject.optString("parentProductVersionName");
		this.childProductVersionName=jsonObject.optString("childProductVersionName");
		this.regionShortName=jsonObject.optString("regionShortName");		
		this.parentProductShortName=jsonObject.optString("parentProductShortName");
		this.childProductShortName=jsonObject.optString("childProductShortName");
	}

	public String getParentProductVersionName() {
		return parentProductVersionName;
	}

	public void setParentProductVersionName(String parentProductVersionName) {
		this.parentProductVersionName = parentProductVersionName;
	}

	public String getChildProductVersionName() {
		return childProductVersionName;
	}

	public void setChildProductVersionName(String childProductVersionName) {
		this.childProductVersionName = childProductVersionName;
	}

	public String getRegionShortName() {
		return regionShortName;
	}

	public void setRegionShortName(String regionShortName) {
		this.regionShortName = regionShortName;
	}

	public String getChildProductShortName() {
		return childProductShortName;
	}

	public void setChildProductShortName(String childProductShortName) {
		this.childProductShortName = childProductShortName;
	}

	public String getParentProductShortName() {
		return parentProductShortName;
	}

	public void setParentProductShortName(String parentProductShortName) {
		this.parentProductShortName = parentProductShortName;
	}
	
	
}
