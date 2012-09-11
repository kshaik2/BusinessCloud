package com.infor.cloudsuite.platform.amazon;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.cxf.common.util.Base64Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.*;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.model.User;
import com.infor.cloudsuite.dao.AmazonCredentialsDao;
import com.infor.cloudsuite.dao.RegionDao;
import com.infor.cloudsuite.entity.AmiDescriptor;
import com.infor.cloudsuite.entity.Region;
import com.infor.cloudsuite.platform.components.TemplateProvider;

/**
 * Created with IntelliJ IDEA.
 * User: briancrow
 * Date: 5/30/12
 * Time: 2:24 PM
 */
@Component
public class AwsOperations {
    private static final Logger logger = LoggerFactory.getLogger(AwsOperations.class);

    @Resource
    private AmazonFactory amazonFactory;

    @Resource
    private AmazonCredentialsDao amazonCredentialsDao;
    @Resource
    private TemplateProvider templateProvider;
    @Resource 
    private RegionDao regionDao;

    public VpcDefinition createVPC(final AmazonEC2 destAmazonEC2, AWSCredentials destCredentials, String region) {
        String cidrBlock = "192.168.0.0/16";
        String publicCidr = "192.168.85.0/24";  //public will be 86

        VpcDefinition vpcDef = new VpcDefinition();

        syncSetAvailZone(destAmazonEC2, region, vpcDef);

        logger.info("Configuring DHCP Option set..");
        String dhcpOptionsId = configureDHCPOptions(destAmazonEC2);

        //Create the actual VPC
        logger.info("Construct the vpc..");
        String vpcId = createVpc(destAmazonEC2, cidrBlock);
        vpcDef.setVpcId(vpcId);
        logger.info("VPC done: " + vpcDef.getVpcId());


        syncAssociateDhcpOptions(destAmazonEC2, dhcpOptionsId, vpcId);

        logger.info("Creating subnet...");
        final String subnetId = createSubnet(destAmazonEC2, vpcId, publicCidr, vpcDef.getZone());
        vpcDef.setPublicSubnetId(subnetId);
        //        String publicSubnet = createSubnet(destAmazonEC2, vpcId, publicCidr, zone);
        //       vpcDef.setPrivateSubnetId();

        //create the internet gateway
        logger.info("Create Internet Gateway...");
        String gatewayId = createInternetGateway(destAmazonEC2, vpcId);

        //create a route table for the gateway.
        logger.info("Create Routing tables...");
        String routeTableId = createRouteTable(destAmazonEC2, vpcId, gatewayId, "0.0.0.0/0");

        logger.info("Associating subnet with route table...");
        associateRouteTableToSubnet(destAmazonEC2, subnetId, routeTableId);

        logger.info("Creating KeyPair...");
        final String keyPairName = "BC_GDE3_TEMP";
        createKeyPair(destAmazonEC2, keyPairName);

        final String securityGroupId = createSGTcpCidr(destAmazonEC2, vpcId, "GDE-TEST-SG1", "GDE Test Security Group",
                3389, 3389, "0.0.0.0/0");
        vpcDef.setSecurityGrougId(securityGroupId);

        String acctId = getAcctNumber(destCredentials);
        final UserIdGroupPair userIdGroupPair = new UserIdGroupPair().withUserId(acctId).withGroupId(securityGroupId);
        //wide open internally.
        destAmazonEC2.authorizeSecurityGroupIngress(new AuthorizeSecurityGroupIngressRequest().
                withGroupId(securityGroupId).
                withIpPermissions(
                        new IpPermission().
                        withIpProtocol("tcp").withFromPort(0).withToPort(65535).
                        withUserIdGroupPairs(userIdGroupPair),
                        new IpPermission().
                        withIpProtocol("udp").withFromPort(0).withToPort(65535).
                        withUserIdGroupPairs(userIdGroupPair),
                        new IpPermission().
                        withIpProtocol("icmp").withFromPort(-1).withToPort(-1).
                        withUserIdGroupPairs(userIdGroupPair)
                        ));


        logger.info("VPC Created successfully!!");

        return vpcDef;
    }

    private synchronized void syncSetAvailZone(AmazonEC2 destAmazonEC2, String region, VpcDefinition vpcDef) {
        logger.info("Finding a suitable zone...");
        final List<AvailabilityZone> zones = destAmazonEC2.describeAvailabilityZones().getAvailabilityZones();
        for (AvailabilityZone zon : zones) {
            logger.debug("  zone: " + zon.getZoneName());
            logger.debug("  region: " + zon.getRegionName());
            if (region.equals(zon.getRegionName())) {
                vpcDef.setZone(zon.getZoneName());
                break;
            }
        }
        logger.info("Zone: " + vpcDef.getZone());
    }

    private synchronized void syncAssociateDhcpOptions(AmazonEC2 destAmazonEC2, String dhcpOptionsId, String vpcId) {
        if (dhcpOptionsId != null) {
            logger.info("Associating DHCP Option set.");
            destAmazonEC2.associateDhcpOptions(new AssociateDhcpOptionsRequest().
                    withVpcId(vpcId).withDhcpOptionsId(dhcpOptionsId));
        }
    }

