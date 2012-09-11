package com.infor.cloudsuite.dto.export.v3;

import com.amazonaws.util.json.JSONObject;
import com.infor.cloudsuite.dto.export.AbstractExportDto;
import com.infor.cloudsuite.entity.Industry;
public class ExportIndustryDto extends AbstractExportDto {

	private String name;
	private String description;
	
	public ExportIndustryDto() {
		
	}
	
	public ExportIndustryDto(Industry industry) {
		this.name=industry.getName();
		this.description=industry.getDescription();
		
		
	}
	
	public ExportIndustryDto(JSONObject industryJsonObject) {
		this.name=industryJsonObject.optString("name");
		this.description=industryJsonObject.optString("description");
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
