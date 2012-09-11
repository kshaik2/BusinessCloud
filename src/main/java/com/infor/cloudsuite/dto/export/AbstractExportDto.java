package com.infor.cloudsuite.dto.export;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;

public class AbstractExportDto {
	private static final SimpleDateFormat SDF=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzz");
	protected static final String LS=System.getProperty("line.separator");
	
	protected Date getDate(JSONObject jsonObject, String dateStringKey, String dateLongKey) {
		Date timestamp;
		try {
			timestamp=new Date(jsonObject.getLong(dateLongKey));
		} catch (JSONException e) {
			try {
				timestamp=SDF.parse(jsonObject.optString(dateStringKey));

			} catch (ParseException ez) {
				timestamp=new Date();
			}
		}
		
		return timestamp;
	}
	
	protected String getDateString(Date date) {
		if (date == null) {
			return null;
		}
		
		return SDF.format(date);
	}
}
