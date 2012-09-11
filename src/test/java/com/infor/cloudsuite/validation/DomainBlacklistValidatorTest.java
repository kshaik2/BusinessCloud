package com.infor.cloudsuite.validation;

import static junit.framework.Assert.assertEquals;

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.infor.cloudsuite.AbstractTest;
import com.infor.cloudsuite.dao.DomainBlacklistDao;
import com.infor.cloudsuite.dto.ContraintViolationDto;
import com.infor.cloudsuite.entity.DomainBlacklist;
import com.infor.cloudsuite.entity.Validation;
import com.infor.cloudsuite.platform.components.ValidationProvider;

/**
 * User: bcrow
 * Date: 11/9/11 4:32 PM
 */
@Transactional(propagation = Propagation.REQUIRED)
public class DomainBlacklistValidatorTest extends AbstractTest {

    @Resource
    private DomainBlacklistDao blacklistDao;
    @Resource
    private ValidationProvider validationProvider;

    @Test
    public void testDomainBlacklistValidation() throws Exception {
        String email = "someone@gmail.com";
        Validation val = new Validation();
        val.setEmail(email);
        ContraintViolationDto<Validation> dto;
        dto = validationProvider.validate(val);
        assertEquals(0, dto.getViolations().size());

        val.setEmail("someone");
        dto = validationProvider.validate(val);
        assertEquals(1, dto.getViolations().size());

        insertDomain("gmail.com");

        val.setEmail(email);
        dto = validationProvider.validate(val);
        assertEquals(1, dto.getViolations().size());

        val.setEmail("gmail.com");
        dto = validationProvider.validate(val);
        assertEquals(2, dto.getViolations().size());

        insertDomain("yaho.*");

        val.setEmail("someone@yahoo.com");
        dto = validationProvider.validate(val);
        assertEquals(1, dto.getViolations().size());

        insertDomain("infor.org");

        val.setEmail("someone@infor.com");
        dto = validationProvider.validate(val);
        assertEquals(0, dto.getViolations().size());

        insertDomain("sun\\..*");

        val.setEmail("someone@sunning.com");
        dto = validationProvider.validate(val);
        assertEquals(0, dto.getViolations().size());

        val.setEmail("someone@sun.org");
        dto = validationProvider.validate(val);
        assertEquals(1, dto.getViolations().size());

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void insertDomain(String domain) {
        blacklistDao.save(new DomainBlacklist(domain));
    }
}
