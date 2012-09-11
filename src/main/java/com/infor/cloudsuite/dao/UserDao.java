package com.infor.cloudsuite.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.QueryHint;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import com.infor.cloudsuite.dto.BDRAdminUserDto;
import com.infor.cloudsuite.dto.UserSummaryDto;
import com.infor.cloudsuite.entity.LeadStatus;
import com.infor.cloudsuite.entity.Role;
import com.infor.cloudsuite.entity.User;
import com.infor.cloudsuite.platform.jpa.ExtJpaRepository;

/**
 * User: bcrow
 * Date: 10/12/11 2:39 PM
 */
public interface UserDao extends ExtJpaRepository<User, Long> {

    @Query("Select user From User user where user.username = ?1")
	@QueryHints(@QueryHint(name = org.hibernate.ejb.QueryHints.HINT_CACHEABLE, value = "true"))
	User findByUsername(String username);

    /**
     * Find a user by his unique username.
     * @param username username to find
     * @return The user matching username, null if none exists.
     */
    @QueryHints(@QueryHint(name = org.hibernate.ejb.QueryHints.HINT_CACHEABLE, value = "true"))
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles where u.username = ?1")
    List<User> findByUsernameWithRoles(String username);

    @Query("SELECT new com.infor.cloudsuite.dto.BDRAdminUserDto(user.id, user.username, user.phone, user.firstName, user.lastName, user.company.name,user.active, user.createdAt) FROM User user JOIN user.roles roles WHERE user.username NOT LIKE ?2 AND roles IN (?1)")
    Page<BDRAdminUserDto> findLeadsExcludeInfor(Set<Role> roles, String notLike, Pageable pageable);

    @Query("SELECT new com.infor.cloudsuite.dto.BDRAdminUserDto(user.id, user.username, user.phone, user.firstName, user.lastName, user.company.name,user.active, user.createdAt) FROM User user JOIN user.roles roles where roles in (?1)")
    Page<BDRAdminUserDto> findLeads(Set<Role> roles, Pageable pageable);

    

    List<User> findByLeadStatus(LeadStatus leadStatus);
    
    @SuppressWarnings("SpringDataJpaMethodInconsistencyInspection")
    @Query("SELECT new com.infor.cloudsuite.dto.UserSummaryDto(user.id,user.username,user.firstName,user.lastName) FROM User user where user.id=?1")
    UserSummaryDto getUserSummaryById(Long id);
    
    @Query("SELECT count(user.id) FROM User user where user.company.id=?1")
	public long countUsersForCompanyId(Long existingCompanyId);

    
    @Query("SELECT count(user.id) FROM User user where user.active=?1")
	public Long countByActive(Boolean active);

    @Query("SELECT count(user.id) FROM User user where user.id in (select distinct stack.user.id FROM DeploymentStack stack) AND user.active=?1")
	public Long countByActiveWithInfor24(Boolean active);

    @Query("SELECT count(user.id) FROM User user where user.id in (select distinct instance.user.id FROM TrialInstance instance) AND user.active=?1")
    public Long countByActiveWithAWS(Boolean active);

    @Query("SELECT count(user.id) FROM User user where user.active=?1 AND user.createdAt >= ?2")
	public Long countByActiveAndCreatedAt(Boolean active, Date then);
    
}
