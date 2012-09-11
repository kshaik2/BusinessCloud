package com.infor.cloudsuite.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.infor.cloudsuite.dto.LoginAgg;
import com.infor.cloudsuite.entity.TrackingType;
import com.infor.cloudsuite.entity.User;
import com.infor.cloudsuite.entity.UserTracking;
import com.infor.cloudsuite.platform.jpa.ExtJpaRepository;

/**
 * User: bcrow
 * Date: 1/9/12 10:19 AM
 */
public interface UserTrackingDao extends ExtJpaRepository<UserTracking, Long> {
    
    @Query("SELECT new com.infor.cloudsuite.dto.LoginAgg(data.user.id, count(data.id), max(data.timestamp)) " +
            "FROM UserTracking data " +
            "where data.trackingType = 'LOGIN' AND data.user.id = ?1 " +
            "GROUP BY data.user.id")
    public LoginAgg getLoginAgg(Long userId);

    public List<UserTracking> findByTrackingTypeAndUser(TrackingType type, User user);
    public List<UserTracking> findByUser(User user);
    
    @Modifying
    @Query("DELETE from UserTracking data WHERE data.user = ?1")
    void deleteDataByUser(User user);
}
