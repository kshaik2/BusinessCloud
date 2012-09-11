package com.infor.cloudsuite;

import java.lang.reflect.Field;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

/**
 * User: bcrow
 * Date: 8/7/12 11:23 AM
 */
public class MockitoTestExecutionListener extends AbstractTestExecutionListener {

    private static Logger logger = LoggerFactory.getLogger(MockitoTestExecutionListener.class);

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        initMockito(testContext);
    }

    private void initMockito(TestContext testContext) {
        logger.debug("initMokito()");
        Class<?> testClass = testContext.getTestClass();
        logger.debug("  testClass: " + testClass.getName());
        final Mocked annotation = testClass.getAnnotation(Mocked.class);
        logger.debug("  Mocked annotation: " + annotation);
        if (annotation != null) {
            logger.info("Initializing Mockito...");
            MockitoAnnotations.initMocks( testContext.getTestInstance());
            if (logger.isDebugEnabled()) {
                final Field[] fields = testClass.getDeclaredFields();
                for (Field field : fields) {
                    logger.info("    Field: " + field.getName() + ": " + field.getAnnotation(Mock.class));
                }
            }
        }
    }
}
