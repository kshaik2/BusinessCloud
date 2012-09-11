package com.infor.cloudsuite.task;

import java.util.Date;
import java.util.Locale;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.stereotype.Service;

@Service
public class StaleRequestScheduleProcessor {

	private static final Logger logger = LoggerFactory.getLogger(StaleRequestScheduleProcessor.class);
	
	@Resource
    private StaleRequestWorker worker;
    private static final DateFormatter dateFormatter = new DateFormatter("yyyy/MM/dd hh:mm:ss");

    public void process() {
        String formatted = dateFormatter.print(new Date(), Locale.US);
        logger.info("[{}] Notify user trial Request is waiting.", formatted);
    	worker.work();	
    }
    
}