    private synchronized String configureDHCPOptions(AmazonEC2 amazonEC2) {
        String dhcpOptionsId = null;
        final List<DhcpOptions> dhcpOptions = amazonEC2.describeDhcpOptions().getDhcpOptions();
        for (DhcpOptions dhcpOption : dhcpOptions) {
            for (DhcpConfiguration dhcpConfig : dhcpOption.getDhcpConfigurations()) {
                if ("domain-name".equals(dhcpConfig.getKey()) &&
                        "gdeinfor2.com".equals(dhcpConfig.getValues().get(0))) {
                    dhcpOptionsId = dhcpOption.getDhcpOptionsId();
                    logger.info("  Found suitable DHCP Option set: " + dhcpOptionsId);
                    break;
                }
            }
        }

        if (dhcpOptionsId == null) {
            dhcpOptionsId = amazonEC2.createDhcpOptions(new CreateDhcpOptionsRequest().
                    withDhcpConfigurations(
                            new DhcpConfiguration().withKey("domain-name").withValues("gdeinfor2.com"),
                            new DhcpConfiguration().withKey("domain-name-servers").
                            withValues("192.168.85.95"
                                    , "192.168.0.2"
                                    ),
                                    new DhcpConfiguration().withKey("netbios-name-servers").withValues("192.168.85.95"))).
                                    getDhcpOptions().getDhcpOptionsId();
            logger.info("  Created DHCP Option set: " + dhcpOptionsId);
        }
        return dhcpOptionsId;
    }

    public void waitForInitialized(AmazonEC2 amazonEC2, Collection<String> instanceIds) {
        long startTime = System.currentTimeMillis();
        long waitTime = 30 * 60 * 1000; //30 minutes.
        long endTime = startTime + waitTime;

        List<String> localIds = new ArrayList<>(instanceIds);

        while (!localIds.isEmpty()) {


            final List<InstanceStatus> statuses = getInstanceStatuses(amazonEC2, localIds);

            for (InstanceStatus status : statuses) {
                logger.debug("Instance: " + status.getInstanceId());
                logger.debug("    status: " + status.getInstanceStatus().getStatus());
                String waitStatus = status.getInstanceStatus().getStatus();
                logger.debug("    state: " + status.getInstanceState().getName());
                for (InstanceStatusEvent event : status.getEvents()) {
                    logger.debug("    event: " + event.getCode() + ": " + event.getDescription());
                }
                if (!"initializing".equals(waitStatus)) {
                    logger.info("Initialized: " + status.getInstanceId());
                    localIds.remove(status.getInstanceId());
                }
            }

            if (System.currentTimeMillis() > endTime) break; //maybe throw exception...
            if (!localIds.isEmpty()) {
                sleep(30);
            }
        }

        logger.info("Initialized in " + ((System.currentTimeMillis() - startTime) / 1000 / 60) + " min.");
    }

    public void tearDownVPC(AmazonEC2 amazonEC2, String vpcId) {

        if (notVpcExists(amazonEC2, vpcId)) {
            logger.error("VPC Does not exist with id:" + vpcId);
            return;
        }

        logger.info("Get the running instances..");
        List<String> deleteInstances = getVpcInstances(amazonEC2, vpcId);
        logger.info("Failsafe: Setting EBS for delete on terminate...");
        sleep(10); //give time for volumes to attach.
        setEBSforDelete(amazonEC2, new HashSet<>(deleteInstances));
        logger.info("Failsafe: Done setting EBS for delete on terminate...");
        logger.info("Found instances: " + deleteInstances.size());

        logger.info("Release the elastic Ip Addresses...");
        releaseElasticIps(amazonEC2, deleteInstances);

        logger.info("Terminiate instances...");
        terminateInstances(amazonEC2, deleteInstances);
        logger.info("Waiting for instances to terminate.");
        List<Reservation> reservations;

        while (deleteInstances.size() > 0) {
            reservations = getReservations(amazonEC2, deleteInstances);
            for (Reservation reservation : reservations) {
                for (Instance instance : reservation.getInstances()) {
                    if (48 == instance.getState().getCode()) {
                        logger.info("   Instance terminated: " + instance.getInstanceId());
                        deleteInstances.remove(instance.getInstanceId());
                    }
                }
            }

            sleep(20);
        }
        logger.info("Finished terminating");

        logger.info("Deleting security groups....");
        deleteSecurityGroups(amazonEC2, vpcId);

        logger.info("Deleting Internet Gateways...");
        deleteGateways(amazonEC2, vpcId);

        logger.info("Deleting ACLs...");
        deleteACLs(amazonEC2, vpcId);

        logger.info("Deleting Subnets...");
        deleteSubnets(amazonEC2, vpcId);


        logger.info("Deleting the VPC..");
        amazonEC2.deleteVpc(new DeleteVpcRequest().withVpcId(vpcId));


        logger.info("Complete!!");
    }

