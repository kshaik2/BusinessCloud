package com.infor.cloudsuite.dto;

/**
 * User: bcrow
 * Date: 11/8/11 9:21 AM
 */
public class UserProductUpdateDto {
    private Long userId;
    private Long productId;
    private UserProductUpdateType type;
    private Boolean active;

    public UserProductUpdateDto() {
    }

    public UserProductUpdateDto(Long userId, Long productId, UserProductUpdateType type, Boolean active) {
        this.userId = userId;
        this.productId = productId;
        this.type = type;
        this.active = active;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public UserProductUpdateType getType() {
        return type;
    }

    public void setType(UserProductUpdateType type) {
        this.type = type;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
