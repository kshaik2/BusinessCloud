package com.infor.cloudsuite.dto;

import com.infor.cloudsuite.entity.User;

public class UserSummaryDto {

    private Long id;
    private String username;
    private String firstName;
    private String lastName;
	
	public UserSummaryDto() {
		
	}
	
	public UserSummaryDto(Long id, String username, String firstName, String lastName) {
		this.id=id;
		this.username=username;
		this.firstName=firstName;
		this.lastName=lastName;
	}
	
	public UserSummaryDto(User user) {
		this.id=user.getId();
		this.username=user.getUsername();
		this.firstName=user.getFirstName();
		this.lastName=user.getLastName();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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
	
	
	
}
