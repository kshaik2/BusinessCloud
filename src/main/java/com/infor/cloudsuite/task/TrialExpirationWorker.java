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

import com.infor.cloudsuite.dao.TrialInstanceDao;
import com.infor.cloudsuite.entity.TrialInstance;
import com.infor.cloudsuite.platform.components.SettingsProvider;
import com.infor.cloudsuite.service.component.TrialEmailComponent;

@Component
public class TrialExpirationWorker {

    private static final Logger logger = LoggerFactory.getLogger(TrialExpirationWorker.class);

    @Resource
    private SettingsProvider settingsProvider;
    @Resource
    private TrialInstanceDao trialInstanceDao;
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
        logger.info("Running Trial Expiration Notification Schedule Task");

        Date date=new Date(); //today
        Calendar calendar=new GregorianCalendar();
        calendar.setTime(date);
        int days=settingsProvider.getNotificationTrialExpirationWarning(); //x days
        calendar.add(Calendar.DATE, days);
        date=calendar.getTime(); //today + x days
        logger.debug("Running the notification query.");

        List<TrialInstance> instances = trialInstanceDao.findByExpirationDateLessThan(date);
        for(TrialInstance instance: instances) {
            logger.debug("Sending trial expiration notification for Instance: " + instance.getId());
            String userEmail;
            if(instance.getUser() != null) {
                userEmail = instance.getUser().getUsername();                
                String url = instance.getUrl();
                Date expiration = instance.getExpirationDate();

                trialEmailComponent.sendTrialExpirationNotificationEmail(userEmail, url, expiration, true);
            }
        }
    }
}
