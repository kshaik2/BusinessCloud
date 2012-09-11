package com.infor.cloudsuite.dto.export.v1;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ExportUserDto {


	private static final SimpleDateFormat SDF=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzz");
	private final static Logger logger=LoggerFactory.getLogger(ExportUserDto.class);
	
	private String awsKey;
	private String awsSecretKey;
	private Date createdAt;
	private String createdAtString;	
	private String password;	
	private String address1;
	private String address2;
	private String phone;
	private Boolean active;
	private String awsAccountNumber;
	private Set<String> roles;
	private String companyName;
	private String username;
	private String country;
	private String firstName;
	private String lastName;
	private String language;
	private List<ExportUserProductDto> userProductDtos;
	private Boolean inforCustomer;
	
	public ExportUserDto() {
			
	}
	
	public ExportUserDto(ResultSet resultSet) throws SQLException{
		this.inforCustomer=resultSet.getBoolean(1);
		this.awsKey=resultSet.getString(2);
		this.awsSecretKey=resultSet.getString(3);
		this.createdAtString=resultSet.getString(4);
		try {
			this.createdAt=SDF.parse(createdAtString);
		} catch (ParseException e) {
			logger.error("Error parsing date string '"+createdAtString+" from DB",e);
		}
		/*
		this.createdAt=resultSet.getTimestamp(4);
		
		if (createdAt != null) {
			this.createdAtString=SDF.format(this.createdAt);
		}
		*/
		this.password=resultSet.getString(5);
		this.address1=resultSet.getString(6);
		this.address2=resultSet.getString(7);
		this.phone=resultSet.getString(8);
		this.active=resultSet.getBoolean(9);
		this.awsAccountNumber=resultSet.getString(10);
		this.companyName=resultSet.getString(11);	
		//roles separate?
		this.username=resultSet.getString(12);
		if ("d.williams@infor.com".equals(username)) {
			logger.info("'"+username+"' time='"+this.createdAtString+"'\n");
		}
		this.country=resultSet.getString(13);
		this.firstName=resultSet.getString(14);
		this.lastName=resultSet.getString(15);
		this.language=resultSet.getString(16);
		
		//user products separate
	}

	public String getAwsKey() {
		return awsKey;
	}

	public void setAwsKey(String awsKey) {
		this.awsKey = awsKey;
	}

	public String getAwsSecretKey() {
		return awsSecretKey;
	}

	public void setAwsSecretKey(String awsSecretKey) {
		this.awsSecretKey = awsSecretKey;
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

	public String getAwsAccountNumber() {
		return awsAccountNumber;
	}

	public void setAwsAccountNumber(String awsAccountNumber) {
		this.awsAccountNumber = awsAccountNumber;
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
	

	public ExportUserDto addRolesFromResult(ResultSet roleResult) throws SQLException{
		if (!roleResult.wasNull() && roleResult.first()) {

			 do {
				 getRoles().add(roleResult.getString(2));
			 } while (roleResult.next());
		}
		
		return this;
	}
	
	public ExportUserDto addUserProductsFromResult(ResultSet upResult) throws SQLException {
		if (!upResult.wasNull() && upResult.first()) {

			do {
				getUserProductDtos().add(new ExportUserProductDto(upResult.getString(2),upResult.getBoolean(3),upResult.getBoolean(4)));
			} while (upResult.next());
			
		}
		
		return this;
	}
	
	public static List<ExportUserDto> listFromSQLQuery(ResultSet resultSet) throws SQLException{
	

		ArrayList<ExportUserDto> exportUserDtos=new ArrayList<>();
		
		if (!resultSet.wasNull() && resultSet.first()) {

			do {
				exportUserDtos.add(new ExportUserDto(resultSet));
			} while (resultSet.next()); 
		}

	
	
		
		return exportUserDtos;
		
	}
 	
}
