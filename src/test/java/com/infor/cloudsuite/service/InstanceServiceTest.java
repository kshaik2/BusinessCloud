package com.infor.cloudsuite.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceState;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.StateReason;
import com.infor.cloudsuite.AbstractTest;
import com.infor.cloudsuite.Mocked;
import com.infor.cloudsuite.dao.AmazonCredentialsDao;
import com.infor.cloudsuite.dao.DeploymentStackLogDao;
import com.infor.cloudsuite.dao.ScheduleDao;
import com.infor.cloudsuite.dao.UserDao;
import com.infor.cloudsuite.dto.AmazonCredentialsDto;
import com.infor.cloudsuite.dto.DeployActionScheduleType;
import com.infor.cloudsuite.dto.DeploymentStackLogDto;
import com.infor.cloudsuite.dto.InstanceActionType;
import com.infor.cloudsuite.dto.InstanceDto;
import com.infor.cloudsuite.dto.InstanceStateChangeDto;
import com.infor.cloudsuite.entity.AmazonCredentials;
import com.infor.cloudsuite.entity.DeploymentStackLogAction;
import com.infor.cloudsuite.entity.DeploymentStatus;
import com.infor.cloudsuite.entity.Schedule;
import com.infor.cloudsuite.entity.ScheduleStatus;
import com.infor.cloudsuite.entity.ScheduleType;
import com.infor.cloudsuite.entity.User;
import com.infor.cloudsuite.platform.amazon.AwsOperations;
import com.infor.cloudsuite.platform.amazon.StackAndInstanceManager;
import com.infor.cloudsuite.service.component.InstanceServiceComponent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@Mocked
@SuppressWarnings("unchecked")
@Transactional
public class InstanceServiceTest extends AbstractTest {
    private static Logger logger = LoggerFactory.getLogger(StackAndInstanceManager.class);

    @Resource
    private AmazonCredentialsDao amazonCredentialsDao;
    @Mock 
    private AwsOperations awsOperations;
    @Resource
    private DeploymentStackLogDao deploymentStackLogDao;
    @Resource
    private InstanceService instanceService;
    @Resource
    private InstanceServiceComponent instanceServiceComponent;
    @Resource
    private ScheduleDao scheduleDao;
    @Mock
    private StackAndInstanceManager stackAndInstanceManager;
    @Resource
    private StackAndInstanceManager stackAndInstanceManager2;
    @Resource
    private UserDao userDao;
    private HttpServletRequest request;

    private DescribeInstancesResult createInstanceTestList() {

        StateReason reason= new StateReason();
        reason.setCode("code");
        reason.setMessage("message");

        InstanceState is = new InstanceState();
        is.setCode(1);
        is.setName("running");

        Instance inst1 = new Instance();
        inst1.setAmiLaunchIndex(1);
        inst1.setArchitecture("arch");
        inst1.setInstanceId("1");
        inst1.setImageId("imgId");
        inst1.setPrivateDnsName("privateDnsName");
        inst1.setPrivateIpAddress("privateIpAddress");
        inst1.setPublicDnsName("publicDnsName");
        inst1.setPublicIpAddress("PublicIpAddress");
        inst1.setInstanceType("InstanceType");
        inst1.setInstanceLifecycle("InstanceLifecycle");
        inst1.setVpcId("VpcId");
        inst1.setSubnetId("SubnetId");
        inst1.setState(is);
        inst1.setStateReason(reason);
        inst1.setStateTransitionReason("stateTransitionReason");
        inst1.setLaunchTime(new Date());

        Instance inst2 = new Instance();
        inst2.setAmiLaunchIndex(1);
        inst2.setArchitecture("arch");
        inst2.setInstanceId("2");
        inst2.setImageId("imgId");
        inst2.setPrivateDnsName("privateDnsName");
        inst2.setPrivateIpAddress("privateIpAddress");
        inst2.setPublicDnsName("publicDnsName");
        inst2.setPublicIpAddress("PublicIpAddress");
        inst2.setInstanceType("InstanceType");
        inst2.setInstanceLifecycle("InstanceLifecycle");
        inst2.setVpcId("VpcId");
        inst2.setSubnetId("SubnetId");
        inst2.setState(is);
        inst2.setStateReason(reason);
        inst2.setStateTransitionReason("stateTransitionReason");
        inst2.setLaunchTime(new Date());

        List<Instance> instList = new ArrayList<>();
        instList.add(inst1);
        instList.add(inst2);

        Reservation res = new Reservation();
        res.setInstances(instList);
        res.setReservationId("test");

        List<Reservation> list = new ArrayList<>();
        list.add(res);

        DescribeInstancesResult descInstRes = new DescribeInstancesResult();
        descInstRes.setReservations(list);

        return descInstRes;
    }

