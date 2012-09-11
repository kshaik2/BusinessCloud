package com.infor.cloudsuite.dto.export.v1;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExportTrialInstanceDto {
	public static final SimpleDateFormat SDF=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzz");
	private static final Logger logger=LoggerFactory.getLogger(ExportTrialInstanceDto.class);
	private String productShortName;
	private String domain;
	private String trialUsername;
	private String guid;
	private String environmentId;
	private Date expirationDate;
	private String expirationDateString;
	private String trialPassword;
	private String username;
	private String type;
	private String url;
	
	
	public ExportTrialInstanceDto() {
		
	}
	
	public ExportTrialInstanceDto(ResultSet resultSet) throws SQLException {
		
		this.productShortName=resultSet.getString(1);
		this.domain=resultSet.getString(2);
		this.trialUsername=resultSet.getString(3);
		this.guid=resultSet.getString(4);
		this.environmentId=resultSet.getString(5);

		this.expirationDateString=resultSet.getString(6);
		try {
			this.expirationDate=SDF.parse(expirationDateString);
		} catch (ParseException e) {
			logger.error("error converting '"+expirationDateString+"' (from DB) to time value",e);
		}
		this.trialPassword=resultSet.getString(7);
		this.username=resultSet.getString(8);
		this.type=resultSet.getString(9);
		this.url=resultSet.getString(10);
		
		
	}

	public String getProductShortName() {
		return productShortName;
	}

	public void setProductShortName(String productShortName) {
		this.productShortName = productShortName;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getTrialUsername() {
		return trialUsername;
	}

	public void setTrialUsername(String trialUsername) {
		this.trialUsername = trialUsername;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getEnvironmentId() {
		return environmentId;
	}

	public void setEnvironmentId(String environmentId) {
		this.environmentId = environmentId;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getExpirationDateString() {
		return expirationDateString;
	}

	public void setExpirationDateString(String expirationDateString) {
		this.expirationDateString = expirationDateString;
	}

	public String getTrialPassword() {
		return trialPassword;
	}

	public void setTrialPassword(String trialPassword) {
		this.trialPassword = trialPassword;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public static List<ExportTrialInstanceDto> listFromSQLQuery(ResultSet resultSet) throws SQLException{
		
		ArrayList<ExportTrialInstanceDto> exportedTrialInstances=new ArrayList<>();
	
		if (!resultSet.wasNull() && resultSet.first()) {
			do {
				exportedTrialInstances.add(new ExportTrialInstanceDto(resultSet));
			} while (resultSet.next());
		}
	
		return exportedTrialInstances;
	}
	
}
