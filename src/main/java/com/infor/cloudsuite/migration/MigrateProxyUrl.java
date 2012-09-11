package com.infor.cloudsuite.migration;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.infor.cloudsuite.dao.TrialInstanceDao;
import com.infor.cloudsuite.entity.TrialInstance;
import com.infor.cloudsuite.platform.components.GuidProvider;

/**
 * Migrate to using a proxy url. This means adding a GUID to all trials that are already provisioned.
 * <p/>
 * User: bcrow
 * Date: 1/10/12 9:46 AM
 */
@Component
public class MigrateProxyUrl extends Migrator {
    private static final Logger logger = LoggerFactory.getLogger(MigrateProxyUrl.class);

    @Resource
    private TrialInstanceDao trialInstanceDao;
    @Resource
    private GuidProvider guidProvider;

    @Transactional
    public void migrate() {
        logger.info("Checking for TrialInstace objects to migrate.");

        final List<TrialInstance> instances = trialInstanceDao.findByGuidIsNull();
        if (instances.size() > 0) {
            logger.info("Migrating {} trialInstances", instances.size());
            for (TrialInstance instance : instances) {
                instance.setGuid(guidProvider.generateGuid());
                trialInstanceDao.save(instance);
            }
        } else {
            logger.info("No trial instances to migrate.");

        }


    }
}
