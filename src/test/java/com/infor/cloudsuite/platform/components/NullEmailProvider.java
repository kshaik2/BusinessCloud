package com.infor.cloudsuite.platform.components;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.springframework.scheduling.annotation.AsyncResult;

import com.infor.cloudsuite.service.StringDefs;

/**
 * User: bcrow
 * Date: 11/30/11 3:05 PM
 */
public class NullEmailProvider extends EmailProvider {
    
    private String returnString = StringDefs.SUCCESS;
    private final List<EmailInfo> asyncEmails = new ArrayList<EmailInfo>();
    private final List<EmailInfo> emails = new ArrayList<EmailInfo>();

    @Override
    public Future<String> sendEmailAsync(String address, String subject, String text) {
        asyncEmails.add(new EmailInfo(address, subject, text));
        return new AsyncResult<String>(returnString);
    }

    @Override
    public Future<String> sendEmailAsync(String address, String subject, String text, boolean html) {
        return sendEmailAsync(address, subject, text);
    }

    @Override
    public String sendEmail(String address, String subject, String text, boolean html) {
        return sendEmail(address, subject, text);
    }

    @Override
    public String sendEmail(String address, String subject, String text) {
        emails.add(new EmailInfo(address, subject, text));
        return returnString;
    }

    public String getReturnString() {
        return returnString;
    }

    public void setReturnString(String returnString) {
        this.returnString = returnString;
    }

    public List<EmailInfo> getAsyncEmails() {
        return asyncEmails;
    }
    
    public boolean asyncEmailsContainSubject(String subject) {
    	if (asyncEmails==null || subject==null) {
    		return false;
    	}
    	for (EmailInfo emailInfo : asyncEmails) {
    		if (subject != null && subject.equals(emailInfo.subject)) {
    			return true;
    		}
    	}
    	
    	
    	return false;
    }

    public List<EmailInfo> getEmails() {
        return emails;
    }

    public class EmailInfo {
        public String address;
        public String subject;
        public String text;

        EmailInfo(String address, String subject, String text) {
            this.address = address;
            this.subject = subject;
            this.text = text;
        }
    }
}
