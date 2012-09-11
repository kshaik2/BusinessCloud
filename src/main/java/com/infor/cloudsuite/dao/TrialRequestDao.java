package com.infor.cloudsuite.dao;

import java.util.Date;
import java.util.List;

import com.infor.cloudsuite.entity.TrialRequest;
import com.infor.cloudsuite.platform.jpa.ExtJpaRepository;

/**
 * User: bcrow
 * Date: 3/19/12 12:14 PM
 */
public interface TrialRequestDao extends ExtJpaRepository<TrialRequest, Long> {
    
    TrialRequest findByRequestKey(String requestKey);
    
    List<TrialRequest> findByCreatedAtLessThan(Date d);
}
