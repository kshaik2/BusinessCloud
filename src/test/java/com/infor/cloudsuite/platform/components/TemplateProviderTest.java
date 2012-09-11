package com.infor.cloudsuite.platform.components;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.infor.cloudsuite.AbstractTest;
import com.infor.cloudsuite.service.StringDefs;

/**
 * User: bcrow
 * Date: 10/28/11 3:52 PM
 */
public class TemplateProviderTest extends AbstractTest{

    @Resource
    private TemplateProvider templateProvider;

    @Test
    @Transactional(propagation = Propagation.REQUIRED)
    public void testProcessTemplate() throws Exception {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("firstName", "Test");
        map.put("activationUrl", "http://activation/url/1/1234567890");
        String result = templateProvider.processTemplate(StringDefs.MESSAGE_ACTIVATION_TEMPATE, Locale.getDefault(), map);
        assertNotNull("Template not null", result);
        assertTrue("Not Empty",!result.isEmpty());
        assertTrue("Contains name", result.contains("Test"));
        assertTrue("Contains Url", result.contains("http://activation/url/1/1234567890"));

    }
}