    private boolean notVpcExists(AmazonEC2 amazonEC2, String vpcId) {
        try {
            DescribeVpcsResult vpcsResult = amazonEC2.describeVpcs(new DescribeVpcsRequest().withVpcIds(vpcId));
            return (vpcsResult.getVpcs() == null || vpcsResult.getVpcs().size() <= 0);
        } catch (AmazonServiceException e) {
            if ("invalidvpcid.notfound".equalsIgnoreCase(e.getErrorCode())) {
                logger.info("VPC Not Found");
                return true;
            } else {
                throw e;
            }

        }

    }

    private synchronized void deleteSubnets(AmazonEC2 amazonEC2, String vpcId) {
        final List<Subnet> subnets = amazonEC2.describeSubnets().getSubnets();
        for (Subnet subnet : subnets) {
            if (vpcId.equals(subnet.getVpcId())) {
                amazonEC2.deleteSubnet(new DeleteSubnetRequest(subnet.getSubnetId()));
            }
        }
    }

    private synchronized void deleteACLs(AmazonEC2 amazonEC2, String vpcId) {
        final List<NetworkAcl> aclList = amazonEC2.describeNetworkAcls().getNetworkAcls();
        for (NetworkAcl acl : aclList) {
            if (vpcId.equals(acl.getVpcId())) {
                if (acl.getIsDefault()) continue; //don't delete default.
                amazonEC2.deleteNetworkAcl(new DeleteNetworkAclRequest().withNetworkAclId(acl.getNetworkAclId()));
            }
        }
    }

    private synchronized void deleteGateways(AmazonEC2 amazonEC2, String vpcId) {
        final List<InternetGateway> internetGateways = amazonEC2.describeInternetGateways().getInternetGateways();
        for (InternetGateway gateway : internetGateways) {
            for (InternetGatewayAttachment attachment : gateway.getAttachments()) {
                if (vpcId.equals(attachment.getVpcId())) {
                    syncDeleteGateway(amazonEC2, vpcId, gateway);
                }
            }
        }
    }

    private synchronized void syncDeleteGateway(AmazonEC2 amazonEC2, String vpcId, InternetGateway gateway) {
        amazonEC2.detachInternetGateway(
                new DetachInternetGatewayRequest().
                withInternetGatewayId(gateway.getInternetGatewayId()).
                withVpcId(vpcId));
        amazonEC2.deleteInternetGateway(new DeleteInternetGatewayRequest().
                withInternetGatewayId(gateway.getInternetGatewayId()));
    }

    private void deleteSecurityGroups(AmazonEC2 amazonEC2, String vpcId) {
        final List<SecurityGroup> securityGroups = amazonEC2.describeSecurityGroups().getSecurityGroups();

        for (SecurityGroup securityGroup : securityGroups) {
            if (vpcId.equals(securityGroup.getVpcId())) {
                if ("default".equals(securityGroup.getGroupName())) continue; //don't delete default.
                syncDeleteSecurityGroup(amazonEC2, securityGroup);
            }
        }
    }

    private synchronized void syncDeleteSecurityGroup(AmazonEC2 amazonEC2, SecurityGroup securityGroup) {
        amazonEC2.deleteSecurityGroup(new DeleteSecurityGroupRequest().withGroupId(securityGroup.getGroupId()));
    }

    public void terminateInstances(AmazonEC2 amazonEC2, List<String> deleteInstances) {
        if (deleteInstances.size() == 0) return;

        logger.info("Terminating instances: ", deleteInstances.size());
        syncTerminateInstances(amazonEC2, deleteInstances);
    }

    private synchronized void syncTerminateInstances(AmazonEC2 amazonEC2, List<String> termInstances) {
        amazonEC2.terminateInstances(new TerminateInstancesRequest(termInstances));
    }

    private synchronized List<Reservation> getReservations(AmazonEC2 amazonEC2, List<String> termInstances) {
        List<Reservation> reservations;
        reservations = amazonEC2.describeInstances(new DescribeInstancesRequest()
        .withInstanceIds(termInstances)).getReservations();
        return reservations;
    }

    private synchronized void releaseElasticIps(AmazonEC2 amazonEC2, List<String> deleteInstances) {
        final List<Address> addresses = amazonEC2.describeAddresses(new DescribeAddressesRequest()).getAddresses();
        for (Address address : addresses) {
            if (deleteInstances.contains(address.getInstanceId())) {
                logger.info("Release: " + address.getPublicIp());
                amazonEC2.disassociateAddress(new DisassociateAddressRequest().withAssociationId(address.getAssociationId()));
                amazonEC2.releaseAddress(new ReleaseAddressRequest().withAllocationId(address.getAllocationId()));
            }
        }
    }

