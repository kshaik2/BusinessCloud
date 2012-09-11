package com.infor.cloudsuite.task;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.Future;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.infor.cloudsuite.dao.ValidationDao;
import com.infor.cloudsuite.platform.components.SettingsProvider;

@Component
public class ValidationCleanerWorker {
    private static final Logger logger = LoggerFactory.getLogger(ValidationCleanerWorker.class);

    @Resource
    private ValidationDao validationDao;
    @Resource
    private SettingsProvider settingsProvider;
    

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
    	Date date=new Date(); //today
    	Calendar calendar=new GregorianCalendar();
    	calendar.setTime(date);
    	int days=(-1) * settingsProvider.getDaysUntilValidationCleaned(); //-x days
    	calendar.add(Calendar.DATE, days);
    	date=calendar.getTime(); //today-x days
    	logger.debug("Running the remove query.");
        validationDao.removeOlderThanDate(date);
    }
}
