package com.infor.cloudsuite.platform.components;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

/**
 * User: bcrow
 * Date: 10/31/11 2:00 PM
 */
@Component
public class MessageProvider {

    @Autowired
    @Qualifier("messageSource")
    private MessageSource messageSource;

    public String getMessage(String code, Object ... params) {
        return messageSource.getMessage(code, params, null, null);
    }

    public String getMessage(String code, Locale locale, Object ... params) {
        return messageSource.getMessage(code, params, null, locale);
    }

    public String getMessageDef(String code, String defMessage, Object ... params) {
        return messageSource.getMessage(code, params, defMessage, null);
    }

    public String getMessageDef(String code, String defMessage, Locale locale, Object ... params) {
        return messageSource.getMessage(code, params, defMessage, locale);
    }
}
