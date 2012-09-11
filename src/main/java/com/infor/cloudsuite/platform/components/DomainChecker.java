package com.infor.cloudsuite.platform.components;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.infor.cloudsuite.dao.DomainBlacklistDao;
import com.infor.cloudsuite.entity.DomainBlacklist;

/**
 * User: bcrow
 * Date: 11/9/11 3:06 PM
 */
@Component
public class DomainChecker {
    private static final Logger logger = LoggerFactory.getLogger(DomainChecker.class);

    @Resource
    private DomainBlacklistDao blacklistDao;

    public boolean hasValidDomain(String emailAddress) {
        final List<DomainBlacklist> blacklists = blacklistDao.findAll();
        if (blacklists.isEmpty()) {
            logger.debug("No blacklists.");
            return true;
        }
        String regExp = combineBlacklists(blacklists).toLowerCase();
        logger.debug("Regular expression: " + regExp);
        String domain = stripAddress(emailAddress).toLowerCase();
        logger.debug("domain:" +  domain);
        return !domain.matches(regExp);
    }

    private String stripAddress(String emailAddress) {
        final int at = emailAddress.indexOf('@');
        if (at < 0) {
            return emailAddress;
        }
        if(at + 1 == emailAddress.length()) {
            return "";
        }
        return emailAddress.substring(at + 1);
    }

    private String combineBlacklists(List<DomainBlacklist> blacklists) {
        StringBuilder regBuilder = new StringBuilder();
        for (DomainBlacklist blacklist : blacklists) {
            regBuilder.append("(");
            regBuilder.append(blacklist.getDomain());
            regBuilder.append(")|");
        }
        regBuilder.deleteCharAt(regBuilder.lastIndexOf("|"));
        return regBuilder.toString();
    }
}
