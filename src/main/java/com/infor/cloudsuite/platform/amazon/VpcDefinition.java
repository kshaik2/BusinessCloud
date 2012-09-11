package com.infor.cloudsuite.platform.amazon;

/**
 * User: bcrow
 * Date: 6/12/12 12:20 PM
 */
public class VpcDefinition {

    private String vpcId;
    private String region;
    private String zone;
    private String publicSubnetId;
    private String privateSubnetId;
    private String securityGrougId;
    private String keyPairName;

    public VpcDefinition() {
    }

    public String getVpcId() {
        return vpcId;
    }

    public void setVpcId(String vpcId) {
        this.vpcId = vpcId;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getPublicSubnetId() {
        return publicSubnetId;
    }

    public void setPublicSubnetId(String publicSubnetId) {
        this.publicSubnetId = publicSubnetId;
    }

    public String getPrivateSubnetId() {
        return privateSubnetId;
    }

    public void setPrivateSubnetId(String privateSubnetId) {
        this.privateSubnetId = privateSubnetId;
    }

    public String getSecurityGrougId() {
        return securityGrougId;
    }

    public void setSecurityGrougId(String securityGrougId) {
        this.securityGrougId = securityGrougId;
    }

    public String getKeyPairName() {
        return keyPairName;
    }

    public void setKeyPairName(String keyPairName) {
        this.keyPairName = keyPairName;
    }
}
