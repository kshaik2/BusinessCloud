package com.infor.cloudsuite.platform.security;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;

import com.infor.cloudsuite.dao.UserDao;
import com.infor.cloudsuite.platform.components.UserTrackingProvider;

/**
 * User: bcrow
 * Date: 11/28/11 1:10 PM
 */
public class AuthenticationSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationSuccessListener.class);

    @Resource
    UserDao userDao;
    @Resource
    UserTrackingProvider userTrackingProvider;
    
    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        logger.debug("Authentication Success: " + event.getAuthentication().getName());
        final Object principal = event.getAuthentication().getPrincipal();
        if (SecurityUser.class.isAssignableFrom(principal.getClass())) {
            SecurityUser secUser = (SecurityUser) principal;
            userTrackingProvider.trackUserLogin(userDao.getReference(secUser.getId()));
        }
    }
}
