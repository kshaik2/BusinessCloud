package com.infor.cloudsuite.platform.amazon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.infor.cloudsuite.dao.DeploymentStackDao;
import com.infor.cloudsuite.dao.RegionDao;
import com.infor.cloudsuite.dto.DeploymentStackUpdateDto;
import com.infor.cloudsuite.entity.AmiDescriptor;
import com.infor.cloudsuite.entity.DeploymentStack;
import com.infor.cloudsuite.entity.DeploymentStackLogAction;
import com.infor.cloudsuite.entity.DeploymentState;
import com.infor.cloudsuite.entity.DeploymentStatus;
import com.infor.cloudsuite.platform.components.EmailProvider;
import com.infor.cloudsuite.platform.components.TemplateProvider;
import com.infor.cloudsuite.service.StringDefs;

/**
 * User: bcrow
 * Date: 6/12/12 10:57 AM
 */
@Component
public class StackBuilder {

    private static final Logger logger = LoggerFactory.getLogger(StackBuilder.class);

    @Resource
    private AwsOperations awsOperations;
    @Resource
    private TemplateProvider templateProvider;
    @Resource
    private EmailProvider emailProvider;
    @Resource
    private DeploymentStackListener stackListener;
    @Resource
    private DeploymentStackDao deploymentStackDao;
    @Resource
    private RegionDao regionDao;
    
    @Async
    public Future<Boolean> createStackAsync(CreateStackRequest createStackRequest){
    	return createStack(createStackRequest);
    }
    
