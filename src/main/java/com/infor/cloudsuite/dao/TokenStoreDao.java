package com.infor.cloudsuite.dao;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.infor.cloudsuite.entity.TokenStore;
import com.infor.cloudsuite.entity.User;
import com.infor.cloudsuite.platform.jpa.ExtJpaRepository;

/**
 * User: bcrow
 * Date: 8/6/12 10:14 AM
 */
public interface TokenStoreDao extends ExtJpaRepository<TokenStore, Long> {

    TokenStore findByUser_IdAndConsumerId(Long userId, String consumerId);

    @Modifying
    @Query(value = "DELETE FROM TokenStore ts WHERE ts.user = ?1")
    void deleteByUser(User user);
}