    private AmazonCredentialsDto getLoginCredentials() {
        login("sales@infor.com","useruser");
        User sales=userDao.findByUsername("sales@infor.com");
        AmazonCredentials amCred=new AmazonCredentials();
        amCred.setAwsKey("DUMMYKEY");
        amCred.setName("DUMMYKEY");
        amCred.setSecretKey("Psst...");
        amCred.setUser(sales);
        amazonCredentialsDao.save(amCred);
        amazonCredentialsDao.flush();

        AmazonCredentialsDto amznCred = new AmazonCredentialsDto(amCred);

        assertNotNull(amCred.getId());
        return amznCred;
    }

    private InstanceStateChangeDto getStateChange( AmazonCredentialsDto amznCred, InstanceActionType state,
            DeployActionScheduleType scheduleType) {
        InstanceStateChangeDto stateChange = new InstanceStateChangeDto();

        List<String> instanceIds = new ArrayList<>();
        instanceIds.add("1");
        instanceIds.add("2");

        stateChange.setCredentials(amznCred);
        stateChange.setInstanceIds(instanceIds);
        stateChange.setStateChange(state);
        stateChange.setScheduleType(scheduleType);
        stateChange.setScheduleValue("12");
        stateChange.setRegionId(6L);
        return stateChange;
    }

    @Before
    public void setup() {
        HttpSession session = new MockHttpSession();
        request = getRequestStub(session);
    }

    @Test
    public void testgetInstancesFailure() {
        when( stackAndInstanceManager.getInstances(anyLong(), anyList(), anyLong() )).thenThrow(new AmazonServiceException("connection timeout"));

        InstanceService systemUnderTest = new InstanceService();
        // inject manually
        ReflectionTestUtils.setField(instanceServiceComponent, "stackAndInstanceManager", stackAndInstanceManager);
        ReflectionTestUtils.setField( systemUnderTest, "instanceServiceComponent", instanceServiceComponent);

        // Make the test call
        try {
            //This test should fail.
            systemUnderTest.getInstances(1L, 3L);
            fail("exception not thrown");
        }
        catch(Exception e) {
            logger.debug("Intended failure in InstanceServiceTest :",e.getMessage());
            assertNotNull("Exception should not be null ", e);
        }
    }

    @Test
    public void testScheduleCreate() {
        AmazonCredentialsDto amznCred = getLoginCredentials();
        InstanceActionType start = InstanceActionType.START;
        DeployActionScheduleType hourly = DeployActionScheduleType.HOURLY;
        InstanceStateChangeDto stateChange = getStateChange(amznCred, start, hourly);

        doNothing().when(awsOperations).startInstances( any( AmazonEC2.class ), any ( List.class) );

        DescribeInstancesResult descInstRes = createInstanceTestList();
        when( awsOperations.describeInstances( any( AmazonEC2.class ), anyList())).thenReturn(descInstRes);

        ReflectionTestUtils.setField( stackAndInstanceManager2, "amazonCredentialsDao", amazonCredentialsDao);
        ReflectionTestUtils.setField( stackAndInstanceManager2, "awsOperations", awsOperations);
        ReflectionTestUtils.setField(instanceServiceComponent, "stackAndInstanceManager", stackAndInstanceManager2);
        ReflectionTestUtils.setField( instanceService, "instanceServiceComponent", instanceServiceComponent);

        instanceService.updateInstancesStatus(request,stateChange);
        
        for(String id: stateChange.getInstanceIds()) {
            List<Schedule> schedules = scheduleDao.findByEntityIdAndTypeAndStatus(id, ScheduleType.STOP_INSTANCE, ScheduleStatus.QUEUED);
            assertEquals(1,schedules.size());
            assertTrue(schedules.get(0).getTargetObject().contains("\"regionId\":6"));
        }
    }