    public Future<Boolean> createStack(CreateStackRequest createStackRequest) {

        StackResults results;

        DeploymentStack deploymentStack = createStackRequest.getDeploymentStack();
        String awsKey = deploymentStack.getAmazonCredentials().getAwsKey();
        String awsSecretKey = deploymentStack.getAmazonCredentials().getSecretKey();

        DeploymentStackUpdateDto dsud = new DeploymentStackUpdateDto();
        dsud.setState(DeploymentState.NOT_AVAILABLE);
        dsud.setDeploymentStackId(deploymentStack.getId());

        final BasicAWSCredentials awsCredentials = new BasicAWSCredentials(awsKey, awsSecretKey);
        final AmazonEC2 destEC2 = awsOperations.getAmazonEC2(awsCredentials, deploymentStack.getRegion().getId());

        Collection<AmiDescriptor> amisDescs = createStackRequest.getAmiDescriptors();
        if (amisDescs.size() == 0) {
            dsud.setAction(DeploymentStackLogAction.CHANGE_IN_STATE_STATUS);
            dsud.setState(DeploymentState.DELETED);
            dsud.setStatus(DeploymentStatus.UNKNOWN);
            final String message = "Failed before VPC creation: No deployable images available.";
            dsud.setMessage(message);
            stackListener.logAction(dsud);
            logger.error(message);
            results = new StackResults();
            results.setComplete(false);
            results.setException(message);
            results.setRollback(true);
            sendStackFailedRollBackEmail(results, createStackRequest);
            return new AsyncResult<>(false);
        }

        logger.info("Granting permission to AMIs...");
        AmiDescriptor theOneThatGetsEip = null;
        ElasticIp theEipThatOneGets;
        for (AmiDescriptor amiDescriptor : amisDescs) {
            if (amiDescriptor.getEipNeeded()) {
                theOneThatGetsEip = amiDescriptor;
                //TODO: Hardcoded string image name??
                if (amiDescriptor.getName().equals("GDE-IUX")) {
                    break;
                }
            }
        }

        if (theOneThatGetsEip == null) {
            //Failsafe to make sure a server gets an EIP.
            theOneThatGetsEip = amisDescs.iterator().next();
        }

        try {
            awsOperations.grantAmi(awsCredentials, amisDescs);
            theEipThatOneGets = awsOperations.getElasticIp(destEC2);
        } catch (AmazonClientException ace) {
            String message = ace.getMessage();
            logger.error("Error granting Ami, or getting Elasting IP. ", ace);

            logger.info("setting stack result");
            dsud.setAction(DeploymentStackLogAction.CHANGE_IN_STATE_STATUS);
            dsud.setState(DeploymentState.DELETED);
            dsud.setStatus(DeploymentStatus.UNKNOWN);
            dsud.setMessage("Failed before VPC creation:" + message);
            stackListener.logAction(dsud);
            logger.info("done setting stack result.");

            results = new StackResults();
            results.setComplete(false);
            results.setComplete(false);
            results.setException(message);
            results.setRollback(true);
            sendStackFailedRollBackEmail(results, createStackRequest);

            return new AsyncResult<>(false);
        }

        logger.info("Creating the VPC...");
        VpcDefinition vpcDef;
        results = new StackResults();
        try {
            vpcDef = awsOperations.createVPC(destEC2, awsCredentials, createStackRequest.getRegionName());
        	dsud.setAction(DeploymentStackLogAction.SET_VPC_ID);
        	dsud.setDeploymentStackId(deploymentStack.getId());
        	dsud.setMessage("Assigning a VPC id:" + vpcDef.getVpcId());
        	dsud.setVpcId(vpcDef.getVpcId());
        	logger.info("Vpc created.");
            stackListener.logAction(dsud);
        	results = finishStackCreate(vpcDef, destEC2, theOneThatGetsEip, theEipThatOneGets, amisDescs, deploymentStack.getDeploymentName());
            
            logger.info("Exited finishStackCreate");
 
            //deploymentStackDao.flush();
        	DeploymentStack finalDS = deploymentStackDao.findById(deploymentStack.getId());
            
            createStackRequest.setDeploymentStack(finalDS);
            
            logger.info("Set DeploymentStack object in createStackRequest");
            if (results != null && results.isComplete())
                sendStackCompleteEmail(createStackRequest);
            else
                sendStackFailedRollBackEmail(results, createStackRequest);

        } catch (AmazonClientException ace) {
            final String message = "Failed while creating the VPC:" + ace.getMessage();
            results.setComplete(false);
            results.setException(message);
            results.setRollback(false);
            sendStackFailedRollBackEmail(results, createStackRequest);

            dsud.setAction(DeploymentStackLogAction.CHANGE_IN_STATE_STATUS);
            dsud.setState(DeploymentState.UNKNOWN);
            dsud.setStatus(DeploymentStatus.UNKNOWN);
            dsud.setMessage(message);
            stackListener.logAction(dsud);

            logger.error("Failed while creating the VPC:", ace);
            return new AsyncResult<>(false);
        } catch (Exception e) {
        	logger.error("Unexpected exceptione encountered creating stack",e);
        }
        logger.info("Create Stack complete: " + (results != null && results.isComplete()));

        return new AsyncResult<>(results != null && results.isComplete());
    }

    
    void sendStackFailedRollBackEmail(StackResults results, CreateStackRequest createStackRequest) {
        try {
            logger.info("Sending failure emails...");
            String address = StringDefs.BUSINESSCLOUD_EMAIL;
            List<String> destEmails = new ArrayList<>(createStackRequest.getDestEmails());
            destEmails.add(address);
            address = getEmailString(destEmails);

            Locale locale = createStackRequest.getLocale();
            Map<String, Object> templateData = new HashMap<>();
            templateData.put("APPLICATIONS", createStackRequest.getProductNames());
            templateData.put("SERVER_COUNT", createStackRequest.getDeploymentStack().getNumServers());
            templateData.put("REGION", createStackRequest.getDeploymentStack().getRegion().getName());
            templateData.put("EXC_MESSAGE", results.getException());
            templateData.put("ROLLBACK", Boolean.valueOf(results.isRollback()).toString());
            templateData.put("ROLLBACK_EXC", results.getRollbackException());

            final String message = templateProvider.processTemplate("stackFailed.ftl", locale, templateData);

            emailProvider.sendEmail(address, "Deployment Failed.", message, true);
        } catch (Exception e) {
            logger.error("Error processing StackFailed email!", e);
        }

    }

    void sendStackCompleteEmail(CreateStackRequest createStackRequest) {
        logger.info("Sending success emails...");
    	Locale locale = createStackRequest.getLocale();
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("APPLICATIONS", createStackRequest.getProductNames());
        templateData.put("SERVER_COUNT", createStackRequest.getDeploymentStack().getNumServers());
        templateData.put("REGION", createStackRequest.getDeploymentStack().getRegion().getName());
        templateData.put("EIP", createStackRequest.getDeploymentStack().getElasticIp());
        templateData.put("URL", createStackRequest.getDeploymentStack().getUrl());
        templateData.put("ID", createStackRequest.getDeploymentStack().getVpcUsername());
        templateData.put("PASSWORD", createStackRequest.getDeploymentStack().getVpcPassword());

        final String message = templateProvider.processTemplate("stackCreated-confirmation.ftl", locale, templateData);

        String address = getEmailString(createStackRequest.getDestEmails());
        logger.info("To Address: " + address);

        emailProvider.sendEmail(address, "Deployment Complete.", message, true);
    }

