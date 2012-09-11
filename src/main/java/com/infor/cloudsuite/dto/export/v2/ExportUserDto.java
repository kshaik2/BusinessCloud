package com.infor.cloudsuite.dto.export.v2;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONObject;
import com.infor.cloudsuite.dto.export.AbstractExportDto;
import com.infor.cloudsuite.entity.Role;
import com.infor.cloudsuite.entity.User;
import com.infor.cloudsuite.entity.UserProduct;

public class ExportUserDto extends AbstractExportDto{

	private static final SimpleDateFormat SDF=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzz");
	

	private Date createdAt;
	private String createdAtString;
	
	private String password;
		
	private String address1;
	private String address2;
	private String phone;
	private Boolean active;
	private Set<String> roles;
	private String companyName;
	private String username;
	private String country;
	private String firstName;
	private String lastName;
	private String language;
	private List<ExportUserProductDto> userProductDtos;
	private Boolean inforCustomer;
	private Date updatedAt;
	private String updatedAtString;

	public ExportUserDto() {
	
		
	}
	
	public ExportUserDto(User user) {

		this.createdAt=user.getCreatedAt();
		this.createdAtString=this.getDateString(createdAt);
		this.updatedAt=user.getUpdatedAt();
		this.updatedAtString=this.getDateString(updatedAt);
		
		this.password=user.getPassword();
		this.address1=user.getAddress1();
		this.address2=user.getAddress2();
		
		this.phone=user.getPhone();
		this.active=user.getActive();
		
		if (user.getCompany() != null) {
			this.companyName=user.getCompany().getName();
		}
		
		for (Role role : user.getRoles()) {
			this.getRoles().add(role.toString());
		}
		
		this.username=user.getUsername();
		this.country=user.getCountry();
		this.firstName=user.getFirstName();
		this.lastName=user.getLastName();
		this.language=user.getLanguage();
		
		for (UserProduct uProduct : user.getUserProducts().values()) {
			this.getUserProductDtos().add(new ExportUserProductDto(uProduct));
		}
		
		this.inforCustomer=user.getInforCustomer();

	}

	
	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	

	public String getCreatedAtString() {
		return createdAtString;
	}

	public void setCreatedAtString(String createdAtString) {
		this.createdAtString = createdAtString;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {

		this.address1=address1;

	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {

		this.address2=address2;

	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Set<String> getRoles() {
		if (roles==null) {
			roles = new HashSet<>();
		}
		return roles;
	}

	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
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

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public List<ExportUserProductDto> getUserProductDtos() {
		if (userProductDtos == null) {
			userProductDtos=new ArrayList<>();
		}
		return userProductDtos;
	}

	public void setUserProductDtos(List<ExportUserProductDto> userProductDtos) {
		this.userProductDtos = userProductDtos;
	}
	
	public Boolean getInforCustomer() {

		return inforCustomer;
	}

	public void setInforCustomer(Boolean inforCustomer) {
		this.inforCustomer = inforCustomer;
	}
	
	

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getUpdatedAtString() {
		return updatedAtString;
	}

	public void setUpdatedAtString(String updatedAtString) {
		this.updatedAtString = updatedAtString;
	}

	public ExportUserDto(JSONObject jsonObject) {
		this.active=jsonObject.optBoolean("active");
		this.address1=jsonObject.optString("address1");
		this.address2=jsonObject.optString("address2");
		this.inforCustomer=jsonObject.optBoolean("inforCustomer");
		this.createdAtString=jsonObject.optString("createdAtString");
		this.createdAt=getDate(jsonObject,"createdAtString","createdAt");
		this.updatedAtString=jsonObject.optString("updatedAtString");
		this.updatedAt=getDate(jsonObject,"updatedAtString","updatedAt");
		
		this.companyName=jsonObject.optString("companyName");
		this.firstName=jsonObject.optString("firstName");
		this.country=jsonObject.optString("country");
		if ("null".equals(this.country)) {
			this.country=null;
		}
		this.username=jsonObject.optString("username");
		this.language=jsonObject.optString("language");
		if ("null".equals(this.language)) {
			this.language=null;
		}
		this.lastName=jsonObject.optString("lastName");
		this.password=jsonObject.optString("password");
		this.phone=jsonObject.optString("phone");
		JSONArray rls=jsonObject.optJSONArray("roles");
		if (rls != null) {
			for (int i=0; i<rls.length();i++) {
				this.getRoles().add(rls.optString(i));
			}
		}
		
		JSONArray uprods=jsonObject.optJSONArray("userProductDtos");
		if (uprods != null) {
			for (int i=0; i<uprods.length();i++) {
				JSONObject uprodObj=uprods.optJSONObject(i);
				if (uprodObj != null) {
					this.getUserProductDtos().add(new ExportUserProductDto(uprodObj));
				}
			}
		}
		
	}
}
