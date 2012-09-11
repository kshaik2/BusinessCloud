package com.infor.cloudsuite.entity;

import java.util.Date;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.infor.cloudsuite.service.StringDefs;

/**
 * User: bcrow
 * Date: 10/24/11 11:00 AM
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "guid"))
public class TrialInstance extends DatedDataEntity {
    private Long id;
    private String guid;
    private ProductVersion productVersion;
    private Region region;
    private TrialInstanceType type;
    private User user;
    private String domain;
    private String environmentId;
    private String url;
    private String username;
    private String password;
    private Date expirationDate;
    private String name;
    

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

    @Column(length = 100)
    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    @ManyToOne
    public ProductVersion getProductVersion() {
		return productVersion;
	}

	public void setProductVersion(ProductVersion productVersion) {
		this.productVersion = productVersion;
	}

    @ManyToOne
    public Region getRegion() {
        return region;
    }



	public void setRegion(Region region) {
        this.region = region;
    }

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    public TrialInstanceType getType() {
        if (type == null) {
            type = TrialInstanceType.USER;
        }
        return type;
    }

    public void setType(TrialInstanceType type) {
        this.type = type;
    }
        
    @ManyToOne
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Column(length = 50)
    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Column(length=100)
    public String getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId(String evironmentId) {
        this.environmentId = evironmentId;
    }

    @Column(length=255)
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Column(length=50)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(length=50)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }


    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
