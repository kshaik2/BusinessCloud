package com.infor.cloudsuite.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.infor.cloudsuite.entity.DomainBlacklist;

/**
 * User: bcrow
 * Date: 11/9/11 3:01 PM
 */
public interface DomainBlacklistDao extends JpaRepository<DomainBlacklist, Integer> {

    /**
     * Find a DomainBlacklist item by its domain
     * @param domain the domain search criteria
     * @return The item if it exists.
     */
    DomainBlacklist findByDomain(String domain);

}
