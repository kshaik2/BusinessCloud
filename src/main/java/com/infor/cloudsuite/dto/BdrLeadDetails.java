package com.infor.cloudsuite.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: bcrow
 * Date: 12/29/11 9:52 AM
 */
public class BdrLeadDetails {
    private String companyName;
    private String phone;
    private String userName;
    private String address1;
    private String address2;
    private String country;
    private Boolean inforCustomer;
    private Date createdAt;
    private List<BdrTrialInfoDto> trials;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Boolean getInforCustomer() {
        if(inforCustomer == null) {
            inforCustomer = Boolean.FALSE;
        }
        return inforCustomer;
    }

    public void setInforCustomer(Boolean inforCustomer) {
        this.inforCustomer = inforCustomer;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public List<BdrTrialInfoDto> getTrials() {
        if (trials == null) {
            trials = new ArrayList<>();
        }
        return trials;
    }

    public void setTrials(List<BdrTrialInfoDto> trials) {
        this.trials = trials;
    }
}
