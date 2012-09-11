package com.infor.cloudsuite.task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.infor.cloudsuite.AbstractTest;
import com.infor.cloudsuite.dao.ProductDao;
import com.infor.cloudsuite.dao.ProductVersionDao;
import com.infor.cloudsuite.dao.RegionDao;
import com.infor.cloudsuite.dao.TrialRequestDao;
import com.infor.cloudsuite.dao.UserDao;
import com.infor.cloudsuite.entity.ProductVersion;
import com.infor.cloudsuite.entity.Region;
import com.infor.cloudsuite.entity.TrialRequest;
import com.infor.cloudsuite.entity.User;
import com.infor.cloudsuite.platform.components.NullEmailProvider;
import com.infor.cloudsuite.platform.components.NullEmailProvider.EmailInfo;
import com.infor.cloudsuite.platform.security.SecurityService;
import com.infor.cloudsuite.service.component.DeploymentServiceComponent;
import com.infor.cloudsuite.service.component.TrialEmailComponent;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

@Transactional(propagation = Propagation.REQUIRED)
public class StaleRequestWorkerTest extends AbstractTest {

	@Resource
	private TrialRequestDao trialRequestDao;
	@Resource
	private StaleRequestWorker staleRequestWorker;
	@Resource
	private TrialEmailComponent trialEmailComponent;
	@Resource
	private ProductDao productDao;
	@Resource
	private ProductVersionDao productVersionDao;
	@Resource
	private RegionDao regionDao;
	@Resource
	private UserDao userDao;
	@Resource
	private SecurityService securityService;
	@Resource
	private DeploymentServiceComponent deploymentServiceComponent;

	private static final Logger logger = LoggerFactory.getLogger(StaleRequestWorkerTest.class);

	@Test
	public void testProcessMethod() throws Exception {
		List<ProductVersion> productVersions = new ArrayList<ProductVersion>();
		productVersions.add(productVersionDao.findByProductAndName(productDao.findByShortName("EAM"),"EAM-BC-3"));
		productVersions.add(productVersionDao.findByProductAndName(productDao.findByShortName("XM"),"XM-BC-3"));
		
		final NullEmailProvider emailProvider = new NullEmailProvider();
		trialEmailComponent.setEmailProvider(emailProvider);
		
		List<NullEmailProvider.EmailInfo> asyncEmails = emailProvider.getAsyncEmails();
		assertEquals("Verify that email count is 0", 0, asyncEmails.size());
		
		User user = userDao.findByUsername(testUserName);
		Region region = regionDao.findById(2L);
		String comment = "Test comment.";
		TrialRequest trialRequest = new TrialRequest();
		trialRequest.setCreatedAt(subtractDaysFromToday(5));
		trialRequest.setUser(user);
		trialRequest.setProductVersions(productVersions);
		trialRequest.setRegion(region);
		trialRequest.setLanguage(user.getLanguage());
		trialRequest.setComment(comment);

		String raw = trialRequest.getId() + user.getUsername() + this.productShortNamesStrungForRequestKey(productVersions);
		final String requestKey = securityService.encodePassword(raw, trialRequest.getCreatedAt());
		trialRequest.setRequestKey(requestKey);

		long startCount=trialRequestDao.count();
		trialRequestDao.save(trialRequest);

		TrialRequest requestByKey = trialRequestDao.findByRequestKey(requestKey);
		assertNotNull("Verify request search by key is not null", requestByKey.getId());
		List<TrialRequest> staleRequest = trialRequestDao.findByCreatedAtLessThan(new Date());
		assertEquals("Verify trials older than 5 days exists", 1, staleRequest.size());
		
		
		assertNotNull("Verify trial request id is not null",trialRequest.getId());
		assertEquals("Verify trial request count has increased after adding trial request", (startCount+1),trialRequestDao.count());
		
		staleRequestWorker.work();

		assertEquals("Verify that email was added", 1, asyncEmails.size());
		logger.debug("\n----- Email count --- \n" + asyncEmails.size() + "\n---------------");

		for(EmailInfo emailInfo: asyncEmails) {
			assertTrue("Verify email contains product names", emailInfo.text.contains("Products: " + trialEmailComponent.productNamesStrungForEmail(productVersions)));
			logger.debug("\n----- EMAIL BODY --- \n" + emailInfo.text + "\n---------------");
			assertTrue(emailInfo.text.contains("User: " + trialRequest.getUser().getUsername()));
			assertTrue(emailInfo.text.contains("Comment: " + trialRequest.getComment()));
			assertTrue(emailInfo.text.contains("Region: " + trialRequest.getRegion().getName()));
		}
		trialRequestDao.flush();
	}


	private Date subtractDaysFromToday(int count) {
		Date date=new Date();
		Calendar calendar=new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, (-1)*count);

		return calendar.getTime();
	}
	
    public String productShortNamesStrungForRequestKey(List<ProductVersion> productVersions) {
    	
    	StringBuilder builder=new StringBuilder();
    	boolean first=true;
    	for (ProductVersion productVersion : productVersions) {
    		if (!first) {
    			builder.append(":");
    		}
    		builder.append(productVersion.getProduct().getShortName()).append(":").append(productVersion.getName());
    		first=false;
    	}
    	return builder.toString();
    }

}