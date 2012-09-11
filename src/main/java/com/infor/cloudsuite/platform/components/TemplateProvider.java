package com.infor.cloudsuite.platform.components;

import java.io.IOException;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * User: bcrow
 * Date: 10/28/11 3:03 PM
 */
@Component
public class TemplateProvider {
    private static final Logger logger = LoggerFactory.getLogger(TemplateProvider.class);

    @Autowired
    private Configuration freemarkerConfiguration;

    public String processTemplate(String templateName, Object model) {
        Locale locale = freemarkerConfiguration.getLocale();
        return processTemplate(templateName, locale, model);
    }

    public String processTemplate(String templateName, Locale locale ,Object model) {
        try {
            final Template template = freemarkerConfiguration.getTemplate(templateName, locale, "ISO-8859-1");
            return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        } catch (IOException | TemplateException e) {
            logger.error("Error creating the email body.", e);
        }
        return null;
    }
}