    @Test
    public void testScheduleStop() {
        AmazonCredentialsDto amznCred = getLoginCredentials();
        InstanceActionType start = InstanceActionType.START;
        DeployActionScheduleType hourly = DeployActionScheduleType.HOURLY;
        InstanceStateChangeDto stateChange = getStateChange(amznCred, start, hourly);

        doNothing().when( awsOperations ).startInstances( any( AmazonEC2.class ), any ( List.class) );

        DescribeInstancesResult descInstRes = createInstanceTestList();
        when( awsOperations.describeInstances(any( AmazonEC2.class ), anyList() )).thenReturn(descInstRes);

        ReflectionTestUtils.setField( stackAndInstanceManager2, "amazonCredentialsDao", amazonCredentialsDao);
        ReflectionTestUtils.setField( stackAndInstanceManager2, "awsOperations", awsOperations);
        ReflectionTestUtils.setField(instanceServiceComponent, "stackAndInstanceManager", stackAndInstanceManager2);
        ReflectionTestUtils.setField( instanceService, "instanceServiceComponent", instanceServiceComponent);

        instanceService.updateInstancesStatus(request,stateChange);

        stateChange.setStateChange(InstanceActionType.STOP);
        instanceService.updateInstancesStatus(request,stateChange);

        for(String id: stateChange.getInstanceIds()) {
            List<Schedule> schedules = scheduleDao.findByEntityIdAndTypeAndStatus(id, ScheduleType.STOP_INSTANCE, ScheduleStatus.CANCELED);
            assertEquals(1,schedules.size());
        }

        //Test Terminate to see if schedule is canceled
        stateChange.setStateChange(InstanceActionType.START);
        instanceService.updateInstancesStatus(request,stateChange);
        stateChange.setStateChange(InstanceActionType.TERMINATE);
        instanceService.updateInstancesStatus(request,stateChange);

        for(String id: stateChange.getInstanceIds()) {
            List<Schedule> schedules = scheduleDao.findByEntityIdAndTypeAndStatus(id, ScheduleType.STOP_INSTANCE, ScheduleStatus.CANCELED);
            assertEquals(2,schedules.size());
        }
    }

    @Test
    public void testStartUpdateInstanceStatus() {
        AmazonCredentialsDto amznCred = getLoginCredentials();
        InstanceActionType start = InstanceActionType.START;
        DeployActionScheduleType hourly = DeployActionScheduleType.HOURLY;

        InstanceStateChangeDto stateChange = getStateChange(amznCred, start, hourly);

        doNothing().when(awsOperations).startInstances( any( AmazonEC2.class ), any ( List.class) );

        DescribeInstancesResult descInstRes = createInstanceTestList();
        when( awsOperations.describeInstances(any( AmazonEC2.class ), anyList() )).thenReturn(descInstRes);

        ReflectionTestUtils.setField( stackAndInstanceManager2, "amazonCredentialsDao", amazonCredentialsDao);
        ReflectionTestUtils.setField( stackAndInstanceManager2, "awsOperations", awsOperations);
        ReflectionTestUtils.setField(instanceServiceComponent, "stackAndInstanceManager", stackAndInstanceManager2);
        ReflectionTestUtils.setField( instanceService, "instanceServiceComponent", instanceServiceComponent);

        List<InstanceDto> instances = instanceService.updateInstancesStatus(request,stateChange);
        assertTrue(instances.get(0).getInstanceState().equals("Available"));
        assertTrue(instances.get(0).getState().getStateName().equals("Running"));

        List<DeploymentStackLogDto> logResults;

        for(String id: stateChange.getInstanceIds()) {
            logResults = deploymentStackLogDao.getLogsByVpcId(id);
            assertTrue(logResults.size() > 0);
            DeploymentStackLogDto verifyLog = logResults.get(0);
            assertTrue(logResults.size() > 0);
            assertEquals(verifyLog.getLogAction(), DeploymentStackLogAction.CHANGE_IN_INSTANCE_STATUS);
            assertEquals(verifyLog.getStatus(), DeploymentStatus.STARTED);
            List<Schedule> schedules = scheduleDao.findByEntityIdAndTypeAndStatus(id, ScheduleType.STOP_INSTANCE, ScheduleStatus.QUEUED);
            assertEquals(1,schedules.size());
        }
    }

    @Test
    public void testStopUpdateInstanceStatus() {
        AmazonCredentialsDto amznCred = getLoginCredentials();
        InstanceActionType start = InstanceActionType.STOP;
        DeployActionScheduleType hourly = DeployActionScheduleType.HOURLY;
        InstanceStateChangeDto stateChange = getStateChange(amznCred, start, hourly);

        doNothing().when(awsOperations).startInstances( any( AmazonEC2.class ), any ( List.class) );

        DescribeInstancesResult descInstRes = createInstanceTestList();
        when( awsOperations.describeInstances(any( AmazonEC2.class ), anyList())).thenReturn(descInstRes);

        ReflectionTestUtils.setField( stackAndInstanceManager2, "amazonCredentialsDao", amazonCredentialsDao);
        ReflectionTestUtils.setField( stackAndInstanceManager2, "awsOperations", awsOperations);
        ReflectionTestUtils.setField(instanceServiceComponent, "stackAndInstanceManager", stackAndInstanceManager2);
        ReflectionTestUtils.setField( instanceService, "instanceServiceComponent", instanceServiceComponent);

        instanceService.updateInstancesStatus(request,stateChange);

        List<DeploymentStackLogDto> logResults;

        for(String id: stateChange.getInstanceIds()) {
            logResults = deploymentStackLogDao.getLogsByVpcId(id);
            assertTrue(logResults.size() > 0);
            DeploymentStackLogDto verifyLog = logResults.get(0);
            assertEquals(verifyLog.getLogAction(), DeploymentStackLogAction.CHANGE_IN_INSTANCE_STATUS);
            assertEquals(verifyLog.getStatus(), DeploymentStatus.STOPPED);
        }
    }

