package com.infor.cloudsuite.task;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.Future;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.infor.cloudsuite.dao.TrialRequestDao;
import com.infor.cloudsuite.entity.TrialRequest;
import com.infor.cloudsuite.platform.components.SettingsProvider;
import com.infor.cloudsuite.service.component.TrialEmailComponent;

@Component
public class StaleRequestWorker {

	private static final Logger logger = LoggerFactory.getLogger(StaleRequestWorker.class);

	@Resource
	private SettingsProvider settingsProvider;

	@Resource
	private TrialRequestDao trialRequestDao;
	
	@Resource
	private TrialEmailComponent trialEmailComponent;
	
	@Async
	@Transactional
	public Future<String> workAsync() {
		logger.debug("Execute the work asyncronously.");
		try {
			work();
		} catch (Exception e) {
			return new AsyncResult<>("failure");
		}
		return new AsyncResult<>("success");

	}

	@Transactional
	public void work() {
		List<TrialRequest> requests;
		
		Date date=new Date(); //today
		Calendar calendar=new GregorianCalendar();
		calendar.setTime(date);
		int daysPastCreation= (-1) * settingsProvider.getDaysAfterTrialRequestStaleNotification();
		calendar.add(Calendar.DATE, daysPastCreation);
		date=calendar.getTime(); //today-x days
		logger.debug("Running the stale requests query.");
		requests = trialRequestDao.findByCreatedAtLessThan(date);
		for(TrialRequest staleRequest: requests ) {
			trialEmailComponent.sendTrialRequestStaleEmail(staleRequest);
		}
	}
}
