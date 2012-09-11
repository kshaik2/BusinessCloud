package com.infor.cloudsuite.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.infor.cloudsuite.service.StringDefs;

@Entity
public class DeploymentStackLog extends DatedDataEntity{

	String vpcId;
	Long id;
	String message;
	DeploymentStatus status;
	DeploymentState state;
	DeploymentStackLogAction logAction;
	Long deploymentStackId;
	
	
	public String getVpcId() {
		return vpcId;
	}

	public void setVpcId(String vpcId) {
		this.vpcId = vpcId;
	}

    @GenericGenerator(name = "sequenceGenerator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                @Parameter(name = "sequence_name", value = "DeploymentStackLog_SEQ"),
                @Parameter(name = "initial_value", value = StringDefs.SEQ_INITIAL_VALUE),
                @Parameter(name = "optimizer", value = StringDefs.SEQ_OPTIMIZER),
                @Parameter(name = "increment_size", value = StringDefs.SEQ_INCREMENT) })
    @Id
    @GeneratedValue(generator = "sequenceGenerator")
    public Long getId() {
    	return id;
    }

        
    public void setId(Long id) {
        this.id = id;
    }
    
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Enumerated(EnumType.STRING)
	public DeploymentStatus getStatus() {
		return status;
	}

	public void setStatus(DeploymentStatus status) {
		this.status = status;
	}
	@Enumerated(EnumType.STRING)
	public DeploymentState getState() {
		return state;
	}

	public void setState(DeploymentState state) {
		this.state = state;
	}

	public DeploymentStackLogAction getLogAction() {
		return logAction;
	}

	public void setLogAction(DeploymentStackLogAction logAction) {
		this.logAction = logAction;
	}


	public Long getDeploymentStackId() {
		return deploymentStackId;
	}

	public void setDeploymentStackId(Long deploymentStackId) {
		this.deploymentStackId = deploymentStackId;
	}

	@Override
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    public Date getCreatedAt() {
        return super.createdAt;
    }

    @Override
    @Temporal(TemporalType.TIMESTAMP)
    public Date getUpdatedAt() {
        return super.updatedAt;
    }
    

}