    private String getEmailString(final List<String> destEmails) {
        String address;
        if (destEmails.size() > 1) {
            StringBuilder sb = new StringBuilder();
            boolean skip = true;
            for (String destEmail : destEmails) {
                if (skip) {
                    skip = false;
                } else {
                    sb.append(", ");
                }
                sb.append(destEmail);
            }
            address = sb.toString();
        } else {
            address = destEmails.get(0);
        }
        return address;
    }

    public StackResults finishStackCreate(VpcDefinition vpcDef, AmazonEC2 destEC2, AmiDescriptor eipAmi,
                                          ElasticIp eip, Collection<AmiDescriptor> amisDescs, String deploymentName) {

        StackResults results = new StackResults();

        DeploymentStackUpdateDto dsud = new DeploymentStackUpdateDto();
        dsud.setVpcId(vpcDef.getVpcId());

        try {
            String userData = null;
            logger.info("Creating EC2 Instances...");
            final Map<String, AmiDescriptor> instanceAmiMap = awsOperations.createInstances(destEC2, vpcDef, amisDescs, userData);
            //callback Instances created

            dsud.setAction(DeploymentStackLogAction.CHANGE_IN_STATE_STATUS);
            dsud.setState(DeploymentState.NOT_AVAILABLE);
            dsud.setStatus(DeploymentStatus.DEPLOYING_INSTANCES_CREATED);
            dsud.setMessage("All Instances created.");

            stackListener.logAction(dsud);


            logger.info("Waiting for instances to start...");
	
            awsOperations.waitForInstances(destEC2, instanceAmiMap.keySet());

            //callback instances running;
            dsud.setAction(DeploymentStackLogAction.CHANGE_IN_STATUS);
            dsud.setStatus(DeploymentStatus.DEPLOYING_INSTANCES_STARTED);
            dsud.setMessage("All instances started.");
            stackListener.logAction(dsud);


            logger.info("Tagging the instances...");
            awsOperations.tagInstances(destEC2, instanceAmiMap, deploymentName);
            logger.info("Setting EBS for delete on terminate...");
            awsOperations.setEBSforDelete(destEC2, instanceAmiMap.keySet());
            logger.info("Done setting EBS for delete on terminate...");

            if (eip != null && eipAmi != null) {
                String instanceId = null;
                for (Map.Entry<String, AmiDescriptor> entry : instanceAmiMap.entrySet()) {
                    AmiDescriptor ami = entry.getValue();

                    if (ami.getName().equals(eipAmi.getName())) {
                        instanceId = entry.getKey();
                    }
                }

                if (instanceId != null) {
                    awsOperations.associateElasticIp(destEC2, instanceId, eip.getAllocationId());

                    dsud.setAction(DeploymentStackLogAction.UPDATE_ELASTIC_IP);
                    dsud.setMessage(eip.getPublicIp());
                    stackListener.logAction(dsud);
                } else {
                    logger.error("No instanceId found for EIP?!");
                }
            }

            logger.info("Waiting for initialization...");
            awsOperations.waitForInitialized(destEC2, instanceAmiMap.keySet());
            //callback Initialized and ready.
            logger.info("Initialized, notifying listener.");
            dsud.setAction(DeploymentStackLogAction.CHANGE_IN_STATE_STATUS);
            dsud.setMessage("Initialized and Ready!");
            boolean rdpReady = false;
            if (eip != null) {
                rdpReady = awsOperations.waitForRdpService(eip);
            }
            if (!rdpReady) {
            	dsud.setState(DeploymentState.AVAILABLE);
            	dsud.setStatus(DeploymentStatus.DEPLOYED_RDPDOWN);
            	dsud.setMessage("Instance up, but RDP not reachable in allotted time");
            	//stackListener.logAction(dsud);
            } else {
            dsud.setState(DeploymentState.AVAILABLE);
            dsud.setStatus(DeploymentStatus.DEPLOYED);
            }
            stackListener.logAction(dsud);
            logger.info("Notified stack listener.");
            /*
            try {
            	logger.info("Notified stack listener:"+stackListener.logAction(dsud).get());
            } catch (Exception e) {
            	logger.error("Exception encountered notifying listener:"+e);
            }
            */
            logger.info("Success!");
            results.setComplete(true);

        } catch (Exception e) {
            //callback error encounterd..rollback initiated.
            results.setComplete(false);
            results.setRollback(true);
            results.setException(e.getMessage());

            dsud.setAction(DeploymentStackLogAction.CHANGE_IN_STATE_STATUS);
            dsud.setState(DeploymentState.UNKNOWN);
            dsud.setStatus(DeploymentStatus.ROLLING_BACK);
            dsud.setMessage("Failed to create server instances:" + e.getMessage());
            stackListener.logAction(dsud);

            logger.error("Failed to create server instances", e);
            try {
                logger.error("Rolling back vpc creation..");
                awsOperations.tearDownVPC(destEC2, vpcDef.getVpcId());
                logger.error("Done with VPC rollback.");
                //callback, rolled back
                dsud.setState(DeploymentState.DELETED);
                dsud.setStatus(DeploymentStatus.ROLLED_BACK);
                dsud.setMessage("Done w/ VPC rollback.");
                stackListener.logAction(dsud);
            } catch (Exception e1) {
                //callback error on rollback...send emails!!!!!
                results.setRollbackException(e1.getMessage());
                dsud.setState(DeploymentState.UNKNOWN);
                dsud.setStatus(DeploymentStatus.ROLLBACK_FAILED);
                dsud.setMessage("VPC Rollback failed.");
                stackListener.logAction(dsud);

                logger.error("VPC rollback failed.");
            }
        }
        return results;
    }

