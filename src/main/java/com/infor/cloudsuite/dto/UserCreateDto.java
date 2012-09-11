package com.infor.cloudsuite.dto;

public class UserCreateDto {

	private Long id=-1L;
	private String firstName;
	private String lastName;
	private String email;
	private String language;
	private String address1;
	private String address2;
	private String country;
	private String companyName;
	private Long companyId;
	private Boolean inforCustomer;
	private String inforId;
	private String phone;
	private Long industryId;
	private String role;
	
	public UserCreateDto() {
		
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


	public String getLanguage() {
		return language;
	}


	public void setLanguage(String language) {
		this.language = language;
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
		return inforCustomer;
	}


	public void setInforCustomer(Boolean inforCustomer) {
		this.inforCustomer = inforCustomer;
	}
	




	public void setInforId(String inforId) {
		this.inforId = inforId;
	}



	public String getPhone() {
		return phone;
	}



	public void setPhone(String phone) {
		this.phone = phone;
	}



	public String getRole() {
		return role;
	}



	public void setRole(String role) {
		this.role = role;
	}



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



	public Long getIndustryId() {
		return industryId;
	}



	public void setIndustryId(Long industryId) {
		this.industryId = industryId;
	}



	public Long getId() {
		return id;
	}



	public void setId(Long id) {
		this.id = id;
	}

	
}

