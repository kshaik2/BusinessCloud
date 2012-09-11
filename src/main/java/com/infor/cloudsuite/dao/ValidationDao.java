package com.infor.cloudsuite.dao;

import java.util.Date;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.infor.cloudsuite.entity.Validation;
import com.infor.cloudsuite.entity.ValidationType;
import com.infor.cloudsuite.platform.jpa.ExtJpaRepository;

/**
 * User: bcrow
 * Date: 10/17/11 1:54 PM
 */
public interface ValidationDao extends ExtJpaRepository<Validation, Long> {

    /**
     * Find a Validation by a given username and validation type. There can only be a
     * single match in the system at a time.
     * @param email the username
     * @param type the validation type
     * @return validation associated or null.
     */
    Validation findByEmailAndType(String email, ValidationType type);

    @Modifying
    @Query("DELETE FROM Validation val WHERE val.createDate < ?1")
    void removeOlderThanDate(Date d);
}