    @Async
    public Future<Boolean> stopStack(String awsKey, String awsSecretKey, String vpcId, Long regionId) {
        boolean ret;
        
        BasicAWSCredentials creds = new BasicAWSCredentials(awsKey, awsSecretKey);
        final AmazonEC2 amazonEC2 = awsOperations.getAmazonEC2(creds, regionId);

        DeploymentStackUpdateDto dsud = new DeploymentStackUpdateDto();
        dsud.setAction(DeploymentStackLogAction.CHANGE_IN_STATE);
        dsud.setMessage("Stopping stack instances for VPC:" + vpcId);
        dsud.setState(DeploymentState.NOT_AVAILABLE);
        dsud.setStatus(DeploymentStatus.STOPPING);
        dsud.setVpcId(vpcId);

        logger.info("Stopping Stack instances for VPC: " + vpcId);
        try {
            //command issued.
            stackListener.logAction(dsud);
            awsOperations.stopVpcInstances(amazonEC2, vpcId);
            //done.
            dsud.setState(DeploymentState.NOT_AVAILABLE);
            dsud.setStatus(DeploymentStatus.STOPPED);
            dsud.setMessage("Stack instances stopped for VPC:" + vpcId);
            stackListener.logAction(dsud);

            ret = true;
        } catch (Exception e) {
            //todo callback stop failed.
            logger.error("Stop Stack failed:", e);
            dsud.setState(DeploymentState.UNKNOWN);
            dsud.setStatus(DeploymentStatus.STOP_FAILED);
            dsud.setMessage("Error stopping stack instances for VPC:" + vpcId + ", " + e.getMessage());
            stackListener.logAction(dsud);
            ret = false;
        }
        logger.info("Done stopping");
        return new AsyncResult<>(ret);
    }

    @Async
    public Future<Boolean> startStack(String awsKey, String awsSecretKey, String vpcId, Long regionId) {
        BasicAWSCredentials creds = new BasicAWSCredentials(awsKey, awsSecretKey);
        final AmazonEC2 amazonEC2 = awsOperations.getAmazonEC2(creds, regionId);

        DeploymentStackUpdateDto dsud = this.getUpdateDtoForVpc(vpcId);//new DeploymentStackUpdateDto();
        dsud.setMessage("Starting stack instances for VPC:" + vpcId);
        dsud.setState(DeploymentState.NOT_AVAILABLE);
        dsud.setStatus(DeploymentStatus.STARTING);
        logger.info("Starting stack: " + vpcId);
        try {
            //Callback start started.
            stackListener.logAction(dsud);
            final List<String> instances = awsOperations.startVpcInstances(amazonEC2, vpcId);
            //callback started /initializing
            dsud.setMessage("Initializing stack instances for VPC:" + vpcId);
            dsud.setState(DeploymentState.NOT_AVAILABLE);
            dsud.setStatus(DeploymentStatus.INITIALIZING);
            stackListener.logAction(dsud);

            awsOperations.waitForInitialized(amazonEC2, instances);
            dsud.setMessage("Start/Init complete for VPC:" + vpcId);
            dsud.setState(DeploymentState.AVAILABLE);
            dsud.setStatus(DeploymentStatus.STARTED);
            //callback initialized/complete
            stackListener.logAction(dsud);
            logger.info("Start completed for:"+vpcId);
        } catch (Exception e) {
            // callback start failed.
        	logger.error("Error with start of stack:"+vpcId);
            dsud.setMessage("Error with start/init of VPC:" + vpcId + ", " + e.getMessage());
            dsud.setState(DeploymentState.UNKNOWN);
            dsud.setStatus(DeploymentStatus.START_FAILED);
            stackListener.logAction(dsud);
            return new AsyncResult<>(false);
        }
        logger.info("Done starting");
        return new AsyncResult<>(true);
    }

