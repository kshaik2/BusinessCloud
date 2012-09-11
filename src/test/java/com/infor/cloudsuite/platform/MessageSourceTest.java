package com.infor.cloudsuite.platform;

import java.util.Locale;

import javax.annotation.Resource;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.infor.cloudsuite.AbstractTest;
import com.infor.cloudsuite.platform.components.MessageProvider;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * User: bcrow
 * Date: 10/31/11 1:23 PM
 */
@Transactional(propagation = Propagation.REQUIRED)
public class MessageSourceTest extends AbstractTest {
    private static final Logger logger = LoggerFactory.getLogger(MessageSourceTest.class);

    @Resource
    MessageProvider messageProvider;

    @Test
    public void testMessageProvider() throws Exception {
        String message = messageProvider.getMessage("hello.world", "Test", "User");
        assertNotNull("base message is not null", message);
        assertTrue("base message is from i18n.", message.contains("From i18n"));
        logger.debug(message);
        message = messageProvider.getMessage("hello.world", new Locale("pt","BR"), "Test", "Hello");
        assertNotNull("pt message is not null", message);
        assertTrue("pt Message is from i18n.", message.contains("De i18n"));
        assertTrue("pt Message is from BP.", message.contains("BP"));
        logger.debug(message);
    }

    @Test
    public void testMessageProviderNoCode() throws Exception {
        String message = messageProvider.getMessage("NotExist", "Test", "User");
        assertNull("message is null", message);
    }

    @Test
    public void testMessageProviderNoCodeDefault() throws Exception {
        String message = messageProvider.getMessageDef("NotExist", "DefaultMessage", "Test", "User");
        assertNotNull("message is not null", message);
        assertTrue("Message is the default message.", message.equals("DefaultMessage"));
        logger.debug(message);
    }

    @Test
    public void testMessageFromBaseSource() throws Exception {
        String message = messageProvider.getMessage("com.infor.cloudsuite.passwordMismatch");
        assertNotNull("message is not null", message);
        assertTrue("Message is the correct message.", message.equals("The provided passwords do not match"));
        logger.debug(message);
    }
}
