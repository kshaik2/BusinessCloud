package com.infor.cloudsuite.entity;

import java.util.EnumSet;

public enum DeploymentState {

	AVAILABLE("Available"),
	NOT_AVAILABLE("Not Available"),
	DELETED("Deleted"),
	UNKNOWN("Unknown");
	/*
	CREATING("Creating"),
	CREATED("Created"),
	START_REQUEST("Start"),
	RUNNING("Running"),
	STOP_REQUEST("Stop"),
	STARTING("Starting"),
	START_INITING("Initializing"),
	STOPPING("Stopping"),
	STOPPED("Stopped"),
	RESTART_REQUEST("Restart"),
	DELETE_REQUEST("Delete"),
	DELETING("Deleting"),
	DELETED("Deleted")
	*/
	
	public static final EnumSet<DeploymentState> DEFAULT_EXCLUDES=EnumSet.of(DELETED);
	private String display;
	DeploymentState(String display) {
		this.display=display;
	}
	
	public String toString() {
		return display;
	}
}
