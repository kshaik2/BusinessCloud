package com.infor.cloudsuite.dto;

/**
* User: bcrow
* Date: 11/9/11 10:12 AM
*/
public class UserFlagUpdateDto {
    private Long userId;
    private Boolean status = false;

    public UserFlagUpdateDto() {
    }

    public UserFlagUpdateDto(Long userId, Boolean status) {
        this.userId = userId;
        this.status = status;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
