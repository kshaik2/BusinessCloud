package com.infor.cloudsuite.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.infor.cloudsuite.entity.User;
import com.infor.cloudsuite.entity.UserProduct;
import com.infor.cloudsuite.entity.UserProductKey;
import com.infor.cloudsuite.platform.jpa.ExtJpaRepository;

/**
 * User: bcrow
 * Date: 10/25/11 3:24 PM
 */
public interface UserProductDao extends ExtJpaRepository<UserProduct, UserProductKey> {

    /**
     * Find a list of UserProducts attached to a user.
     *
     * @param userId user id for user.
     * @return List of UserProducts with the given user id.
     */
    @Query("SELECT userProduct " +
            " FROM UserProduct userProduct " +
            " WHERE userProduct.id.user.id = ?1")
    List<UserProduct> findByUserId(Long userId);
        
    @Modifying
    @Query("DELETE FROM UserProduct userProduct WHERE userProduct.id.user = ?1")
    void deleteByUser(User user);
}
