package com.infor.cloudsuite.dto;

import com.infor.cloudsuite.entity.Role;

/**
 * User: bcrow
 * Date: 12/12/11 9:58 AM
 */
public class UserRoleUpdateDto {
    private Long userId;
    private Role newRole;

    public UserRoleUpdateDto() {
    }

    public UserRoleUpdateDto(Long userId, Role newRole) {
        this.userId = userId;
        this.newRole = newRole;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Role getNewRole() {
        return newRole;
    }

    public void setNewRole(Role newRole) {
        this.newRole = newRole;
    }
}
