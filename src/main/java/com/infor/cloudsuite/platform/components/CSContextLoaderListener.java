package com.infor.cloudsuite.platform.components;

import java.util.Properties;

import javax.servlet.ServletContextEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoaderListener;

import com.infor.cloudsuite.service.ScheduleService;
import com.infor.cloudsuite.service.SeedService;

/**
 * User: bcrow
 * Date: 11/3/11 3:47 PM
 */
public class CSContextLoaderListener extends ContextLoaderListener {
    private static final Logger logger = LoggerFactory.getLogger(CSContextLoaderListener.class);

    @Override
    public void contextInitialized(ServletContextEvent event) {
        super.contextInitialized(event);
        final SeedService seedService = getCurrentWebApplicationContext().getBean(SeedService.class);
        final ScheduleService scheduleService=getCurrentWebApplicationContext().getBean(ScheduleService.class);
        final Properties properties = System.getProperties();
        String version = properties.getProperty("java.version");
        String vendor = properties.getProperty("java.vendor");
        String vmName = properties.getProperty("java.vm.name");

        logger.info("Java vendor: {}", vendor);
        logger.info("Java version: {}", version);
        logger.info("Java vm name: {}", vmName);

        logger.debug("Context initialized.");
        logger.debug("Seeding database if it is empty.");
        seedService.seedDatabase();
        logger.debug("Loading scheduled tasks data");
        scheduleService.loadSchedules();
    }
}
