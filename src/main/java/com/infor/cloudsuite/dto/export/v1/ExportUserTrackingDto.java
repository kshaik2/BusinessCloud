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

public class ExportUserTrackingDto {
	private static final SimpleDateFormat SDF=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzz");
	private static final Logger logger=LoggerFactory.getLogger(ExportUserTrackingDto.class);
	private String username;
	private String otherData;
	private Long targetObject;
	private String trackingType;
	private Date timestamp;
	private String timestampString;

	public ExportUserTrackingDto() {
		
	}
	
	public ExportUserTrackingDto(ResultSet resultSet) throws SQLException {
		this.username=resultSet.getString(1);
		this.otherData=resultSet.getString(2);
		this.targetObject=resultSet.getLong(3);
		this.trackingType=resultSet.getString(4);
		
		this.timestampString=resultSet.getString(5);
		try {
			this.timestamp=SDF.parse(timestampString);
		} catch (ParseException e) {
			logger.error("error converting '"+timestampString+"' (from DB) to time value",e);
		}

	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getOtherData() {
		return otherData;
	}

	public void setOtherData(String otherData) {
		this.otherData = otherData;
	}

	public Long getTargetObject() {
		return targetObject;
	}

	public void setTargetObject(Long targetObject) {
		this.targetObject = targetObject;
	}

	public String getTrackingType() {
		return trackingType;
	}

	public void setTrackingType(String trackingType) {
		this.trackingType = trackingType;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getTimestampString() {
		return timestampString;
	}

	public void setTimestampString(String timestampString) {
		this.timestampString = timestampString;
	}
	

	public static List<ExportUserTrackingDto> listFromSQLQuery(ResultSet resultSet) throws SQLException{
	
		ArrayList<ExportUserTrackingDto> exportedUserTrackings=new ArrayList<>();
	
		if (!resultSet.wasNull() && resultSet.first()) {
			do {
				exportedUserTrackings.add(new ExportUserTrackingDto(resultSet));
			} while (resultSet.next());
		}
	
		return exportedUserTrackings;
	}
	
}
