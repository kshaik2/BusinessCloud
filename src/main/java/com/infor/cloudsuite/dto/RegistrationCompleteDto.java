package com.infor.cloudsuite.dto;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.hibernate.validator.constraints.NotEmpty;

import com.infor.cloudsuite.entity.User;

/**
 * User: bcrow
 * Date: 10/20/11 1:50 PM
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegistrationCompleteDto implements PasswordCompleter {
    
    private Long validationId;
    private String password;
    private String password2;
    private String companyName;
    private Long companyId;
    private Long industryId;
    private String inforId;
    private String language;
    private String address1;
    private String address2;
    private String phone;
    private String country;

    public RegistrationCompleteDto() {
    	
    }
    
    public RegistrationCompleteDto(User user) {
    	if (user.getCompany() != null) {
    		this.companyName=user.getCompany().getName();
    		this.companyId=user.getCompany().getId();
    		this.industryId=user.getCompany().getIndustry().getId();
    		this.inforId=user.getCompany().getInforId();
    	}
    	
    	this.language=user.getLanguage();
    	this.address1=user.getAddress1();
    	this.address2=user.getAddress2();
    	this.phone=user.getPhone();
    	this.country=user.getCountry();
    }
    
    public Long getValidationId() {
        return validationId;
    }

    public void setValidationId(Long validationId) {
        this.validationId = validationId;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    @NotEmpty
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @NotEmpty
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

    @NotEmpty
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @NotEmpty
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @NotEmpty
    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public String getInforId() {
		return inforId;
	}

	public void setInforId(String inforId) {
		this.inforId = inforId;
	}

	public Long getIndustryId() {
		return industryId;
	}

	public void setIndustryId(Long industryId) {
		this.industryId = industryId;
	}
    

}
