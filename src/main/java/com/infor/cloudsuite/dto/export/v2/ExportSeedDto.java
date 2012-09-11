package com.infor.cloudsuite.dto.export.v2;
import java.util.ArrayList;
import java.util.List;

public class ExportSeedDto {

    private String version="VERSION_TWO_SEED";
    private List<ExportRegionDto> exportedRegions;
    private List<ExportProductDto> exportedProducts;
    private List<ExportProductDescriptionDto> exportedProductDescriptions;
    private List<ExportUserDto> exportedUsers;
    private List<ExportAmazonCredentialsDto> exportedAmazonCredentials;
    private List<ExportTrialEnvironmentDto> exportedTrialEnvironments;
    private List<ExportTrialProductChildDto> exportedTrialProductChildren;

	public ExportSeedDto() {
		
	}


	public String getVersion() {
		return version;
	}


	public void setVersion(String version) {
		this.version = version;
	}


	public List<ExportRegionDto> getExportedRegions() {
		if (exportedRegions==null) {
			exportedRegions=new ArrayList<>();
		}
		return exportedRegions;
	}


	public void setExportedRegions(List<ExportRegionDto> exportedRegions) {
		this.exportedRegions = exportedRegions;
	}


	public List<ExportProductDto> getExportedProducts() {
		if (exportedProducts==null) {
			exportedProducts=new ArrayList<>();
		}
		return exportedProducts;
	}


	public void setExportedProducts(List<ExportProductDto> exportedProducts) {
		this.exportedProducts = exportedProducts;
	}


	public List<ExportProductDescriptionDto> getExportedProductDescriptions() {
		if (exportedProductDescriptions==null) {
			exportedProductDescriptions=new ArrayList<>();
		}
		return exportedProductDescriptions;
	}


	public void setExportedProductDescriptions(List<ExportProductDescriptionDto> exportedProductDescriptions) {
		this.exportedProductDescriptions = exportedProductDescriptions;
	}


	public List<ExportUserDto> getExportedUsers() {
		if (exportedUsers == null) {
			exportedUsers=new ArrayList<>();
		}
		return exportedUsers;
	}


	public void setExportedUsers(List<ExportUserDto> exportedUsers) {
		this.exportedUsers = exportedUsers;
	}


	public List<ExportAmazonCredentialsDto> getExportedAmazonCredentials() {
		if (exportedAmazonCredentials==null) {
			exportedAmazonCredentials=new ArrayList<>();
		}
		return exportedAmazonCredentials;
	}


	public void setExportedAmazonCredentials(List<ExportAmazonCredentialsDto> exportedAmazonCredentials) {
		this.exportedAmazonCredentials = exportedAmazonCredentials;
	}


	public List<ExportTrialEnvironmentDto> getExportedTrialEnvironments() {
		if (exportedTrialEnvironments==null) {
			exportedTrialEnvironments=new ArrayList<>();
		}
		return exportedTrialEnvironments;
	}


	public void setExportedTrialEnvironments(List<ExportTrialEnvironmentDto> exportedTrialEnvironments) {
		this.exportedTrialEnvironments = exportedTrialEnvironments;
	}


	public List<ExportTrialProductChildDto> getExportedTrialProductChildren() {
		if (exportedTrialProductChildren==null) {
			exportedTrialProductChildren=new ArrayList<>();
		}
		return exportedTrialProductChildren;
	}


	public void setExportedTrialProductChildren(List<ExportTrialProductChildDto> exportedTrialProductChildren) {
		this.exportedTrialProductChildren = exportedTrialProductChildren;
	}
}
