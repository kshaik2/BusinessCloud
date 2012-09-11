package com.infor.cloudsuite.task;

import java.util.Date;
import java.util.Locale;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.stereotype.Service;

@Service
public class ValidationCleanerScheduleProcessor {

	private static final Logger logger = LoggerFactory.getLogger(ValidationCleanerScheduleProcessor.class);
 
	@Resource
    private ValidationCleanerWorker worker;
	@Resource
	private TrialExpirationWorker trialWorker;
	
    private static final DateFormatter dateFormatter = new DateFormatter("yyyy/MM/dd hh:mm:ss");

//    Could not get @Scheduled annotation to work.
//    @Scheduled(cron = "0 15 00 ? * * ") // 12:15 am daily
    public void process() {
        String formatted = dateFormatter.print(new Date(), Locale.US);
        
        logger.info("[{}] Cleaning old registration information.", formatted);
    	worker.work();
    	
    	logger.info("[{}] Notifying users of expiring trials.", formatted);
    	trialWorker.work();
    }


}
