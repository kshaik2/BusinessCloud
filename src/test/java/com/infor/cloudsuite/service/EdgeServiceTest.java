package com.infor.cloudsuite.service;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import com.infor.cloudsuite.AbstractTest;
import com.infor.cloudsuite.dao.ProductDao;
import com.infor.cloudsuite.dao.ProductVersionDao;
import com.infor.cloudsuite.dao.RegionDao;
import com.infor.cloudsuite.dao.TrialInstanceDao;
import com.infor.cloudsuite.dao.UserDao;
import com.infor.cloudsuite.dto.ProductDto;
import com.infor.cloudsuite.dto.TrialEnvironmentDto;
import com.infor.cloudsuite.entity.TrialInstance;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * User: bcrow
 * Date: 12/2/11 3:11 PM
 */
@Transactional
public class EdgeServiceTest extends AbstractTest {

    @Resource
    private TrialInstanceDao trialInstanceDao;
    @Resource
    private EdgeService edgeService;
    @Resource
    private UserDao userDao;
    @Resource
    private ProductDao productDao;
    @Resource
    private RegionDao regionDao;
    @Resource
    private ProductVersionDao productVersionDao;
    
    @Test
    public void testgetExpiredTrials() throws Exception {
        final String eam = "EAM";
        final String environmentId = "1";

        createTrial(eam, "EAM-BC-3",environmentId);

        //TODO -- new service security...
        final List<TrialEnvironmentDto> trials = edgeService.getExpiredTrialsUnsecured(eam);
        assertNotNull(trials);
        assertEquals(1, trials.size());

    }

    @Test
    public void testgetAllExpiredTrials() throws Exception {
        final String eam = "EAM";
        final String environmentId = "1";

        createTrial(eam, "EAM-BC-3",environmentId);
        createTrial("XM", "XM-BC-3",environmentId);
        createTrial("Syteline","Syteline-BC-3", environmentId);

        final List<TrialEnvironmentDto> trials = edgeService.getExpiredTrialsUnsecured();
        assertNotNull(trials);
        assertEquals(3, trials.size());

    }

    private TrialInstance createTrial(String productName,String productVersionName, String evironmentId) {
        TrialInstance trialInstance = new TrialInstance();
        trialInstance.setGuid(UUID.randomUUID().toString());
        trialInstance.setEnvironmentId(evironmentId);
        Calendar cal = Calendar.getInstance();
        trialInstance.setCreatedAt(cal.getTime());
        cal.add(Calendar.DATE, -1);
        trialInstance.setExpirationDate(cal.getTime());
        trialInstance.setUsername("r1");
        trialInstance.setPassword("r1");
        trialInstance.setUser(userDao.findByUsername(testUserName));
        trialInstance.setUrl("http://testurl/" + productName);
        trialInstance.setProductVersion(productVersionDao.findByProductAndName(productDao.findByShortName(productName),productVersionName));
        trialInstance.setRegion(regionDao.findByShortName("SA"));
        trialInstanceDao.save(trialInstance);
        return trialInstance;
    }

    @Test
    public void testGetProducts() throws Exception {
        final List<ProductDto> products = edgeService.getProductsUnsecured();
        assertNotNull(products);
        assertEquals(productDao.count(), products.size());
    }
}