    private synchronized List<String> getVpcInstances(AmazonEC2 amazonEC2, String vpcId) {
        logger.info("   Describing...");
        List<Reservation> reservations = amazonEC2.describeInstances().getReservations();
        logger.info("   Done describing.");
        List<String> instances = new ArrayList<>();
        for (Reservation reservation : reservations) {
            for (Instance instance : reservation.getInstances()) {
                if (vpcId.equals(instance.getVpcId())) {
                    instances.add(instance.getInstanceId());
                }
            }
        }
        return instances;
    }

    private synchronized void createNameTag(AmazonEC2 amazonEC2, String instanceId, String name) {
        amazonEC2.createTags(new CreateTagsRequest()
        .withTags(new Tag("Name", name))
        .withResources(instanceId));
    }

    public Map<String, String> getNameTags(final AmazonEC2 amazonEC2) {
        final List<TagDescription> tags = amazonEC2.describeTags(new DescribeTagsRequest().withFilters(
                new Filter().withName("resource-type").withValues("instance"),
                //                new Filter().withName("resource-id").withValues(instanceId), //quicker to get all Names.
                new Filter().withName("key").withValues("Name")
                )).getTags();

        Map<String, String> resourceNames = new HashMap<>();
        for (TagDescription tag : tags) {
            resourceNames.put(tag.getResourceId(), tag.getValue());
        }
        return resourceNames;
    }

    public String getNameTag(AmazonEC2 amazonEC2, String instanceId) {
        String name = null;
        final List<TagDescription> tags = amazonEC2.describeTags(new DescribeTagsRequest().withFilters(
                new Filter().withName("resource-type").withValues("instance"),
                new Filter().withName("resource-id").withValues(instanceId),
                new Filter().withName("key").withValues("Name") ) ).getTags();

        if(tags != null && tags.size() > 0) {
            name = tags.get(0).getValue();
        }

        return name;
    }

    public synchronized void associateElasticIp(AmazonEC2 amazonEC2, String instanceId, String allocationId) {
        String associationId = amazonEC2.associateAddress(
                new AssociateAddressRequest()
                .withAllocationId(allocationId)
                .withInstanceId(instanceId))
                .getAssociationId();
        logger.info("Allocated Elastic IP id: " + associationId);
        logger.info("Allocated Elastic IP: " + allocationId);
    }

    public void waitForInstances(AmazonEC2 amazonEC2, Set<String> instanceIds) {
        //        String status = "initializing";
        //        String statInt = "0";
        //        pending | running | shutting-down | terminated | stopping | stopped
        //        ok | impaired | initializing | insufficient-data | not-applicable

        List<String> localInstIds = new ArrayList<>(instanceIds);

        boolean sleep = false;
        boolean rethrow = false;
        while (!localInstIds.isEmpty()) {

            if (sleep)
                sleep(30);
            else
                sleep = true;

            List<InstanceStatus> statuses;
            try {
                statuses = getInstanceStatuses(amazonEC2, localInstIds);
            } catch (AmazonClientException e) {
                if (rethrow) { //retry once.
                    throw e;
                } else {
                    rethrow = true;
                    continue;
                }
            }

            if (statuses.size() > 0) {
                for (InstanceStatus status : statuses) {
                    localInstIds.remove(status.getInstanceId());
                    logger.info("  insanceId: " + status.getInstanceId());
                    logger.info("    Status: " + status.getInstanceStatus().getStatus());
                    logger.info("    State: " + status.getInstanceState().getName());
                }
            }
        }
    }

    public DescribeInstancesResult describeInstances(AmazonEC2 amazonEC2) {
        return amazonEC2.describeInstances();
    }

    public DescribeInstancesResult describeInstances(AmazonEC2 amazonEC2, List<String> ids) {
        DescribeInstancesRequest instanceReq = new DescribeInstancesRequest();
        instanceReq.setInstanceIds(ids);
        return amazonEC2.describeInstances(instanceReq);
    }

    private synchronized List<InstanceStatus> getInstanceStatuses(AmazonEC2 amazonEC2, List<String> localInstIds) {
        List<InstanceStatus> statuses;
        statuses = amazonEC2.describeInstanceStatus(new DescribeInstanceStatusRequest()
        .withInstanceIds(localInstIds)).getInstanceStatuses();
        return statuses;
    }

    @Async
    private String runInstance(AmazonEC2 amazonEC2,
            String subnetId,
            String keyPairName,
            String securityGroupId,
            String imageName,
            String privateIpAddress,
            String userData,
            InstanceType instanceType) {
        logger.info("Launching EC2 instance....");

        Reservation reservation = amazonEC2.runInstances(
                new RunInstancesRequest()
                .withImageId(imageName)
                .withMinCount(1).withMaxCount(1)
                .withInstanceType(instanceType)
                .withSubnetId(subnetId)
                .withPrivateIpAddress(privateIpAddress)
                .withKeyName(keyPairName)
                .withInstanceInitiatedShutdownBehavior("stop")
                .withSecurityGroupIds(securityGroupId)
                .withUserData(userData)
                //.withClientToken("GDE3-TEST-1")
                ).getReservation();

        logger.info("  Done with runInstance call.");
        logger.info("  Reservation id: " + reservation.getReservationId());

        for (GroupIdentifier groupIdentifier : reservation.getGroups()) {
            logger.info("  Group: " + groupIdentifier.getGroupName());
        }

        String instanceId = null;
        for (Instance instance : reservation.getInstances()) {
            instanceId = instance.getInstanceId();
            logger.info("  Instance: " + instance.getInstanceId());
        }

        logger.info("  Owner: " + reservation.getOwnerId());
        logger.info("  Requester: " + reservation.getRequesterId());
        return instanceId;
    }

