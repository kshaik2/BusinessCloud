package com.infor.cloudsuite.dto.export.v3;

import com.amazonaws.util.json.JSONObject;
import com.infor.cloudsuite.entity.Company;

public class ExportCompanyDto {

    private String name;
    private String industryName;
    private String inforId;
    private String notes;
	
	public ExportCompanyDto() {
		
	}
	
	public ExportCompanyDto(Company company) {

		this.name=company.getName();
		this.industryName=company.getIndustry().getName();
		this.inforId=company.getInforId();
		this.notes=company.getNotes();
	}
	
	public ExportCompanyDto(JSONObject jsonObject) {
		this.name=jsonObject.optString("name");
		this.industryName=jsonObject.optString("industryName");
		this.inforId=jsonObject.optString("inforId");
		this.notes=jsonObject.optString("notes");
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIndustryName() {
		return industryName;
	}

	public void setIndustryName(String industryName) {
		this.industryName = industryName;
	}

	public String getInforId() {
		return inforId;
	}

	public void setInforId(String inforId) {
		this.inforId = inforId;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	
	
}
