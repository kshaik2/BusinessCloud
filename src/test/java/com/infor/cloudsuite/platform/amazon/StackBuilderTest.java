package com.infor.cloudsuite.platform.amazon;

import java.util.Collections;
import java.util.Locale;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.infor.cloudsuite.AbstractTest;
import com.infor.cloudsuite.dao.RegionDao;
import com.infor.cloudsuite.entity.DeploymentStack;
import com.infor.cloudsuite.platform.components.EmailProvider;
import com.infor.cloudsuite.platform.components.NullEmailProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: bcrow
 * Date: 6/15/12 10:04 AM
 */
public class StackBuilderTest extends AbstractTest {

    @Resource
    StackBuilder stackBuilder;
    @Resource
    RegionDao regionDao;
    
    private EmailProvider emailProviderBefore;
    private NullEmailProvider nullEmailProvider;
    
    @Before
    public void setUp() {
    	this.nullEmailProvider=new NullEmailProvider();
    	this.emailProviderBefore=stackBuilder.getEmailProvider();
    	stackBuilder.setEmailProvider(nullEmailProvider);
    }
    
    @After
    public void finish() {
    	stackBuilder.setEmailProvider(this.emailProviderBefore);
    }
    
    @Test
    public void testEmail() {

        DeploymentStack deploymentStack = new DeploymentStack();
        deploymentStack.setId(1L);
        deploymentStack.setElasticIp("100.0.0.1");
        deploymentStack.setUrl("rdp://100.0.0.1");
        deploymentStack.setVpcUsername("gdeinfor2\\Administrator");
        deploymentStack.setVpcPassword("G!oba!08");
        deploymentStack.setNumServers(5);
        deploymentStack.setRegion(regionDao.findById(4L));
        CreateStackRequest createStackRequest = new CreateStackRequest();
        createStackRequest.setLocale(Locale.US);
        createStackRequest.setDestEmails(Collections.singletonList("no-one@infor.com"));
        final String eamName = "Infor10 EAM Enterprise";
        createStackRequest.getProductNames().add(eamName);
        final String lnName = "Infor10 LN Business";
        createStackRequest.getProductNames().add(lnName);
        createStackRequest.setDeploymentStack(deploymentStack);
        stackBuilder.sendStackCompleteEmail(createStackRequest);
        assertEquals("number of emails not equal.", 1, nullEmailProvider.getEmails().size());
        assertTrue(nullEmailProvider.getEmails().get(0).text.contains(eamName));
        assertTrue(nullEmailProvider.getEmails().get(0).text.contains(lnName));
        assertTrue(nullEmailProvider.getEmails().get(0).text.contains("rdp://100.0.0.1"));
        assertTrue(nullEmailProvider.getEmails().get(0).text.contains("G!oba!08"));
        assertTrue(nullEmailProvider.getEmails().get(0).address.equals("no-one@infor.com"));
    }

    @Test
    public void testFailedEmail() {

        DeploymentStack deploymentStack = new DeploymentStack();
        deploymentStack.setId(1L);
        deploymentStack.setElasticIp("100.0.0.1");
        deploymentStack.setUrl("rdp://100.0.0.1");
        deploymentStack.setVpcUsername("gdeinfor2\\Administrator");
        deploymentStack.setVpcPassword("G!oba!08");
        deploymentStack.setNumServers(5);
        deploymentStack.setRegion(regionDao.findById(4L));
        CreateStackRequest createStackRequest = new CreateStackRequest();
        createStackRequest.setLocale(Locale.US);
        createStackRequest.setDestEmails(Collections.singletonList("no-one@infor.com"));
        final String eamName = "Infor10 EAM Enterprise";
        createStackRequest.getProductNames().add(eamName);
        final String lnName = "Infor10 LN Business";
        createStackRequest.getProductNames().add(lnName);
        createStackRequest.setDeploymentStack(deploymentStack);
        final StackResults results = new StackResults();
        results.setComplete(false);
        results.setException("Failed message");
        results.setRollback(true);
        results.setRollbackException("Rollback exception message.");
        stackBuilder.sendStackFailedRollBackEmail(results, createStackRequest);
        System.out.println(nullEmailProvider.getEmails().get(0).text);
        assertEquals("number of emails not equal.", 1, nullEmailProvider.getEmails().size());
        assertTrue(nullEmailProvider.getEmails().get(0).text.contains(eamName));
        assertTrue(nullEmailProvider.getEmails().get(0).text.contains(lnName));
        assertTrue(nullEmailProvider.getEmails().get(0).address.contains("no-one@infor.com"));
        assertTrue(nullEmailProvider.getEmails().get(0).text.contains(deploymentStack.getRegion().getName()));
    }


}

