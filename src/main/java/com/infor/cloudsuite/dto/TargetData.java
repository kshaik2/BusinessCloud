package com.infor.cloudsuite.dto;

import java.util.Date;

public class TargetData {
    private Long userId;
    private Long amznCredId;
    private Long regionId;
    private String entityId;
    private String deploymentName;
    private String url; 
    private Date scheduledAtDate;

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public Long getAmznCredId() {
        return amznCredId;
    }
    public void setAmznCredId(Long amznCredId) {
        this.amznCredId = amznCredId;
    }
    public String getEntityId() {
        return entityId;
    }
    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }
    public String getDeploymentName() {
        return deploymentName;
    }
    public void setDeploymentName(String deploymentName) {
        this.deploymentName = deploymentName;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public Date getScheduledAtDate() {
        return scheduledAtDate;
    }
    public void setScheduledAtDate(Date scheduledAtDate) {
        this.scheduledAtDate = scheduledAtDate;
    }
    public Long getRegionId() {
        return regionId;
    }
    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }
}