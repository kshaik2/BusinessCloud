package com.infor.cloudsuite.dto;

import com.infor.cloudsuite.entity.UserUserRelationType;

public class UserToUserUpdateDto {

    private Long userId;
    private Long relatedUserId;
    private UserToUserOpType opType;
    private UserUserRelationType relationType;

	public UserToUserUpdateDto() {
		
	}
	
	public UserToUserUpdateDto(Long userId, Long relatedUserId,UserToUserOpType opType, UserUserRelationType relationType) {
		this.userId=userId;
		this.relatedUserId=relatedUserId;
		this.opType=opType;
		this.relationType=relationType;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getRelatedUserId() {
		return relatedUserId;
	}

	public void setRelatedUserId(Long relatedUserId) {
		this.relatedUserId = relatedUserId;
	}

	public UserToUserOpType getOpType() {
		return opType;
	}

	public void setOpType(UserToUserOpType opType) {
		this.opType = opType;
	}

	public UserUserRelationType getRelationType() {
		return relationType;
	}

	public void setRelationType(UserUserRelationType relationType) {
		this.relationType = relationType;
	}
	
	
}
