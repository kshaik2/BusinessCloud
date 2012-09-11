package com.infor.cloudsuite.platform.amazon;

import javax.annotation.PostConstruct;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cloudformation.AmazonCloudFormation;
import com.amazonaws.services.cloudformation.AmazonCloudFormationClient;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;

/**
 * User: bcrow
 * Date: 11/16/11 9:16 AM
 */
public class AmazonFactory {
    private String accessKey;
    private String secretKey;
    private AWSCredentials credentials;


    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    @PostConstruct
    public void init() {
        this.credentials = new BasicAWSCredentials(accessKey, secretKey);
    }

    public AWSCredentials getCredentials(String accessKey, String secretKey) {
        return new BasicAWSCredentials(accessKey, secretKey);
    }
    
    public AmazonCloudFormation getCloudFormation() {
        return new AmazonCloudFormationClient(credentials);
    }

    public AmazonCloudFormation getCloudFormation(AWSCredentials awsCredentials) {
        return new AmazonCloudFormationClient(awsCredentials);
    }

    public AmazonS3 getAmazonS3() {
        return new AmazonS3Client(credentials);
    }

    public AmazonS3 getAmazonS3(AWSCredentials awsCredentials) {
        return new AmazonS3Client(awsCredentials);
    }
    
    public AmazonEC2 getAmazonEC2() {
        return new AmazonEC2Client(credentials);
    }

    public AmazonEC2 getAmazonEC2(AWSCredentials awsCredentials) {
        return new AmazonEC2Client(awsCredentials);
    }

    public AmazonSNS getAmazonSNS() {
        return new AmazonSNSClient(credentials);
    }

    public AmazonSNS getAmazonSNS(AWSCredentials awsCredentials) {
        return new AmazonSNSClient(awsCredentials);
    }

    public AmazonIdentityManagement getAmazonIdentityManagement() {
        return new AmazonIdentityManagementClient(credentials);
    }

    public AmazonIdentityManagement getAmazonIdentityManagement(AWSCredentials awsCredentials) {
        return new AmazonIdentityManagementClient(awsCredentials);
    }
}
