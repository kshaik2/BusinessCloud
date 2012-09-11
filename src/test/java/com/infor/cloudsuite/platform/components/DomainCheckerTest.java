package com.infor.cloudsuite.platform.components;

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.infor.cloudsuite.AbstractTest;
import com.infor.cloudsuite.dao.DomainBlacklistDao;
import com.infor.cloudsuite.entity.DomainBlacklist;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * User: bcrow
 * Date: 11/9/11 3:51 PM
 */
@Transactional(propagation = Propagation.REQUIRED)
public class DomainCheckerTest extends AbstractTest {

    @Resource
    DomainChecker domainChecker;
    @Resource
    private DomainBlacklistDao blacklistDao;

    @Test
    public void testHasValidDomain() throws Exception {

        assertTrue("Domain Valid", domainChecker.hasValidDomain("nothing@nothing.com"));
        addDomain("nothing.com");
        assertFalse("Domain invalid", domainChecker.hasValidDomain("nothing@nothing.com"));
        assertFalse("Domain invalid", domainChecker.hasValidDomain("nothing@NoThInG.com"));

        assertTrue("Domain Valid", domainChecker.hasValidDomain(""));
        assertTrue("Domain Valid", domainChecker.hasValidDomain("username"));
        assertFalse("Domain Valid", domainChecker.hasValidDomain("nothing.com"));//return the string if no @ found.

        assertTrue("Domain Valid", domainChecker.hasValidDomain("someone@gmail.com"));
        addDomain("gmail\\..*");
        assertTrue("Domain valid", domainChecker.hasValidDomain("someone@gmailing.com"));
        assertFalse("Domain invalid", domainChecker.hasValidDomain("someone@gmail.com"));
        assertFalse("Domain invalid", domainChecker.hasValidDomain("nothing@nothing.com"));

        assertTrue("", domainChecker.hasValidDomain("@someDomain.com"));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void addDomain(String domain) {
        blacklistDao.save(new DomainBlacklist(domain));
    }
}
