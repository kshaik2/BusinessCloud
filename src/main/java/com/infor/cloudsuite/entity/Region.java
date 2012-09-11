package com.infor.cloudsuite.entity;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.infor.cloudsuite.dto.DeploymentType;

/**
 * User: bcrow
 * Date: 3/19/12 10:37 AM
 */
@Entity
@Cacheable
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "shortName"))
public class Region implements Serializable {
    
    private Long id;
    private String shortName;
    private String name;
    private String cloudAlias;
    private String endPoint; 
    private DeploymentType regionType;

    @Id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(length = 10)
    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @Column(length = 100)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    @Column(length=50) 
    public String getCloudAlias() {
    	return this.cloudAlias;
    }

	public void setCloudAlias(String cloudAlias) {
		this.cloudAlias = cloudAlias;
	}

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    @Enumerated(EnumType.STRING)
    public DeploymentType getRegionType() {
        return regionType;
    }

    public void setRegionType(DeploymentType regionType) {
        this.regionType = regionType;
    }
    
    
}
