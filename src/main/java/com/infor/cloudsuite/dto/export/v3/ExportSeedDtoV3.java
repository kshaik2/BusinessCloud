package com.infor.cloudsuite.dto.export.v3;

import java.util.ArrayList;
import java.util.List;

public class ExportSeedDtoV3 extends com.infor.cloudsuite.dto.export.v2.ExportSeedDto{

	private List<ExportAmiDescriptorDto> exportedAmiDescriptors;
	private List<ExportIndustryDto> exportedIndustryDtos;
	private List<ExportCompanyDto> exportedCompanies;
	
	public ExportSeedDtoV3() {
		
	}
	
	public List<ExportAmiDescriptorDto> getExportedAmiDescriptors() {
		if (exportedAmiDescriptors==null) {
			exportedAmiDescriptors=new ArrayList<>();
		}
		return exportedAmiDescriptors;
	}
	
	public void setExportedAmiDescriptors(List<ExportAmiDescriptorDto> exportedAmiDescriptors) {
		this.exportedAmiDescriptors = exportedAmiDescriptors;
	}
	
	
	public List<ExportIndustryDto> getExportedIndustryDtos() {
		if (exportedIndustryDtos == null) {
			exportedIndustryDtos=new ArrayList<>();
		}
		return exportedIndustryDtos;
	}
	public void setExportedIndustryDtos(List<ExportIndustryDto> exportedIndustryDtos) {
		this.exportedIndustryDtos = exportedIndustryDtos;
	}

	public List<ExportCompanyDto> getExportedCompanies() {
		if (exportedCompanies==null) {
			exportedCompanies=new ArrayList<>();
		}
		return exportedCompanies;
	}

	public void setExportedCompanies(List<ExportCompanyDto> exportedCompanies) {
		this.exportedCompanies = exportedCompanies;
	}
	
	
	
}
