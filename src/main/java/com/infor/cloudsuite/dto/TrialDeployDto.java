package com.infor.cloudsuite.dto;

public class TrialDeployDto {

    private Boolean trial;
    private Boolean deployment;

    TrialDeployDto(Boolean trial, Boolean deployment) {
        this.trial = (trial == null) ? false : trial;
        this.deployment = (deployment == null) ? false : deployment;
    }

    public TrialDeployDto() {
    }

    public Boolean getTrial() {
        return trial;
    }

    public void setTrial(Boolean trial) {
        this.trial = trial;
    }

    public Boolean getDeployment() {
        return deployment;
    }

    public void setDeployment(Boolean deployment) {
        this.deployment = deployment;
    }
}