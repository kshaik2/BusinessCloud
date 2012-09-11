package com.infor.cloudsuite.dto;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.infor.cloudsuite.entity.LeadStatus;
import com.infor.cloudsuite.entity.Role;
import com.infor.cloudsuite.entity.User;

public class UserDto {

	
	private CompanyDto company;
	private Boolean active;
	private String address1;
	private String address2;
	private String country;
	private Date createdAt;
	private String firstName;
	private String lastName;
	private Boolean inforCustomer;
	private Boolean inforPartner;
	private String language;
	private String phone;
	private Set<String> roles;
	private Date updatedAt;
	private String username;
	private Long id;
	private String leadStatus;
	private Set<Long> usersOwned;
	private Set<Long> usersFollowed;
	
	
	public UserDto(User user) {
		this(user,null,null);
	}
	
	public UserDto(User user, Collection<Long> ownedUsers, Collection<Long> followedUsers) {
		this.id=user.getId();
		this.active=user.getActive();
		this.address1=user.getAddress1();
		this.address2=user.getAddress2();
		this.country=user.getCountry();
		this.createdAt=user.getCreatedAt();
		if (user.getCompany()!=null) {
			this.company=new CompanyDto(user.getCompany());
		}
		this.firstName=user.getFirstName();
		this.lastName=user.getLastName();
		this.inforCustomer=user.getInforCustomer();
		this.language=user.getLanguage();
		this.phone=user.getPhone();
		for (Role role :user.getRoles()) {
			this.getRoles().add(role.name());
		}
		this.updatedAt=user.getUpdatedAt();
		this.username=user.getUsername();

		if (followedUsers != null) {

			this.getUsersFollowed().addAll(followedUsers);
		}
		
		
		if (ownedUsers != null) {
			this.getUsersOwned().addAll(ownedUsers);
		}
		if (user.getLeadStatus()==null) {
			this.leadStatus=LeadStatus.NONE.name();
		} else {
			this.leadStatus=user.getLeadStatus().name();
		}
		
		this.inforPartner=user.getInforPartner();
	}


	public CompanyDto getCompany() {
		return company;
	}

	public void setCompany(CompanyDto company) {
		this.company = company;
	}

	public Boolean getActive() {
		return active;
	}


	public void setActive(Boolean active) {
		this.active = active;
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


	public Date getCreatedAt() {
		return createdAt;
	}


	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
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


	public Boolean getInforCustomer() {
		return inforCustomer;
	}


	public void setInforCustomer(Boolean inforCustomer) {
		this.inforCustomer = inforCustomer;
	}

	

	public Boolean getInforPartner() {
		return inforPartner;
	}


	public void setInforPartner(Boolean inforPartner) {
		this.inforPartner = inforPartner;
	}


	public String getLanguage() {
		return language;
	}


	public void setLanguage(String language) {
		this.language = language;
	}


	public String getPhone() {
		return phone;
	}


	public void setPhone(String phone) {
		this.phone = phone;
	}


	public Set<String> getRoles() {
		if (roles==null) {
			roles =new HashSet<>();
		}
		return roles;
	}


	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}


	public Date getUpdatedAt() {
		return updatedAt;
	}


	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getLeadStatus() {
		return leadStatus;
	}


	public void setLeadStatus(String leadStatus) {
		this.leadStatus = leadStatus;
	}


	public Set<Long> getUsersOwned() {
		if (usersOwned==null) {
			usersOwned=new HashSet<>();
		}
		return usersOwned;
	}


	public void setUsersOwned(Set<Long> usersOwned) {
		this.usersOwned = usersOwned;
	}


	public Set<Long> getUsersFollowed() {
		if (usersFollowed==null) {
			usersFollowed=new HashSet<>();
		}
		return usersFollowed;
	}


	public void setUsersFollowed(Set<Long> usersFollowed) {
		this.usersFollowed = usersFollowed;
	}


	
	
	
	
}
