package com.infor.cloudsuite.dto;

/**
 * User: bcrow
 * Date: 12/28/11 11:33 AM
 */
public class UserIdDto {
    private Long userId;
    private String userName;

    public UserIdDto() {
    }

    public UserIdDto(Long userId) {
        this.userId = userId;
    }

    public UserIdDto(String userName) {
        this.userName = userName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