    @Async
    public Future<String> tearDownStack(String awsKey, String awsSecretKey, String vpcId, Long regionId) {
        BasicAWSCredentials creds = new BasicAWSCredentials(awsKey, awsSecretKey);
        final AmazonEC2 amazonEC2 = awsOperations.getAmazonEC2(creds, regionId);
        DeploymentStackUpdateDto dsud = this.getUpdateDtoForVpc(vpcId, DeploymentStackLogAction.CHANGE_IN_STATE_STATUS);
        dsud.setStatus(DeploymentStatus.TERMINATING);
        dsud.setState(DeploymentState.NOT_AVAILABLE);
        dsud.setMessage("Tearing down VPC:" + vpcId);
        logger.info("Tearing Down stack: " + vpcId);
        try {
            //Callback teardown started.
            stackListener.logAction(dsud);
            
            awsOperations.tearDownVPC(amazonEC2, vpcId);
            //callback teardown completed
            dsud.setStatus(DeploymentStatus.TERMINATED);
            dsud.setState(DeploymentState.DELETED);
            dsud.setMessage("Tear down of VPC:" + vpcId + " completed.");
            stackListener.logAction(dsud);
        } catch (Exception e) {

            logger.error("Teardown failed!", e);
            //callback teardown failed.
            dsud.setState(DeploymentState.UNKNOWN);
            dsud.setStatus(DeploymentStatus.TEARDOWN_FAILED);
            dsud.setMessage("Error encountered tearing down VPC:" + vpcId + ", " + e.getMessage());
            stackListener.logAction(dsud);
            return new AsyncResult<>("Failed");
        }
        logger.info("Done with teardown");
        return new AsyncResult<>("Done");
    }

    @Async
    public Future<Boolean> restartStack(String awsKey, String awsSecretKey, String vpcId, Long regionId) {


        try {

            boolean future = stopStack(awsKey, awsSecretKey, vpcId, regionId).get();
            if (future) {
                startStack(awsKey, awsSecretKey, vpcId, regionId);
            }
        } catch (AmazonClientException | ExecutionException | InterruptedException ee) {
            DeploymentStackUpdateDto dsud = this.getUpdateDtoForVpc(vpcId);//new DeploymentStackUpdateDto();
            dsud.setState(DeploymentState.UNKNOWN);
            dsud.setMessage("Exception occurred when restarting VPC:" + vpcId + ", " + ee.getMessage());
            stackListener.logAction(dsud);

        }

    	return new AsyncResult<>(false);
    }

    protected DeploymentStackUpdateDto getUpdateDtoForVpc(String vpcId, DeploymentStackLogAction logAction) {
        DeploymentStackUpdateDto dsud = new DeploymentStackUpdateDto();
        dsud.setVpcId(vpcId);
        dsud.setAction(logAction);
        return dsud;
    }

    protected DeploymentStackUpdateDto getUpdateDtoForVpc(String vpcId) {
        return getUpdateDtoForVpc(vpcId, DeploymentStackLogAction.CHANGE_IN_STATE);
    }

    public void setEmailProvider(EmailProvider provider) {
    	
        this.emailProvider = provider;
        logger.info(this.getClass().getName()+":EmailProvider changed to class:"+provider.getClass().getName()+", is:"+this.emailProvider.getClass().getName());
    }

	public void setAwsOperations(AwsOperations awsOperations) {
		this.awsOperations=awsOperations;
	}

	public DeploymentStackListener getStackListener() {
		return stackListener;
	}

	public void setStackListener(DeploymentStackListener stackListener) {
		this.stackListener = stackListener;
	}

	public AwsOperations getAwsOperations() {
		return awsOperations;
	}

	public EmailProvider getEmailProvider() {
		return emailProvider;
	}
	
	
}
