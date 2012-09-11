package com.infor.cloudsuite.validation;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.infor.cloudsuite.AbstractTest;
import com.infor.cloudsuite.dto.ContraintViolationDto;
import com.infor.cloudsuite.dto.PasswordCompleter;
import com.infor.cloudsuite.platform.components.ValidationProvider;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * User: bcrow
 * Date: 10/27/11 10:41 AM
 */
@Transactional(propagation = Propagation.REQUIRED)
public class PasswordValidatorTest extends AbstractTest {
    private static final Logger logger = LoggerFactory.getLogger(PasswordValidatorTest.class);


    @Resource
    private ValidationProvider validationService;

    @Test
    public void testPasswordValidationAll() throws Exception {

        Passwords test1 = new Passwords("one", "two");
        final ContraintViolationDto<Passwords> dto = validationService.validate(test1);
        assertTrue("Has violations.", dto.isHasViolations());
        assertEquals("1 violation exist", 1, dto.getViolations().size());

        for (ConstraintViolation<Passwords> violation : dto.getViolations()) {
            logger.debug("   " + violation.getMessage());
            logger.debug("   " + violation.getPropertyPath());
            logger.debug("   " + violation.getMessageTemplate());
        }


    }

    @Test
    public void testPasswordsTooShort() throws Exception {
        Passwords test1 = new Passwords("", "");
        final ContraintViolationDto<Passwords> dto = validationService.validate(test1);
        assertTrue("Has violations.", dto.isHasViolations());
        assertEquals("1 violation exist", 1, dto.getViolations().size());

        for (ConstraintViolation<Passwords> violation : dto.getViolations()) {
            logger.debug("   " + violation.getMessage());
            logger.debug("   " + violation.getPropertyPath());
            logger.debug("   " + violation.getMessageTemplate());
        }
    }

    @Test
    public void testNoPasswordNoValidationErrors() throws Exception {
        Passwords test1 = new Passwords("oneone", "oneone");
        final ContraintViolationDto<Passwords> dto = validationService.validate(test1);
        assertFalse("Has violations.", dto.isHasViolations());
        assertEquals("0 violation exist", 0, dto.getViolations().size());

    }

    class Passwords implements PasswordCompleter {

        String password;
        String password2;

        Passwords(String password, String password2) {
            this.password = password;
            this.password2 = password2;
        }

        @Override
        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }


        @Override
        public String getPassword2() {
            return password2;
        }

        public void setPassword2(String password2) {
            this.password2 = password2;
        }
    }
}