    private void setDevicesToDeleteOnTerminate(AmazonEC2 amazonEC2, Instance instance) {
        for (InstanceBlockDeviceMapping mapping : instance.getBlockDeviceMappings()) {
            if (!mapping.getEbs().isDeleteOnTermination()) {
                String volumeid = mapping.getEbs().getVolumeId();
                EbsInstanceBlockDeviceSpecification ebsDev = new EbsInstanceBlockDeviceSpecification()
                .withDeleteOnTermination(true)
                .withVolumeId(volumeid);
                InstanceBlockDeviceMappingSpecification instDev = new InstanceBlockDeviceMappingSpecification()
                .withDeviceName(mapping.getDeviceName())
                .withEbs(ebsDev);
                ModifyInstanceAttributeRequest modReq = new ModifyInstanceAttributeRequest()
                .withAttribute(InstanceAttributeName.BlockDeviceMapping)
                .withBlockDeviceMappings(instDev)
                .withInstanceId(instance.getInstanceId());
                logger.info("  Modify device: " + instDev.getDeviceName());
                amazonEC2.modifyInstanceAttribute(modReq);
            }
        }
    }

    public void cleanupBlockStorage(AmazonEC2 amazonEC2) {
        final List<Volume> volumes = amazonEC2.describeVolumes().getVolumes();
        for (Volume volume : volumes) {
            if (volume.getAttachments().isEmpty() &&
                    "available".equalsIgnoreCase(volume.getState())) {
                amazonEC2.deleteVolume(new DeleteVolumeRequest(volume.getVolumeId()));
            }
        }
    }

    private void createKeyPair(AmazonEC2 amazonEC2, String keyPairName) {
        DescribeKeyPairsResult pairsResult = amazonEC2.describeKeyPairs();
        boolean keyExists = false;
        for (KeyPairInfo keyPairInfo : pairsResult.getKeyPairs()) {
            logger.info("  pair Name: " + keyPairInfo.getKeyName());
            if (keyPairName.equals(keyPairInfo.getKeyName())) keyExists = true;
        }
        if (!keyExists) {
            logger.info("  creating key pair");
            CreateKeyPairResult keyPair = amazonEC2.createKeyPair(new CreateKeyPairRequest(keyPairName));
            //todo: need to store this if we create it.
            logger.info("  KeyPair material: " + keyPair.getKeyPair().getKeyMaterial());

        }
    }

    private void associateRouteTableToSubnet(AmazonEC2 amazonEC2, String subnetId, String routeTableId) {
        amazonEC2.associateRouteTable(
                new AssociateRouteTableRequest().
                withRouteTableId(routeTableId).
                withSubnetId(subnetId));
        logger.debug("  Route table associated with subnet.");
    }

    private String createRouteTable(AmazonEC2 amazonEC2, String vpcId,
            String gatewayId, String destCidrBlock) {
        DescribeRouteTablesResult routeTablesResult = amazonEC2.describeRouteTables();
        String routeTableId = null;
        for (RouteTable routeTable : routeTablesResult.getRouteTables()) {
            if (routeTable.getVpcId().equals(vpcId)) {
                routeTableId = routeTable.getRouteTableId();
                break;
            }
        }
        if (routeTableId != null) {
            amazonEC2.createRoute(new CreateRouteRequest().
                    withDestinationCidrBlock(destCidrBlock).  //everyone has access.
                    withRouteTableId(routeTableId).
                    withGatewayId(gatewayId));

            logger.debug("  Route created.");
        } else {
            logger.debug("  Route not created.");
        }
        return routeTableId;
    }

    private String createInternetGateway(AmazonEC2 amazonEC2, String vpcId) {
        CreateInternetGatewayResult gateway = amazonEC2.createInternetGateway();
        String gatewayId = gateway.getInternetGateway().getInternetGatewayId();
        logger.info("  Internet Gateway id: " + gatewayId);
        amazonEC2.attachInternetGateway(new AttachInternetGatewayRequest().
                withVpcId(vpcId).
                withInternetGatewayId(gatewayId));
        logger.info("  Attached internet gateway.");
        return gatewayId;
    }

    private synchronized String createVpc(AmazonEC2 amazonEC2, String cidrBlock) {
        CreateVpcResult vpc = amazonEC2.createVpc(new CreateVpcRequest(cidrBlock));
        String vpcId = vpc.getVpc().getVpcId();
        logger.info("VPC Create with ID: " + vpcId);
        return vpcId;
    }

