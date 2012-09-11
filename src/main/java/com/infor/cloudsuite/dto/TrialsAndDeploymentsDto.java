package com.infor.cloudsuite.dto;

import java.util.ArrayList;
import java.util.List;

import com.infor.cloudsuite.entity.DeploymentStack;
import com.infor.cloudsuite.entity.TrialInstance;
import com.infor.cloudsuite.entity.User;

public class TrialsAndDeploymentsDto {

    private Long userId;
    private List<TrialDto> trials;
    private List<DeploymentStackDto> deployments;
	
	public TrialsAndDeploymentsDto() {
		
	}
	
	public TrialsAndDeploymentsDto(User user, List<TrialInstance> trialInstances, List<DeploymentStack> deploys) {
		
		this.userId=user.getId();
		
		for (TrialInstance trialInstance : trialInstances) {
		
			this.getTrials().add(new TrialDto(trialInstance));
		}
		
		for (DeploymentStack deploy : deploys) {
			this.getDeployments().add(new DeploymentStackDto(deploy));
		}
		
		
	}


	public Long getUserId() {
		return userId;
	}


	public void setUserId(Long userId) {
		this.userId = userId;
	}


	public List<TrialDto> getTrials() {
		if (trials==null) {
			trials=new ArrayList<>();
		}
		return trials;
	}


	public void setTrials(List<TrialDto> trials) {
		this.trials = trials;
	}


	public List<DeploymentStackDto> getDeployments() {
		if (deployments==null) {
			deployments=new ArrayList<>();
		}
		return deployments;
	}


	public void setDeployments(List<DeploymentStackDto> deploymentStacks) {
		this.deployments = deploymentStacks;
	}
	
	
}
