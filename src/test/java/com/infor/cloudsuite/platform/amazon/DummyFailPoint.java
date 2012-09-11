package com.infor.cloudsuite.platform.amazon;

import java.util.EnumSet;

import com.infor.cloudsuite.entity.DeploymentState;
import com.infor.cloudsuite.entity.DeploymentStatus;

public enum DummyFailPoint {

	CONNECT_TO_AMAZON,
	GRANT_AMI_PERMISSIONS(DeploymentState.DELETED,DeploymentStatus.UNKNOWN),
	WAIT_FOR_INSTANCES,
	CREATE_VPC(DeploymentState.UNKNOWN,DeploymentStatus.UNKNOWN),
	START_INSTANCES,
	STOP_INSTANCES,
	ELASTIC_IP_GET(DeploymentState.DELETED,DeploymentStatus.UNKNOWN),
	ELASTIC_IP_ASSIGN,
	STARTUP,
	SHUTDOWN,
	TEARDOWN, 
	WAIT_FOR_INITIALIZED, 
	CREATE_INSTANCES(DeploymentState.DELETED,DeploymentStatus.ROLLED_BACK),
	TAG_INSTANCES, 
	SET_EBS_FOR_DELETE, 
	GET_USER_DATA, 
	CLEANUP_BLOCK_STORAGE,
	WAIT_FOR_RDP(DeploymentState.AVAILABLE,DeploymentStatus.DEPLOYED_RDPDOWN);

	public static final EnumSet<DummyFailPoint> AUTO_TEST=EnumSet.of(
			CONNECT_TO_AMAZON,
			GRANT_AMI_PERMISSIONS,
			ELASTIC_IP_GET,
			CREATE_VPC,
			CREATE_INSTANCES,
			WAIT_FOR_RDP
			);
	DeploymentState expectedState=null;
	DeploymentStatus expectedStatus=null;
	boolean examineStateAndStatus=false;
	
	DummyFailPoint() {
		
	}
	DummyFailPoint(DeploymentState state, DeploymentStatus status) {
		this.expectedState=state;
		this.expectedStatus=status;
		this.examineStateAndStatus=true;
	}
	
	public DeploymentState getExpectedDeploymentState() {
		return expectedState;
	}
	
	public DeploymentStatus getExpectedDeploymentStatus() {
		return expectedStatus;
	}

	public boolean examineStateAndStatus() {
		return examineStateAndStatus;
	}
}
