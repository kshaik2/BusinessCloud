package com.infor.cloudsuite.dto;

public class RegionDto {

    private Long id;
    private String name;
    private String shortName;
    private String cloudAlias;
    private String endPoint;
    private String regionType;
    
    public RegionDto(Long id, String shortName, String name, String endPoint, String cloudAlias, DeploymentType regionType) {
        this.id=id;
        this.name=name;
        this.shortName=shortName;
        this.cloudAlias=cloudAlias;
        this.endPoint=endPoint;
        if (regionType == null) regionType = DeploymentType.AWS;
        this.regionType=regionType.name();
    }
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getShortName() {
        return shortName;
    }
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
    public String getCloudAlias() {
        return cloudAlias;
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

    public String getRegionType() {
        return regionType;
    }

    public void setRegionType(String regionType) {
        this.regionType = regionType;
    }
    
}