    private synchronized String createSubnet(AmazonEC2 amazonEC2, String vpcId,
            String cidrBlock, String zone) {
        CreateSubnetResult subnet = amazonEC2.createSubnet(
                new CreateSubnetRequest(vpcId, cidrBlock).
                withAvailabilityZone(zone));
        String subnetId = subnet.getSubnet().getSubnetId();
        logger.info(" Subnet Id: " + subnetId);
        return subnetId;
    }

    public void grantAmi(AWSCredentials credentials, Collection<AmiDescriptor> amisDescs) {
        //String accessKey = "AKIAIA7HTUW2JJUSU3GQ"; Mike's
        //        String accessKey = "AKIAIC7FI2GOA5KFMVMA";
        //        String secretKey = "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"; Mike's
        //        String secretKey = "WbezhI716zaJtDYFSGcxS2sa9r6I00f0HtKjuN8g";

        //amis can belong to different accts.

        String acctNumber = getAcctNumber(credentials);
        logger.info("Acct num: " + acctNumber);

        Map<AwsKeyInfo, List<AmiDescriptor>> acctAmis = new HashMap<>();

        for (AmiDescriptor amisDesc : amisDescs) {
            AwsKeyInfo key = new AwsKeyInfo(amisDesc.getAwsKey(), amisDesc.getAwsSecretKey());
            List<AmiDescriptor> descriptors = acctAmis.get(key);
            if (descriptors == null) {
                descriptors = new ArrayList<>();
                acctAmis.put(key, descriptors);
            }

            descriptors.add(amisDesc);
        }

        for (Map.Entry<AwsKeyInfo, List<AmiDescriptor>> entry : acctAmis.entrySet()) {
            AwsKeyInfo key = entry.getKey();
            final AmazonEC2 productEc2 = amazonFactory.getAmazonEC2(
                    new BasicAWSCredentials(key.getAwsKey(), key.getAwsSecretKey()));
            List<String> amiNames = new ArrayList<>();
            for (AmiDescriptor descriptor : entry.getValue()) {
                amiNames.add(descriptor.getAmi());
            }
            logger.info("Descibe images..");
            final DescribeImagesResult describeImagesResult = productEc2.
                    describeImages(new DescribeImagesRequest().withImageIds(amiNames));
            for (Image image : describeImagesResult.getImages()) {
                productEc2.modifyImageAttribute(new ModifyImageAttributeRequest().withImageId(image.getImageId()).
                        withLaunchPermission(new LaunchPermissionModifications().
                                withAdd(new LaunchPermission().withUserId(acctNumber))));
            }
            logger.info("Done granting images.");
        }
    }

    private String getAcctNumber(AWSCredentials destCredentials) {
        final AmazonIdentityManagement aim = amazonFactory.getAmazonIdentityManagement(destCredentials);
        final User user = aim.getUser().getUser();
        logger.debug("User ID: " + user.getUserId());
        return user.getUserId();
    }

    public AmazonEC2 getAmazonEC2(BasicAWSCredentials awsCredentials, Long regionId) {
        AmazonEC2 rtn = amazonFactory.getAmazonEC2(awsCredentials);
        
        Region region = regionDao.findById(regionId);
        logger.debug("Setting AmazonEC2 with Region: " + regionId);
        
        rtn.setEndpoint(region.getEndPoint());
        
        return rtn;
    }


    public Map<String, AmiDescriptor> createInstances(final AmazonEC2 destAmazonEC2, VpcDefinition vpcDef,
            Collection<AmiDescriptor> amisDescs, String userData) {

        Map<String, AmiDescriptor> instanceAmiMap = new HashMap<>();
        for (AmiDescriptor amisDesc : amisDescs) {
            final String instanceId = runInstance(destAmazonEC2, vpcDef, amisDesc, userData);
            instanceAmiMap.put(instanceId, amisDesc);
        }

        return instanceAmiMap;

    }


