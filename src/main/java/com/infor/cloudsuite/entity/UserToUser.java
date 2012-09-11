package com.infor.cloudsuite.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.infor.cloudsuite.service.StringDefs;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "userId",
"targetUserId","relationType" }) })
public class UserToUser {

    private Long id;
    private Long userId;
    private Long targetUserId;
    private UserUserRelationType relationType;
	
	public UserToUser() {
		
	}
	
	public UserToUser(Long userId, Long targetUserId, UserUserRelationType relationType) {
		this.userId=userId;
		this.targetUserId=targetUserId;
		this.relationType=relationType;
	}
	
	
	public UserToUser(User user, User targetUser, UserUserRelationType relationType) {
		this(user.getId(),targetUser.getId(),relationType);
	}
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getTargetUserId() {
		return targetUserId;
	}
	public void setTargetUserId(Long targetUserId) {
		this.targetUserId = targetUserId;
	}
	public UserUserRelationType getRelationType() {
		return relationType;
	}
	public void setRelationType(UserUserRelationType relationType) {
		this.relationType = relationType;
	}

	@GenericGenerator(name = "sequenceGenerator", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "UserToUser_SEQ"),
			@Parameter(name = "initial_value", value = StringDefs.SEQ_INITIAL_VALUE),
			@Parameter(name = "optimizer", value = StringDefs.SEQ_OPTIMIZER),
			@Parameter(name = "increment_size", value = StringDefs.SEQ_INCREMENT) })
	@Id
	@GeneratedValue(generator = "sequenceGenerator")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	
	
	
	
}
