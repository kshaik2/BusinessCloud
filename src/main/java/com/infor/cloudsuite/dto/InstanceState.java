package com.infor.cloudsuite.dto;

public enum InstanceState {
    AVAILABLE("Available"),
    NOT_AVAILABLE("Not Available"),
    PENDING("Pending"),
    RUNNING("Running"),
    SHUTTING_DOWN("Shutting-down"),
    TERMINATED("Terminated"),
    STOPPING("Stopping"),
    STOPPED("Stopped");
   
    private String display;
    InstanceState(String display) {
        this.display=display;
    }
    
    public String toString() {
        return display;
    }
    
}
