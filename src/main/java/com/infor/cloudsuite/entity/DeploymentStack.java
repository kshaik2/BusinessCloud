package com.infor.cloudsuite.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.infor.cloudsuite.service.StringDefs;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "vpcId" }) })
public class DeploymentStack extends DatedDataEntity {

	private User user;
	private User createdByUser;
	
	private String deploymentName;
    private String vpcId;
	private String elasticIp;
	private DeploymentStatus deploymentStatus;
	private DeploymentState deploymentState;

	private Long id;
	private AmazonCredentials amazonCredentials;
	private Region region;
	
	private List<ProductVersion> deployedProductVersions;
	private String url;
	private String vpcUsername="gdeinfor2\\Administrator";
	private String vpcPassword="G!oba!08";
	
	private Integer numServers;
	private Long scheduleId;
	private Date scheduledStopAt;
	
	@OneToOne	
	public User getUser() {
		return user;
	}

	
	public void setUser(User user) {
		this.user = user;
	}

	@OneToOne	
	public User getCreatedByUser() {
		return createdByUser;
	}

	public void setCreatedByUser(User createdByUser) {
		this.createdByUser = createdByUser;
	}

    public String getDeploymentName() {
        return deploymentName;
    }

    public void setDeploymentName(String deploymentName) {
        this.deploymentName = deploymentName;
    }

    public String getVpcId() {
		return vpcId;
	}

	public void setVpcId(String vpcId) {
		this.vpcId = vpcId;
	}

	public String getElasticIp() {
		return elasticIp;
	}

	public void setElasticIp(String elasticIp) {
		this.elasticIp = elasticIp;
	}
	
	@Enumerated(EnumType.STRING)
	public DeploymentStatus getDeploymentStatus() {
		return deploymentStatus;
	}

	public void setDeploymentStatus(DeploymentStatus deploymentStatus) {
		this.deploymentStatus = deploymentStatus;
	}
	
	@Enumerated(EnumType.STRING)
	public DeploymentState getDeploymentState() {
		return deploymentState;
	}

	public void setDeploymentState(DeploymentState deploymentState) {
		this.deploymentState = deploymentState;
	}

    @GenericGenerator(name = "sequenceGenerator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                @Parameter(name = "sequence_name", value = "DeploymentStack_SEQ"),
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


    @OneToOne
	public AmazonCredentials getAmazonCredentials() {
		return amazonCredentials;
	}


	public void setAmazonCredentials(AmazonCredentials amazonCredentials) {
		this.amazonCredentials = amazonCredentials;
	}


	@ManyToMany
	public List<ProductVersion> getDeployedProductVersions() {
		if (deployedProductVersions==null) {
			deployedProductVersions=new ArrayList<>();
		}
		return deployedProductVersions;
	}


	public void setDeployedProductVersions(List<ProductVersion> deployedProductVersions) {
		this.deployedProductVersions = deployedProductVersions;
	}


	@OneToOne
	public Region getRegion() {
		return region;
	}


	public void setRegion(Region region) {
		this.region = region;
	}


	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
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


	public Date getScheduledStopAt() {
		return scheduledStopAt;
	}


	public void setScheduledStopAt(Date scheduledStopAt) {
		this.scheduledStopAt = scheduledStopAt;
	}
    
    
}