    @Test
    public void testTerminateUpdateInstanceStatus() {
        AmazonCredentialsDto amznCred = getLoginCredentials();
        InstanceActionType start = InstanceActionType.TERMINATE;
        DeployActionScheduleType hourly = DeployActionScheduleType.HOURLY;
        InstanceStateChangeDto stateChange = getStateChange(amznCred, start, hourly);

        doNothing().when(awsOperations).startInstances( any( AmazonEC2.class ), any ( List.class) );

        DescribeInstancesResult descInstRes = createInstanceTestList();
        when( awsOperations.describeInstances(any( AmazonEC2.class ), anyList() )).thenReturn(descInstRes);

        ReflectionTestUtils.setField( stackAndInstanceManager2, "amazonCredentialsDao", amazonCredentialsDao);
        ReflectionTestUtils.setField( stackAndInstanceManager2, "awsOperations", awsOperations);
        ReflectionTestUtils.setField(instanceServiceComponent, "stackAndInstanceManager", stackAndInstanceManager2);
        ReflectionTestUtils.setField( instanceService, "instanceServiceComponent", instanceServiceComponent);

        instanceService.updateInstancesStatus(request,stateChange);

        List<DeploymentStackLogDto> logResults;

        for(String id: stateChange.getInstanceIds()) {
            logResults = deploymentStackLogDao.getLogsByVpcId(id);
            DeploymentStackLogDto verifyLog = logResults.get(0);
            assertTrue(logResults.size() > 0);
            assertEquals(verifyLog.getLogAction(), DeploymentStackLogAction.CHANGE_IN_INSTANCE_STATUS);
            assertEquals(verifyLog.getStatus(), DeploymentStatus.TERMINATED);
        }
    }

    @Test
    public void testTimeToTerminate() {
        AmazonCredentialsDto amznCred = getLoginCredentials();
        InstanceActionType start = InstanceActionType.START;
        DeployActionScheduleType hourly = DeployActionScheduleType.HOURLY;
        InstanceStateChangeDto stateChange = getStateChange(amznCred, start, hourly);

        ReflectionTestUtils.setField( stackAndInstanceManager2, "amazonCredentialsDao", amazonCredentialsDao);
        ReflectionTestUtils.setField( stackAndInstanceManager2, "awsOperations", awsOperations);
        ReflectionTestUtils.setField(instanceServiceComponent, "stackAndInstanceManager", stackAndInstanceManager2);
        ReflectionTestUtils.setField( instanceService, "instanceServiceComponent", instanceServiceComponent);


        DescribeInstancesResult descInstRes = createInstanceTestList();
        when( awsOperations.describeInstances(any( AmazonEC2.class ), anyList() )).thenReturn(descInstRes);

        List<InstanceDto> instanceDtos = instanceService.updateInstancesStatus(request,stateChange);

        for(String id: stateChange.getInstanceIds()) {
            List<Schedule> schedules = scheduleDao.findByEntityIdAndTypeAndStatus(id, ScheduleType.STOP_INSTANCE, ScheduleStatus.QUEUED);
            assertEquals(1,schedules.size());
        }

        //        // Make the test call
        //        instanceService.getInstances(amznCred.getId());

        assertNotNull( instanceDtos.get(0).getScheduledStopAt() );
    }

    @Test
    public void verifyNotificationEmail() {
        AmazonCredentialsDto amznCred = getLoginCredentials();
        InstanceActionType start = InstanceActionType.START;
        DeployActionScheduleType hourly = DeployActionScheduleType.HOURLY;
        InstanceStateChangeDto stateChange = getStateChange(amznCred, start, hourly);

        ReflectionTestUtils.setField( stackAndInstanceManager2, "amazonCredentialsDao", amazonCredentialsDao);
        ReflectionTestUtils.setField( stackAndInstanceManager2, "awsOperations", awsOperations);
        ReflectionTestUtils.setField(instanceServiceComponent, "stackAndInstanceManager", stackAndInstanceManager2);
        ReflectionTestUtils.setField( instanceService, "instanceServiceComponent", instanceServiceComponent);


        DescribeInstancesResult descInstRes = createInstanceTestList();
        when( awsOperations.describeInstances(any( AmazonEC2.class ), anyList() )).thenReturn(descInstRes);

        instanceService.updateInstancesStatus(request,stateChange);

        for(String id: stateChange.getInstanceIds()) {
            List<Schedule> schedules = scheduleDao.findByEntityIdAndTypeAndStatus(id, ScheduleType.EMAIL_NOTIFICATION_SCHEDULE_STOP, ScheduleStatus.QUEUED);
            assertEquals(1,schedules.size());
        }
    }

}