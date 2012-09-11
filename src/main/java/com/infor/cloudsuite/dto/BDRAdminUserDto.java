package com.infor.cloudsuite.dto;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.infor.cloudsuite.entity.User;

/**
 * User: bcrow
 * Date: 11/8/11 10:51 AM
 */
public class BDRAdminUserDto {
    private static final Logger logger = LoggerFactory.getLogger(BDRAdminUserDto.class);

    private Long userId;
    private String email;
    private String phone;
    private String firstName;
    private String lastName;
    private String companyName;
    private Boolean active;
    private Date createdAt;
    

    public BDRAdminUserDto(Long userId, String email, String phone,
			String firstName, String lastName, String companyName, Boolean active,
			Date createdAt) {

		this.userId = userId;
		this.email = email;
		this.phone = phone;
		this.firstName = firstName;
		this.lastName = lastName;
		this.companyName = companyName;
		this.active = active;
		this.createdAt = createdAt;
	}

	public BDRAdminUserDto(User user) {
        this.userId = user.getId();
        this.email=user.getUsername();
        this.phone=user.getPhone();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        if (user.getCompany() != null) {
        	this.companyName=user.getCompany().getName();
        }
        this.active = user.getActive();
        this.createdAt=user.getCreatedAt();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

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

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
