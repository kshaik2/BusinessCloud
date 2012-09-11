package com.infor.cloudsuite.dto;

import java.util.Date;

/**
 * User: bcrow
 * Date: 11/28/11 1:32 PM
 */
public class LoginAgg {
    private Long userId;
    private Long loginCnt;
    private Date lastLogin;

    public LoginAgg(Long userId, Long loginCnt, Date lastLogin) {
        this.userId = userId;
        this.loginCnt = loginCnt;
        this.lastLogin = lastLogin;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getLoginCnt() {
        return loginCnt;
    }

    public void setLoginCnt(Long loginCnt) {
        this.loginCnt = loginCnt;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }
}
