package com.infor.cloudsuite.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.infor.cloudsuite.entity.DeploymentStack;
import com.infor.cloudsuite.entity.DeploymentState;
import com.infor.cloudsuite.entity.DeploymentStatus;
import com.infor.cloudsuite.entity.ProductVersion;
import com.infor.cloudsuite.entity.TrialInstance;
import com.infor.cloudsuite.entity.User;

public class DeploymentStackDto {

	private Long id;
	private Long amazonCredentialsId;
	
	private Date createdAt;
	private Date updatedAt;

	private UserSummaryDto createdByUser;
	private UserSummaryDto user;
	private String deploymentState;
	private String deploymentStatus;
	
	private String elasticIp;
	private String vpcId;
	
	private List<ProductVersionDto> deployedProductVersions;
	
	private Long regionId;

    private String deploymentName;
	
	private String deploymentUrl;
	private String vpcUsername;
	private String vpcPassword;
	
	private Date lastStartedAt;
	private Integer numServers;
	private Long scheduleId;
	private Date scheduledStopAt; 
	private DeploymentType deploymentType;
	private String trialDomain;
	
	public DeploymentStackDto() {
		
	}
	
	public DeploymentStackDto(TrialDto trialDto, ProductVersion productVersion, User createdBy, User user) {
		this.numServers=1;
		this.deploymentType=DeploymentType.INFOR24;
		this.regionId=trialDto.getRegionId();
		this.getDeployedProductVersions().add(new ProductVersionDto(productVersion));
		this.createdByUser=new UserSummaryDto(createdBy);
		this.user=new UserSummaryDto(user);
		this.createdAt=trialDto.getCreatedAt();
		this.lastStartedAt=trialDto.getCreatedAt();
		this.updatedAt=trialDto.getUpdatedAt();
		this.scheduledStopAt=trialDto.getExpirationDate();
		this.deploymentName=trialDto.getName();
		this.trialDomain=trialDto.getDomain();
		this.deploymentUrl=trialDto.getUrl();
		this.deploymentState=DeploymentState.AVAILABLE.toString();
		this.deploymentStatus=DeploymentStatus.STARTED.toString();
		this.id=trialDto.getId();
		this.vpcUsername=trialDto.getUsername();
		this.vpcPassword=trialDto.getPassword();
		
	}
	public DeploymentStackDto(TrialInstance trialInstance) {
		this.numServers=1;
		this.deploymentType=DeploymentType.INFOR24;
		this.regionId=trialInstance.getRegion().getId();
		this.scheduledStopAt=trialInstance.getExpirationDate();
		this.createdAt=trialInstance.getCreatedAt();
		this.updatedAt=trialInstance.getUpdatedAt();
		this.user=new UserSummaryDto(trialInstance.getUser());
		this.createdByUser=this.user;
		this.getDeployedProductVersions().add(new ProductVersionDto(trialInstance.getProductVersion()));
		this.lastStartedAt=this.createdAt;
		this.deploymentState=DeploymentState.AVAILABLE.toString();
		this.deploymentStatus=DeploymentStatus.STARTED.toString();
		
		this.deploymentUrl=trialInstance.getUrl();
		this.vpcUsername=trialInstance.getUsername();
		this.vpcPassword=trialInstance.getPassword();
		this.deploymentName=trialInstance.getName();
		this.trialDomain=trialInstance.getDomain();
		this.id=trialInstance.getId();
	}
	public DeploymentStackDto(DeploymentStack deploymentStack) {
		this.deploymentType=DeploymentType.AWS;
		this.id=deploymentStack.getId();
		this.createdAt=deploymentStack.getCreatedAt();
		this.createdByUser=new UserSummaryDto(deploymentStack.getCreatedByUser());
		this.user=new UserSummaryDto(deploymentStack.getUser());
		this.deploymentName = deploymentStack.getDeploymentName();
        this.deploymentState=deploymentStack.getDeploymentState().toString();
		this.deploymentStatus=deploymentStack.getDeploymentStatus().toString();
		this.elasticIp=deploymentStack.getElasticIp();
		this.amazonCredentialsId=deploymentStack.getAmazonCredentials().getId();
		this.updatedAt=deploymentStack.getUpdatedAt();
		this.vpcId=deploymentStack.getVpcId();
		this.numServers=deploymentStack.getNumServers();
		if (deploymentStack.getDeployedProductVersions()!=null) {
			for (ProductVersion productVersion : deploymentStack.getDeployedProductVersions()) {
				getDeployedProductVersions().add(new ProductVersionDto(productVersion));
			}
		}

		if(deploymentStack.getRegion() != null) {
		    this.regionId=deploymentStack.getRegion().getId();
		}
		
		this.deploymentUrl=deploymentStack.getUrl();
		this.vpcUsername=deploymentStack.getVpcUsername();
		this.vpcPassword=deploymentStack.getVpcPassword();
		this.scheduleId=deploymentStack.getScheduleId();
		this.scheduledStopAt = deploymentStack.getScheduledStopAt();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getAmazonCredentialsId() {
		return amazonCredentialsId;
	}

	public void setAmazonCredentialsId(Long amazonCredentialsId) {
		this.amazonCredentialsId = amazonCredentialsId;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	
	public Date getScheduledStopAt() {
		return scheduledStopAt;
	}

	public void setScheduledStopAt(Date scheduledStopAt) {
		this.scheduledStopAt = scheduledStopAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public UserSummaryDto getCreatedByUser() {
		return createdByUser;
	}

	public void setCreatedByUser(UserSummaryDto createdByUser) {
		this.createdByUser = createdByUser;
	}

	public UserSummaryDto getUser() {
		return user;
	}

	public void setUser(UserSummaryDto user) {
		this.user = user;
	}

    public String getDeploymentName() {
        return deploymentName;
    }

    public void setDeploymentName(String deploymentName) {
        this.deploymentName = deploymentName;
    }

    public String getDeploymentState() {
		return deploymentState;
	}

	public void setDeploymentState(String deploymentState) {
		this.deploymentState = deploymentState;
	}

	public String getDeploymentStatus() {
		return deploymentStatus;
	}

	public void setDeploymentStatus(String deploymentStatus) {
		this.deploymentStatus = deploymentStatus;
	}

	public String getElasticIp() {
		return elasticIp;
	}

	public void setElasticIp(String elasticIp) {
		this.elasticIp = elasticIp;
	}

	public String getVpcId() {
		return vpcId;
	}

	public void setVpcId(String vpcId) {
		this.vpcId = vpcId;
	}


	public Long getRegionId() {
		return regionId;
	}

	public void setRegionId(Long regionId) {
		this.regionId = regionId;
	}


	public String getDeploymentUrl() {
		return deploymentUrl;
	}

	public void setDeploymentUrl(String deploymentUrl) {
		this.deploymentUrl = deploymentUrl;
	}

	public String getVpcUsername() {
		return vpcUsername;
	}

	public void setVpcUsername(String vpcUsername) {
		this.vpcUsername = vpcUsername;
	}

	public String getVpcPassword() {
		return vpcPassword;
	}

	public void setVpcPassword(String vpcPassword) {
		this.vpcPassword = vpcPassword;
	}

	public Date getLastStartedAt() {
		return lastStartedAt;
	}

	public void setLastStartedAt(Date lastStartedAt) {
		this.lastStartedAt = lastStartedAt;
	}

	public Integer getNumServers() {
		return numServers;
	}

	public void setNumServers(Integer numServers) {
		this.numServers = numServers;
	}

	public Long getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(Long scheduleId) {
		this.scheduleId = scheduleId;
	}
	public DeploymentType getDeploymentType() {
		return deploymentType;
	}
	public void setDeploymentType(DeploymentType deploymentType) {
		this.deploymentType = deploymentType;
	}
	public String getTrialDomain() {
		return trialDomain;
	}
	public void setTrialDomain(String trialDomain) {
		this.trialDomain = trialDomain;
	}

	public List<ProductVersionDto> getDeployedProductVersions() {
		if (deployedProductVersions==null) {
			deployedProductVersions=new ArrayList<>();
		}
		return deployedProductVersions;
	}

	public void setDeployedProductVersions(List<ProductVersionDto> deployedProductVersions) {
		this.deployedProductVersions = deployedProductVersions;
	}
	
	
	
}
