package com.infor.cloudsuite.dto.export.v2;

import java.util.ArrayList;
import java.util.List;

public class ExportDataDto {
//main export object to return from method call
	String version="VERSION_TWO";
	List<ExportUserDto> exportedUsers;
	List<ExportTrialInstanceDto> exportedTrialInstances;
	List<ExportTrialEnvironmentDto> exportedTrialEnvironments;
	List<ExportDeploymentDto> exportedDeployments;
	List<ExportAmazonCredentialsDto> exportedAmazonCredentials;
	List<ExportUserTrackingDto> exportedUserTrackings;
	
	Boolean overwriteConflicts;
	
	
	public ExportDataDto() {
		
	}
	
	
	public String getVersion() {
		return version;
	}


	public void setVersion(String version) {
		this.version = version;
	}


	public List<ExportUserDto> getExportedUsers() {
		if (exportedUsers==null) {
			exportedUsers=new ArrayList<>();
		}
		return exportedUsers;
	}

	public void setExportedUsers(List<ExportUserDto> exportedUsers) {
		this.exportedUsers = exportedUsers;
	}

	public List<ExportTrialInstanceDto> getExportedTrialInstances() {
		if (exportedTrialInstances==null) {
			exportedTrialInstances=new ArrayList<>();
		}
		return exportedTrialInstances;
	}

	public void setExportedTrialInstances(List<ExportTrialInstanceDto> exportedTrialInstances) {
		this.exportedTrialInstances = exportedTrialInstances;
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

	public List<ExportDeploymentDto> getExportedDeployments() {
		if (exportedDeployments == null) {
			exportedDeployments=new ArrayList<>();
		}
		return exportedDeployments;
	}

	public void setExportedDeployments(List<ExportDeploymentDto> exportedDeployments) {
		this.exportedDeployments = exportedDeployments;
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


	public List<ExportUserTrackingDto> getExportedUserTrackings() {
		if (exportedUserTrackings==null) {
			exportedUserTrackings=new ArrayList<>();
		}
		return exportedUserTrackings;
	}


	public void setExportedUserTrackings(
			List<ExportUserTrackingDto> exportedUserTrackings) {
		this.exportedUserTrackings = exportedUserTrackings;
	}


	public Boolean getOverwriteConflicts() {
		return overwriteConflicts;
	}


	public void setOverwriteConflicts(Boolean overwriteConflicts) {
		this.overwriteConflicts = overwriteConflicts;
	}
	
	
}
