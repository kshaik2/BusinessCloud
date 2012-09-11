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
import com.infor.cloudsuite.entity.Schedule;

@Component(value="stopStackTask")
@Scope("prototype")
public class StopStackTask extends AbstractScheduleTask{
    private final Logger logger=LoggerFactory.getLogger(StopStackTask.class);
    private long timeout=10000L;
    public StopStackTask(Long scheduleId) {
        super(scheduleId);
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

        DeploymentStack stack=deploymentStackDao.findById(deploymentStackId);
        try {
            AmazonCredentials amCred=stack.getAmazonCredentials();
            return stackAndInstanceManager.stopStack(amCred.getAwsKey(), amCred.getSecretKey(), stack.getVpcId(), stack.getRegion().getId() ).get();
        } catch (AmazonClientException | ExecutionException | InterruptedException e) {
            logger.error("Encountered exception while calling stopStack",e);
        }
        return false;
    }


    private static TaskReadyState getTaskReadyState(long deploymentStackId, DeploymentStackDao deploymentStackDao) {
        DeploymentStack stack = deploymentStackDao.findById(deploymentStackId);
        if (stack == null) {
            return TaskReadyState.NULL;
        }

        switch (stack.getDeploymentState()) {
        case DELETED :
            return TaskReadyState.CANCELED;
        case NOT_AVAILABLE:
            switch (stack.getDeploymentStatus()) {
            case STOPPED: 
            case TERMINATED:
            case TEARDOWN_FAILED: 
            case ROLLBACK_FAILED: 
            case ROLLED_BACK:
            case STOP_FAILED:
            case UNKNOWN:
                return TaskReadyState.CANCELED;

            default: break;

            }
            return TaskReadyState.TRANSITIONING;
        case UNKNOWN:
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
            return TaskReadyState.CANCELED;
        case AVAILABLE:
            return TaskReadyState.READY;

        default: return TaskReadyState.CANCELED;
        }
    }
}
