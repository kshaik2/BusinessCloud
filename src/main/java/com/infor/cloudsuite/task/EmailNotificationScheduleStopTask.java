package com.infor.cloudsuite.task;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.annotation.Resource;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.infor.cloudsuite.dao.AmazonCredentialsDao;
import com.infor.cloudsuite.dao.UserDao;
import com.infor.cloudsuite.dto.TargetData;
import com.infor.cloudsuite.entity.AmazonCredentials;
import com.infor.cloudsuite.entity.Schedule;
import com.infor.cloudsuite.entity.User;
import com.infor.cloudsuite.platform.CSWebApplicationException;
import com.infor.cloudsuite.platform.components.EmailProvider;
import com.infor.cloudsuite.platform.components.MessageProvider;
import com.infor.cloudsuite.platform.components.TemplateProvider;
import com.infor.cloudsuite.platform.json.CSObjectMapper;
import com.infor.cloudsuite.service.StringDefs;

@Component(value="emailNotificationScheduleStopTask")
@Scope("prototype")
public class EmailNotificationScheduleStopTask extends AbstractScheduleTask {

    @Resource
    private EmailProvider emailProvider;
    @Resource
    private TemplateProvider templateProvider;
    @Resource
    private MessageProvider messageProvider;
    @Resource
    private CSObjectMapper objectMapper;
    @Resource
    private AmazonCredentialsDao amznCredDao;
    @Resource
    private UserDao userDao;

    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationScheduleStopTask.class);

    public EmailNotificationScheduleStopTask(Long scheduleId) {
        super(scheduleId);
    }

    @Override
    public boolean runTask(Schedule schedule) {
        logger.info(">>EmailNotificationScheduleTask called for entityId:"+schedule.getEntityId());

        boolean rtn;
        String name = "";
        String link;
        Date scheduledAt = new Date();
        Long userId = null;
        HashMap<String, Object> templateMap = new HashMap<>(2);

        TargetData target = null;
        try {
            target = objectMapper.readValue(schedule.getTargetObject(), TargetData.class);
        } catch (JsonParseException | JsonMappingException e) {
            logger.error("JSON error", e);
        } catch (IOException e) {
            logger.error("IO error", e);
        }

        if(target != null) {
            //Get instance name tag from AWS
            if(target.getDeploymentName() != null) {
                name = target.getDeploymentName();
            }
            else {
                AmazonCredentials creds = amznCredDao.findById(target.getAmznCredId());
                if(creds == null) {
                    throw new CSWebApplicationException(new Exception("Credentials not valid for targetData"));
                }
                BasicAWSCredentials baseCreds = new BasicAWSCredentials(creds.getAwsKey(), creds.getSecretKey());
                final AmazonEC2 amazonEC2 = awsOperations.getAmazonEC2(baseCreds, target.getRegionId());
                name = awsOperations.getNameTag(amazonEC2, target.getEntityId());
                if(name == null || name.isEmpty()) {
                   name = target.getEntityId(); 
                }
            }
            
            scheduledAt = target.getScheduledAtDate();
            link = target.getUrl();
            userId = target.getUserId();
            
            int endIndex = name.length();
            if(endIndex > 40) {
                endIndex = 39;
            }
            templateMap.put("deploymentName", name.substring(0, endIndex));
            templateMap.put("detailsLink", link);
            templateMap.put("timestamp", scheduledAt);
        }

        final String toAddress;

        if(userId != null) {
            User user = userDao.findById(userId);
            toAddress = user.getUsername();
        }
        else {
            toAddress = StringDefs.BC_ADMIN_EMAIL;
        }

        long currentTime = Calendar.getInstance().getTimeInMillis();
        long scheduledStopTime = scheduledAt.getTime();
        long minutesLeft = ( (scheduledStopTime - currentTime) / 1000 ) / 60;

        if (minutesLeft < 0) {
            logger.info("minutesLeft (before STOP) was:"+minutesLeft+" , set to 0");
            minutesLeft=0L;
        }
        final String subject = messageProvider.getMessage(StringDefs.MESSAGE_EMAIL_STOP_WARNING_SUBJECT, name, minutesLeft);
        final String emailBody = templateProvider.processTemplate(StringDefs.MESSAGE_EMAIL_STOP_WARNING_TEMPLATE, templateMap);

        String status = emailProvider.sendEmail(toAddress, subject, emailBody, true);
        rtn = true;

        logger.info(">>Email Notification Sent for entityId:"+schedule.getEntityId() + " to email: " + toAddress + " Status: " + status);

        return rtn;
    }

}