    private void sleep(int seconds) {
        long time = seconds * 1000;
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            logger.error("Sleep Interrupted!");
        }
    }

    private String runInstance(AmazonEC2 amazonEC2, VpcDefinition vpcDef, AmiDescriptor amisDesc, String userData) {
        String encUserData = null;
        if (userData != null) {
            encUserData = Base64Utility.encode(userData.getBytes());
        }
        return runInstance(amazonEC2, vpcDef.getPublicSubnetId(), vpcDef.getKeyPairName(),
                vpcDef.getSecurityGrougId(), amisDesc.getAmi(), amisDesc.getIpAddress(), encUserData,
                InstanceType.fromValue(amisDesc.getSize()));
    }

    public void tagInstances(AmazonEC2 destEC2, Map<String, AmiDescriptor> instanceAmiMap, String deploymentName) {
        for (Map.Entry<String, AmiDescriptor> entry : instanceAmiMap.entrySet()) {
            String tagName = entry.getValue().getTagName();
            if (deploymentName != null) {
                tagName += ("_" + deploymentName);
            }
            createNameTag(destEC2, entry.getKey(), tagName);
        }
    }

    public void setEBSforDelete(AmazonEC2 destEC2, Set<String> instanceIds) {

        final List<Reservation> reservations = destEC2.describeInstances(new DescribeInstancesRequest().withInstanceIds(instanceIds)).getReservations();
        for (Reservation reservation : reservations) {
            for (Instance instance : reservation.getInstances()) {
                setDevicesToDeleteOnTerminate(destEC2, instance);
            }
        }

    }

    private static class AwsKeyInfo {
        private String awsKey;
        private String awsSecretKey;

        private AwsKeyInfo(String awsKey, String awsSecretKey) {
            this.awsKey = awsKey;
            this.awsSecretKey = awsSecretKey;
        }

        public String getAwsKey() {
            return awsKey;
        }

        public void setAwsKey(String awsKey) {
            this.awsKey = awsKey;
        }

        public String getAwsSecretKey() {
            return awsSecretKey;
        }

        @SuppressWarnings("UnusedDeclaration")
        public void setAwsSecretKey(String awsSecretKey) {
            this.awsSecretKey = awsSecretKey;
        }

        @Override
        public int hashCode() {
            return awsKey.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            AwsKeyInfo that = (AwsKeyInfo) o;

            //noinspection RedundantIfStatement
            if (!awsKey.equals(that.awsKey)) return false;

            return true;
        }
    }

    public ElasticIp getElasticIp(AmazonEC2 amazonEC2) {
        //For parallelization reasons...Do not reuse elastic ips.
        //        List<Address> addresses = amazonEC2.describeAddresses().getAddresses();
        //        ElasticIp elasticIp = null;
        //        boolean found = false;
        //        for (Address address : addresses) {
        //            if (DomainType.Vpc.toString().equals(address.getDomain()) && null == address.getAssociationId()) {
        //                elasticIp = new ElasticIp(address.getAllocationId(), address.getPublicIp());
        //                found = true;
        //            }
        //            logger.info("Allocation ID: " + address.getAllocationId());
        //            logger.info("Association ID:" + address.getAssociationId());
        //            logger.info("Domain: " + address.getDomain());
        //            logger.info("Instance ID: " + address.getInstanceId());
        //            logger.info("Public IP" + address.getPublicIp());
        //            if (found) {
        //                break;
        //            }
        //        }

        //        if (!found) {

        logger.info("Allocating new Elastic IP.");
        final AllocateAddressResult result = amazonEC2.allocateAddress(
                new AllocateAddressRequest().withDomain(DomainType.Vpc));

        ElasticIp elasticIp = new ElasticIp(result.getAllocationId(), result.getPublicIp());
        logger.info("Allocation new Elastic IP Address.");
        //        }
        return elasticIp;
    }

    private String createSGTcpCidr(AmazonEC2 amazonEC2, String vpcId, String securityGroupName,
            String description, int minPort, int maxPort, String ipRanges) {
        String securityGroupId = null;
        List<SecurityGroup> securityGroups = amazonEC2.describeSecurityGroups().getSecurityGroups();
        for (SecurityGroup securityGroup : securityGroups) {
            if (vpcId.equals(securityGroup.getVpcId())) {
                if (securityGroup.getGroupName().equals(securityGroupName)) {
                    securityGroupId = securityGroup.getGroupId();
                    logger.info("Security Group found: " + securityGroupId);
                    break;
                }
            }
        }

        if (securityGroupId == null) {

            securityGroupId = amazonEC2.createSecurityGroup(new CreateSecurityGroupRequest()
            .withGroupName(securityGroupName)
            .withDescription(description)
            .withVpcId(vpcId)).getGroupId();
            amazonEC2.authorizeSecurityGroupIngress(
                    new AuthorizeSecurityGroupIngressRequest()
                    .withGroupId(securityGroupId)
                    .withIpPermissions(new IpPermission()
                    .withIpProtocol("tcp")
                    .withFromPort(minPort).withToPort(maxPort)
                    .withIpRanges(ipRanges))
                    );
            logger.info("Created security group: " + securityGroupId);

        }
        return securityGroupId;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getUserData(String cidrBlock, String elasticIp, String region, String vpcId) {

        CidrIPAddresss vpcIp = new CidrIPAddresss(cidrBlock);
        logger.info("cidr: " + vpcIp.cidrBlock);
        logger.info("ip: " + vpcIp.ipAddress);
        logger.info("mask: " + vpcIp.subnetMask);


        HashMap<String, String> data = new HashMap<>();
        data.put("VPC_NET_IP", vpcIp.ipAddress); //private subnet VPC network ip
        data.put("VPC_IP_NET_MASK", vpcIp.subnetMask); // VPC private subnet mask
        data.put("OPEN_VPN_EIP_ADDR", elasticIp);//Elastic IP address
        data.put("AWS_ACCESS_KEY", amazonFactory.getAccessKey());
        data.put("AWS_SECRET_KEY", amazonFactory.getSecretKey());
        data.put("AWS_REGION", region);
        data.put("VPC_ID", vpcId);
        return templateProvider.processTemplate("userdata/openVPN_userdata2.ftl", data);
    }


    private static class CidrIPAddresss {
        public String cidrBlock;
        public String ipAddress;
        public String subnetMask;

        public CidrIPAddresss(String cidrBlock) {
            this.cidrBlock = cidrBlock;
            convert(cidrBlock);
        }

        private void convert(String cidrBlock) {
            String[] parts = cidrBlock.split("/");
            ipAddress = parts[0];
            int prefix;
            if (parts.length < 2) {
                prefix = 0;
            } else {
                prefix = Integer.parseInt(parts[1]);
            }
            int mask = 0xffffffff << (32 - prefix);
            logger.debug("Prefix=" + prefix);
            logger.debug("Address=" + ipAddress);

            byte[] bytes = new byte[]{
                    (byte) (mask >>> 24), (byte) (mask >> 16 & 0xff), (byte) (mask >> 8 & 0xff), (byte) (mask & 0xff)};

            InetAddress netAddr;
            try {
                netAddr = InetAddress.getByAddress(bytes);
                logger.debug("Mask=" + netAddr.getHostAddress());
                subnetMask = netAddr.getHostAddress();
            } catch (UnknownHostException e) {
                logger.error("Unknown Host: ", e);
            }

        }

    }


    public void stopVpcInstances(AmazonEC2 amazonEC2, String vpcId) {

        logger.info("Get the instances..");
        List<String> instances = getVpcInstances(amazonEC2, vpcId);
        logger.info("Found instances: " + instances.size());

        stopInstances(amazonEC2, instances);
        logger.info("  Waiting for instances to stop.");

        List<Reservation> reservations;

        while (instances.size() > 0) {
            reservations = getReservations(amazonEC2, instances);
            for (Reservation reservation : reservations) {
                for (Instance instance : reservation.getInstances()) {
                    if (80 == instance.getState().getCode()) {
                        logger.info("   Instance stopped: " + instance.getInstanceId());
                        instances.remove(instance.getInstanceId());
                    }
                }
            }

            sleep(30);
        }

    }

    public void stopInstances(AmazonEC2 amazonEC2, List<String> instances) {
        if (instances.size() == 0) return;

        logger.info("  Stopping instances: ", instances.size());
        amazonEC2.stopInstances(new StopInstancesRequest(instances));
    }

    public List<String> startVpcInstances(AmazonEC2 amazonEC2, String vpcId) {
        logger.info("Get the instances..");
        List<String> instances = getVpcInstances(amazonEC2, vpcId);
        logger.info("Found instances: " + instances.size());

        startInstances(amazonEC2, instances);

        logger.info("Waiting for instances to start.");

        List<Reservation> reservations;

        while (instances.size() > 0) {
            reservations = getReservations(amazonEC2, instances);
            for (Reservation reservation : reservations) {
                for (Instance instance : reservation.getInstances()) {
                    if (16 == instance.getState().getCode()) {
                        logger.info("   Instance started: " + instance.getInstanceId());
                        instances.remove(instance.getInstanceId());
                    }
                }
            }


            sleep(30);
        }

        return instances;
    }

    public void startInstances(final AmazonEC2 amazonEC2, final List<String> instances) {
        if (instances.size() == 0) return;

        logger.info("Starting instances: ", instances.size());
        amazonEC2.startInstances(new StartInstancesRequest(instances));
    }

    protected boolean waitForRdpService(ElasticIp eip) {
        logger.info("Waiting for RDP server on elastic IP to be available:" + eip.getPublicIp());
        String publicIp = eip.getPublicIp();
        boolean retValue = false;
        InetAddress address;
        try {
            address = InetAddress.getByName(publicIp);
        } catch (UnknownHostException e) {
            logger.error("UnknownHost encountered", e);
            //e.printStackTrace();
            return false;
        }
        Socket clientSocket = null;
        for (int i = 0; i < 60; i++) {

            try {
                logger.info("Trying to connect...3389");
                //close in finally block.
                clientSocket = new Socket(address, 3389);
                if (clientSocket.isConnected()) {
                    logger.info("Connected to 3389!");
                    //clientSocket.close();
                    retValue = true;
                    break;
                }

            } catch (ConnectException ce) {
                logger.info("ConnectException encountered:" + ce.getMessage());
                try {
                    Thread.sleep(60000L);
                } catch (InterruptedException ie) {
                    //interrupted
                }
            } catch (IOException e) {
                logger.error("IOException encountered", e);
            } finally {
                if (clientSocket != null) {
                    try {
                        if (clientSocket.isConnected()) {
                            clientSocket.close();
                        }
                    } catch (IOException e) {
                        logger.error("Exception encountered closing socket", e);

                    }
                }
            }
        }
        if (!retValue) {
            logger.info("Elastic IP has not become reachable in more than an hour.");
        }
        return retValue;
    }

}
