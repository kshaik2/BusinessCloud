package com.infor.cloudsuite.platform.amazon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.infor.cloudsuite.entity.AmiDescriptor;
import com.infor.cloudsuite.platform.components.GuidProvider;

@Component
public class DummyAwsOperations extends AwsOperations {

    private static final Logger logger = LoggerFactory.getLogger(DummyAwsOperations.class);
	@Resource
	GuidProvider guidProvider;
	private final HashSet<DummyFailPoint> dummyFailPoints=new HashSet<>();

	@Override
	public VpcDefinition createVPC(AmazonEC2 destAmazonEC2,
			AWSCredentials destCredentials, String region) {
		logger.info("createVpc called");
		failAt(DummyFailPoint.CREATE_VPC);
		
		VpcDefinition vpcDef=new VpcDefinition();
		vpcDef.setKeyPairName(destCredentials.getAWSAccessKeyId());
		vpcDef.setZone("dummyzone");		
		vpcDef.setPrivateSubnetId("private-ip-block");
		vpcDef.setPublicSubnetId("public-ip-block");
		vpcDef.setVpcId("dvpc:"+guidProvider.generateGuid());
		vpcDef.setRegion(region);
		vpcDef.setSecurityGrougId("security-group");
		nap();
		return vpcDef;
	}

	@Override
	public void waitForInitialized(AmazonEC2 amazonEC2,
			Collection<String> instanceIds) {
		logger.info("Wait for initialized");
		failAt(DummyFailPoint.WAIT_FOR_INITIALIZED);
		nap();
		logger.info("Returning from wait");
    }

	@Override
	public void tearDownVPC(AmazonEC2 amazonEC2, String vpcId) {
		logger.info("Tear down called");
		failAt(DummyFailPoint.TEARDOWN);
		nap();
	}

	@Override
	public void associateElasticIp(AmazonEC2 amazonEC2, String instanceId, String allocationId) {
		logger.info("associateElasticIp called");
		failAt(DummyFailPoint.ELASTIC_IP_ASSIGN);		
		nap();
	}

	@Override
	public void waitForInstances(AmazonEC2 amazonEC2, Set<String> instanceIds) {
		logger.info("waitForInstances called");
		failAt(DummyFailPoint.WAIT_FOR_INSTANCES);
		nap();
		}

	@Override
	public void grantAmi(AWSCredentials credentials,
			Collection<AmiDescriptor> amisDescs) {
		
		logger.info("grantAmi called");
		failAt(DummyFailPoint.GRANT_AMI_PERMISSIONS);
		nap();
	}

	@Override
	public Map<String, AmiDescriptor> createInstances(AmazonEC2 destAmazonEC2,
			VpcDefinition vpcDef, Collection<AmiDescriptor> amisDescs,
			String userData) {

		logger.info("createInstances called");
		failAt(DummyFailPoint.CREATE_INSTANCES);
		HashMap<String,AmiDescriptor> instances=new HashMap<>();
		nap();
		return instances;
		
	}

	@Override
	public void tagInstances(AmazonEC2 destEC2,
			Map<String, AmiDescriptor> instanceAmiMap, String deploymentName) {
		logger.info("tagInstances called");
		failAt(DummyFailPoint.TAG_INSTANCES);
		nap();
	}

	@Override
	public ElasticIp getElasticIp(AmazonEC2 amazonEC2) {
		logger.info("getElasticIp called");
		failAt(DummyFailPoint.ELASTIC_IP_GET);
		nap();
		return new ElasticIp("fake-allocate","10.0.0.10");
		
	}

	@Override
	public void stopVpcInstances(AmazonEC2 amazonEC2, String vpcId) {
		logger.info("stopVpcInstances called");
		failAt(DummyFailPoint.STOP_INSTANCES);
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<String> startVpcInstances(AmazonEC2 amazonEC2, String vpcId) {
		logger.info("startVpcInstances called.");
		failAt(DummyFailPoint.START_INSTANCES);
        return new ArrayList<>();
	}

	@Override
	public void cleanupBlockStorage(AmazonEC2 amazonEC2) {
		logger.info("cleanupBlockStorage called.");
		failAt(DummyFailPoint.CLEANUP_BLOCK_STORAGE);
		nap();
		
	}

	@Override
	public AmazonEC2 getAmazonEC2(BasicAWSCredentials awsCredentials, Long regionId) {
		logger.info("getAmazonEC2 called");
		failAt(DummyFailPoint.CONNECT_TO_AMAZON);
		return null;
	}

	@Override
	public void setEBSforDelete(AmazonEC2 destEC2, Set<String> instanceIds) {

		logger.info("setEBSforDelete called");
		failAt(DummyFailPoint.SET_EBS_FOR_DELETE);
	}

	@Override
	public String getUserData(String cidrBlock, String elasticIp,
			String region, String vpcId) {

		failAt(DummyFailPoint.GET_USER_DATA);
		return "userdata";
	}
	
	private void nap() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException ignored) {

		}
	}
	

	private void failAt(DummyFailPoint stageDummyFailPoint) {
		for (DummyFailPoint dfp : dummyFailPoints) {
			if (stageDummyFailPoint.equals(dfp)) {
				throw new DummyFailPointException(stageDummyFailPoint);
			}
		}
	}
	
	public void addDummyFailPoint(DummyFailPoint failPoint) {
		if (failPoint != null) {
			dummyFailPoints.add(failPoint);
		}
	}
	
	public void clearFailPoints() {
		dummyFailPoints.clear();
	}

	@Override
	protected boolean waitForRdpService(ElasticIp eip) {

		try {
			failAt(DummyFailPoint.WAIT_FOR_RDP);
		} catch (DummyFailPointException dfpe) {
			return false;
		}
		
		return true;
		
	}
	
	
}
