package com.infor.cloudsuite.dao;

import com.infor.cloudsuite.entity.ConsultRequest;
import com.infor.cloudsuite.platform.jpa.ExtJpaRepository;

/**
 * User: bcrow
 * Date: 11/30/11 2:31 PM
 */
public interface ConsultRequestDao extends ExtJpaRepository<ConsultRequest, Long> {

    ConsultRequest findByUserId(Long id);
}
