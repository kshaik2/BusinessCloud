package com.infor.cloudsuite.task;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import com.infor.cloudsuite.AbstractTest;
import com.infor.cloudsuite.Mocked;
import com.infor.cloudsuite.dao.ProductDao;
import com.infor.cloudsuite.dao.ProductVersionDao;
import com.infor.cloudsuite.dao.RegionDao;
import com.infor.cloudsuite.dao.TrialInstanceDao;
import com.infor.cloudsuite.dao.UserDao;
import com.infor.cloudsuite.dto.TrialDto;
import com.infor.cloudsuite.entity.ProductVersion;
import com.infor.cloudsuite.entity.Region;
import com.infor.cloudsuite.entity.User;
import com.infor.cloudsuite.platform.components.NullEmailProvider;
import com.infor.cloudsuite.platform.components.SettingsProvider;
import com.infor.cloudsuite.service.TrialService;
import com.infor.cloudsuite.service.component.TrialEmailComponent;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@Mocked
@Transactional
public class TrailExpirationWarningTest extends AbstractTest {

    @Resource
    private TrialService trailService;
    @Resource
    private UserDao userDao;
    @Resource
    private ProductDao productDao;
    @Resource
    private ProductVersionDao productVersionDao;
    
    @Resource
    private RegionDao regionDao;
    @Resource
    private TrialEmailComponent trialEmailComponent;
    @Resource
    private TrialInstanceDao trialInstanceDao;

    @Mock
    private SettingsProvider settingsProvider;

    @Test
    public void testWork() {
        login("sales@infor.com","useruser");
        User sales=userDao.findByUsername("sales@infor.com");

        ProductVersion productVersion = productVersionDao.findByProductAndName(productDao.findByShortName("EAM"), "EAM-BC-3");
        Region region = regionDao.findById(2L);

        TrialDto trial = trailService.launchTrial(getRequestStub(), productVersion, sales, region, Locale.US, new Date());

        NullEmailProvider emailProvider = new NullEmailProvider();
        TrialExpirationWorker trialWorker = new TrialExpirationWorker();

        //Since trial is created for 30 days we add 5 more days so we trigger the email notification.
        when( settingsProvider.getNotificationTrialExpirationWarning() ).thenReturn(35);

        ReflectionTestUtils.setField(trialEmailComponent, "emailProvider", emailProvider);
        ReflectionTestUtils.setField(trialWorker, "trialEmailComponent", trialEmailComponent);
        ReflectionTestUtils.setField(trialWorker, "trialInstanceDao", trialInstanceDao);
        ReflectionTestUtils.setField(trialWorker, "settingsProvider", settingsProvider);

        trialWorker.work();

        List<NullEmailProvider.EmailInfo> asyncEmails = emailProvider.getAsyncEmails();
        assertEquals("Verify email count", 1, asyncEmails.size());
        
        for(NullEmailProvider.EmailInfo emailInfo: asyncEmails) {
            assertEquals("Verify email", sales.getUsername(), emailInfo.address);
            assertTrue("Verify body", emailInfo.text.contains(trial.getUrl()));
        }
    }
}