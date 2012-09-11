package com.infor.cloudsuite.service.component;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.infor.cloudsuite.dao.TrialRequestDao;
import com.infor.cloudsuite.entity.ProductVersion;
import com.infor.cloudsuite.entity.Region;
import com.infor.cloudsuite.entity.TrialRequest;
import com.infor.cloudsuite.entity.User;
import com.infor.cloudsuite.platform.components.RequestServices;
import com.infor.cloudsuite.platform.security.SecurityService;

/**
 * User: bcrow
 * Date: 3/19/12 12:11 PM
 */
@Component
public class TrialRequestsComponent {
	@Resource
	private TrialRequestDao trialRequestDao;
	@Resource
	private SecurityService securityService;
	@Resource
	private TrialEmailComponent trialEmailComponent;
	@Resource
	private RequestServices requestServices;

	@Transactional
	public TrialRequest createTrialRequest(HttpServletRequest request, List<ProductVersion> productVersions, User user, Region region, Locale language, String comment) {
		TrialRequest trialRequest = new TrialRequest();
		trialRequest.setCreatedAt(new Date());
		trialRequest.setUser(user);
		trialRequest.setProductVersions(productVersions);
		trialRequest.setRegion(region);
		trialRequest.setLanguage(language.toString());
		trialRequest.setComment(comment);
		trialRequestDao.save(trialRequest);
		Assert.notNull(trialRequest.getId(), "Trial Request ID was not set.");

		String raw = trialRequest.getId() + user.getUsername() + this.productShortNamesStrungForRequestKey(productVersions);
		final String requestKey = securityService.encodePassword(raw, trialRequest.getCreatedAt());
		trialRequest.setRequestKey(requestKey);

		trialEmailComponent.sendTrialRequestEmail(user, productVersions, region, requestKey, comment, request);
		trialEmailComponent.sendTrialRequestConfimationEmail(user, productVersions, language);

		return trialRequest;
	}

	public TrialRequest deleteTrialRequest(String requestKey) {
		final TrialRequest trialRequest = trialRequestDao.findByRequestKey(requestKey);
		trialRequestDao.delete(trialRequest);
		return trialRequest;
	}

	public List<TrialRequest> getTrialRequests() {
		return trialRequestDao.findAll();
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
