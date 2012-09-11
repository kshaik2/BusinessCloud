package com.infor.cloudsuite.entity;

public enum DeploymentStatus {

	DEPLOY_INITIATED("Created"),
	DEPLOYING("Deploying"),
	DEPLOYING_INSTANCES_CREATED("Starting"),
	DEPLOYING_INSTANCES_STARTED("Initializing"),
	DEPLOYED("Deployed"),
	TERMINATING("Terminating"),
	TERMINATED("Terminated"),
	UNKNOWN("Unknown"),
	ROLLING_BACK("Rolling back"),
	ROLLED_BACK("Rolled back"),
	ROLLBACK_FAILED("Rollback FAILED"), 
	DEPLOYED_RDPDOWN("Deployed"),//adds below
	STOPPING("Stopping"),
	STOPPED("Stopped"),
	STOP_FAILED("Stop failed"),
	STARTING("Starting"),
	STARTED("Started"),
	START_FAILED("Start failed"),
	INITIALIZING("Initializing"),
	TEARDOWN_FAILED("Teardown failed");

    private String name;

    DeploymentStatus(String name){
		this.name=name;
	}
	public String toString(){
		return name;
	}
}
