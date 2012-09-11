package com.infor.cloudsuite.dto;

import com.infor.cloudsuite.entity.Company;

public class CompanyDto {

	
	private Long id;
	private Long industryId;
	private String inforId;
	private String name;
	private String notes;
	
	
	public CompanyDto() {
		
	}
	
	public CompanyDto(Long id, String inforId, String name, String notes, Long industryId) {
		
		this.id=id;
		this.inforId=inforId;
		this.name=name;
		this.notes=notes;
		this.industryId=industryId;
	}

	public CompanyDto(Company company) {
		this.id=company.getId();
		this.inforId=company.getInforId();
		this.name=company.getName();
		this.notes=company.getNotes();
		if (company.getIndustry() != null) {
			this.industryId=company.getIndustry().getId();
		}
		
	}
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}



	public Long getIndustryId() {
		return industryId;
	}

	public void setIndustryId(Long industryId) {
		this.industryId = industryId;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getInforId() {
		return inforId;
	}

	public void setInforId(String inforId) {
		this.inforId = inforId;
	}
	
	
}
