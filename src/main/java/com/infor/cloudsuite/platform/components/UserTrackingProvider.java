package com.infor.cloudsuite.platform.components;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.infor.cloudsuite.dao.UserTrackingDao;
import com.infor.cloudsuite.entity.TrackingType;
import com.infor.cloudsuite.entity.TrialInstance;
import com.infor.cloudsuite.entity.User;
import com.infor.cloudsuite.entity.UserTracking;

/**
 * User: bcrow
 * Date: 1/9/12 10:19 AM
 */
@Component
@Transactional
public class UserTrackingProvider {
    
    @Resource
    private UserTrackingDao userTrackingDao;
    
    public void trackUserLogin(User user) {
        userTrackingDao.save(new UserTracking(user, TrackingType.LOGIN));
    }
    
    public void trackFaildLogin(User user) {
        userTrackingDao.save(new UserTracking(user, TrackingType.FAILED_LOGIN));
    }
    
    public void trackProxyUrlHit(User user, TrialInstance trialInstance, String other) {
        userTrackingDao.save(new UserTracking(user, TrackingType.PROXY_URL_HIT, trialInstance.getId(), other));
    }
}
