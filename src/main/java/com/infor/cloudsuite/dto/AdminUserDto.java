package com.infor.cloudsuite.dto;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.infor.cloudsuite.entity.Role;
import com.infor.cloudsuite.entity.User;
import com.infor.cloudsuite.entity.UserProduct;

/**
 * User: bcrow
 * Date: 11/8/11 10:51 AM
 */
public class AdminUserDto {
    private static final Logger logger = LoggerFactory.getLogger(AdminUserDto.class);

    private Long id;
    private User user;
    private String role;
    private LoginAgg loginAgg;
    private List<DeploymentStackDto> deployments;
    private CompanyDto company;
    private List<Long> deployAvailable;
    private List<Long> trialAvailable;
    
    public AdminUserDto(User user, List<DeploymentStackDto> deploymentStacks, LoginAgg loginAgg) {
        this.id = user.getId();
        this.user = user;

        //Do not like to do this. But the web front end is only handling one
        //role at a time.
        //noinspection LoopStatementThatDoesntLoop
        for (Role userRole : user.getRoles()) {
            this.role = userRole.toString();
            break;
        }
        logger.debug("User products: " + user.getUserProducts().size());
        this.getDeployments().addAll(deploymentStacks);
        
        this.loginAgg = loginAgg;
        if (user.getCompany() != null) {
        	this.company=new CompanyDto(user.getCompany());
        }
        
        for (UserProduct up : user.getUserProducts().values()) {
        	if (up.getLaunchAvailable()) {
        		getDeployAvailable().add(up.getProduct().getId());
        	}
        	if (up.getTrialAvailable()) {
        		getTrialAvailable().add(up.getProduct().getId());
        	}
        }
        
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


    public LoginAgg getLoginAgg() {
        return loginAgg;
    }

    public void setLoginAgg(LoginAgg loginAgg) {
        this.loginAgg = loginAgg;
    }

	public List<DeploymentStackDto> getDeployments() {
		if (deployments==null) {
			deployments=new ArrayList<>();
		}
		
		return deployments;
	}

	public void setDeployments(List<DeploymentStackDto> deployments) {
		this.deployments = deployments;
	}

	public CompanyDto getCompany() {
		return company;
	}

	public void setCompany(CompanyDto company) {
		this.company = company;
	}

	public List<Long> getDeployAvailable() {
		if (deployAvailable==null) {
			deployAvailable=new ArrayList<>();
		}
		return deployAvailable;
	}

	public void setDeployAvailable(List<Long> deployAvailable) {
		this.deployAvailable = deployAvailable;
	}

	public List<Long> getTrialAvailable() {
		if (trialAvailable==null) {
			trialAvailable=new ArrayList<>();
		}
		return trialAvailable;
	}

	public void setTrialAvailable(List<Long> trialAvailable) {
		this.trialAvailable = trialAvailable;
	}




   
}
