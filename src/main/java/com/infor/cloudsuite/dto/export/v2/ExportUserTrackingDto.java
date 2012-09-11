package com.infor.cloudsuite.dto.export.v2;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.amazonaws.util.json.JSONObject;
import com.infor.cloudsuite.dto.export.AbstractExportDto;
import com.infor.cloudsuite.entity.UserTracking;

public class ExportUserTrackingDto extends AbstractExportDto {
	private static final SimpleDateFormat SDF=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzz");
	private String username;
	private String otherData;
	private Long targetObject;
	private String trackingType;
	private Date timestamp;
	private String timestampString;

	
	public ExportUserTrackingDto() {
		
	}
	
	public ExportUserTrackingDto(UserTracking userTracking) {
		this.username=userTracking.getUser().getUsername();
		this.otherData=userTracking.getOtherData();
		this.targetObject=userTracking.getTargetObject();
		this.trackingType=userTracking.getTrackingType().name();
		this.timestamp=userTracking.getTimestamp();
		if (timestamp != null) {
			this.timestampString=SDF.format(timestamp);
		}
		
	}

	public ExportUserTrackingDto(JSONObject jsonObject) {
		this.username=jsonObject.optString("username");
		this.otherData=jsonObject.optString("otherData");
		this.targetObject=jsonObject.optLong("targetObject");
		this.trackingType=jsonObject.optString("trackingType");
		this.timestamp=getDate(jsonObject,"timestampString","timestamp");
		this.timestampString=jsonObject.optString("timestampString");
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
	
	
}
