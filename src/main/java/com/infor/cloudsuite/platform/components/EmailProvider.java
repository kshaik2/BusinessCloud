package com.infor.cloudsuite.platform.components;

import java.util.concurrent.Future;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import com.infor.cloudsuite.service.StringDefs;

/**
 * User: bcrow
 * Date: 10/28/11 9:27 AM
 */
@Component
public class EmailProvider {
    private static Logger logger = LoggerFactory.getLogger(EmailProvider.class);

    @Autowired
    @Qualifier("amazonMailSender")
    private JavaMailSender mailSender;

    @Async
    public Future<String> sendEmailAsync(String address, String subject, String text) {
        String result;
        result = sendEmail(address, subject, text);
        return new AsyncResult<>(result);
    }

    @Async
    public Future<String> sendEmailAsync(String address, String subject, String text, boolean html) {
        String result;
        result = sendEmail(address, subject, text, html);
        return new AsyncResult<>(result);
    }

    public String sendEmail(String address, String subject, String text) {
        return sendEmail(address, subject, text, false);
    }

    public String sendEmail(String address, String subject, String text, boolean html) {
        String result;
        MimeMessage message = mailSender.createMimeMessage();
        try {
            message.setFrom(new InternetAddress(StringDefs.BUSINESSCLOUD_EMAIL));
            message.setRecipients(Message.RecipientType.TO, address);

            message.setSubject(subject);
            if (html) {
                message.setContent(text, "text/html");
            } else {
                message.setText(text);
            }
            mailSender.send(message);
            result = StringDefs.SUCCESS;
        } catch (MessagingException e) {
            logger.error(e.getMessage());
            result = StringDefs.FAILURE;
        }
        return result;
    }
}
