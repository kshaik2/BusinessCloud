package com.infor.cloudsuite.task;

import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.amazonaws.AmazonClientException;
import com.infor.cloudsuite.dao.DeploymentStackDao;
import com.infor.cloudsuite.entity.AmazonCredentials;
import com.infor.cloudsuite.entity.DeploymentStack;
import com.infor.cloudsuite.entity.DeploymentState;
import com.infor.cloudsuite.entity.DeploymentStatus;
import com.infor.cloudsuite.entity.Schedule;

@Component(value="terminateStackTask")
@Scope("prototype")
public class TerminateStackTask extends AbstractScheduleTask {
	private final Logger logger=LoggerFactory.getLogger(TerminateStackTask.class);
	private long timeout=10000L;
	
	
	public TerminateStackTask(Long scheduleId) {
		super(scheduleId);
	}
	
	public TerminateStackTask(Long scheduleId, long timeout) {
		this(scheduleId);
		this.timeout=timeout;
	}

	@Override
	public boolean runTask(Schedule schedule) {
		logger.info(">>run() called for entityId:"+schedule.getEntityId());
		
		long deploymentStackId=Long.parseLong(schedule.getEntityId());
		TaskReadyState readyState;
		do {
			readyState=getTaskReadyState(deploymentStackId,deploymentStackDao);
			if (readyState==TaskReadyState.NULL || readyState==TaskReadyState.CANCELED) {
				break;
			}
			try {
				Thread.sleep(timeout);
			} catch (InterruptedException e) {
                logger.error("Encountered exception while sleeping in loop",e);
			}
			
		} while (readyState != TaskReadyState.READY);
		

		
		if (readyState==TaskReadyState.NULL || readyState==TaskReadyState.CANCELED) {
			return false;
		}
		


		boolean runResult=false;
		Exception except=null;
		DeploymentStack stack=deploymentStackDao.findById(deploymentStackId);
		boolean override=(DeploymentState.UNKNOWN==stack.getDeploymentState());
		//&& DeploymentStatus.UNKNOWN==stack.getDeploymentStatus());
		
		try {
			AmazonCredentials amCred=stack.getAmazonCredentials();
			stack.setDeploymentStatus(DeploymentStatus.TERMINATING);
			stack.setDeploymentState(DeploymentState.NOT_AVAILABLE);
			deploymentStackDao.save(stack);
			
			String result=stackAndInstanceManager.terminateStack(amCred.getAwsKey(), amCred.getSecretKey(), stack.getVpcId(), stack.getRegion().getId()  ).get();
			runResult=("Done".equalsIgnoreCase(result));
		} catch (AmazonClientException | ExecutionException | InterruptedException e) {
            except=e;
			logger.error("Encountered exception while calling stopStack",e);
		}
		if (!runResult) {
			//ran, but w/o success
			if (except==null) {
				logger.error("Failed at tear down with no exception for:"+deploymentStackId);
			} else {
				logger.error("Exception thrown while tearing down stack for:"+deploymentStackId,except);
			}

		}
		if (override) {
			stack.setDeploymentState(DeploymentState.DELETED);
			
			deploymentStackDao.save(stack);
			
		}
		return runResult;
	}
	
	public static TaskReadyState getTaskReadyState(Long deploymentStackId, DeploymentStackDao deploymentStackDao) {

		DeploymentStack stack = deploymentStackDao.findById(deploymentStackId);
		if (stack == null) {
			return TaskReadyState.NULL;
		}
		
		switch (stack.getDeploymentState()) {
			case DELETED :
				return TaskReadyState.CANCELED;
			case NOT_AVAILABLE:
				switch (stack.getDeploymentStatus()) {
					case ROLLING_BACK:
					case STOPPING:
					case INITIALIZING:
					case STARTING:
					case DEPLOYING:
					case DEPLOYING_INSTANCES_STARTED:
					case DEPLOYING_INSTANCES_CREATED:
						return TaskReadyState.TRANSITIONING;
				
					default:break;
				}
				return TaskReadyState.READY;
			case AVAILABLE:
			case UNKNOWN:
				return TaskReadyState.READY;
						
			default: return TaskReadyState.CANCELED;
			}
		
	}

}
