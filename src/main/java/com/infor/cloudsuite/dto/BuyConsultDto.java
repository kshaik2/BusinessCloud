package com.infor.cloudsuite.dto;

import org.hibernate.validator.constraints.Email;

/**
 * User: bcrow
 * Date: 10/28/11 9:11 AM
 */
public class BuyConsultDto {
    private Long productId;
    private String email;
    private String phone;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    @Email()
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
