package com.infor.cloudsuite.platform.components;

import javax.annotation.Resource;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.infor.cloudsuite.AbstractTest;
import com.infor.cloudsuite.platform.amazon.AmazonFactory;
import com.infor.cloudsuite.platform.amazon.StackBuilder;

/**
 * Created with IntelliJ IDEA.
 * User: briancrow
 * Date: 5/30/12
 * Time: 2:55 PM
 */
//@Transactional
public class ExpCloudCreatorTest extends AbstractTest {
    private static Logger logger = LoggerFactory.getLogger(ExpCloudCreatorTest.class);

    @Resource
    private StackBuilder stackBuilder;
    @Resource
    private AmazonFactory amazonFactory;

    private static final String gdeKey = "AKIAIC7FI2GOA5KFMVMA";
    private static final String gdeSecret = "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";

    @Test
    public void testCreateVPC() {
//        stackBuilder.createStack(new CreateStackRequest());
    }

    @Test
    public void testTearDownVPC() {
//        stackBuilder.tearDownStack(amazonFactory.getAccessKey(),
//                amazonFactory.getSecretKey(),
//                "")
    }

    @Test
    public void testStartVPC() {
//        stackBuilder.startStack(amazonFactory.getAccessKey(), amazonFactory.getSecretKey(), "vpc-4574792d");
    }

    @Test
    public void testStopVPC() {
//        stackBuilder.stopStack(amazonFactory.getAccessKey(), amazonFactory.getSecretKey(), "vpc-4574792d");
    }

}
