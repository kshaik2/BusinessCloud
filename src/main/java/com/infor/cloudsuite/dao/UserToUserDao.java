package com.infor.cloudsuite.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.infor.cloudsuite.entity.UserToUser;
import com.infor.cloudsuite.entity.UserUserRelationType;
import com.infor.cloudsuite.platform.jpa.ExtJpaRepository;


public interface UserToUserDao extends ExtJpaRepository<UserToUser, Long> {
	
	@Query
	public UserToUser findByUserIdAndTargetUserIdAndRelationType(Long userId, Long targetUserId, UserUserRelationType relationType);
	
	
	@Query("select utu.targetUserId from UserToUser utu  WHERE utu.userId=?1 AND utu.relationType=?2")
	public List<Long> getTargetUserIdsWithRelationTypeToUser(Long userId, UserUserRelationType relationType);
	
	@Query("select utu.userId from UserToUser utu WHERE utu.targetUserId=?1 AND utu.relationType=?2")
	public List<Long> getUserIdsWithRelationTypeToTarget(Long targetUserId, UserUserRelationType relationType);

	@Query("select utu from UserToUser utu where utu.userId=?1 AND utu.relationType=?2")
	public List<UserToUser> getUserToUserForUserIdWithRelationType(Long userId, UserUserRelationType relationType);
	
	@Query("delete from UserToUser utu where utu.userId=?1 AND utu.relationType=?2")
	@Modifying
	public void deleteRelationshipsForUserIdWithRelationType(Long userId, UserUserRelationType relationType);
	
	@Query("delete from UserToUser utu where utu.userId=?1")
	@Modifying
	public void deleteAllRelationshipsForUserId(Long userId);
}
