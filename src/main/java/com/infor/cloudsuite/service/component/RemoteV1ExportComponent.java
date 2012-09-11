package com.infor.cloudsuite.service.component;


import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.infor.cloudsuite.dto.export.v1.ExportDataDto;
import com.infor.cloudsuite.dto.export.v1.ExportTrialInstanceDto;
import com.infor.cloudsuite.dto.export.v1.ExportUserDto;
import com.infor.cloudsuite.dto.export.v1.ExportUserTrackingDto;
import com.infor.cloudsuite.dto.export.v1.OtherJdbcConnection;
import com.infor.cloudsuite.dto.export.v1.QueryEnum;
import com.infor.cloudsuite.platform.CSWebApplicationException;
import com.infor.cloudsuite.service.StringDefs;

@Component
public class RemoteV1ExportComponent {
	private final Logger logger=LoggerFactory.getLogger(RemoteV1ExportComponent.class);
	@Resource
	OtherJdbcConnection otherJdbcConnection;
    @Resource
    ObjectMapper objectMapper;
	
	
	private String getJsonStringFromDtoObject(Object export, boolean format) {
    	String jsonString;
    	try {
    		if (format) {
                jsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(export);
            } else {
                jsonString= objectMapper.writeValueAsString(export);
            }
    	} catch (IOException e) {
            logger.error("Encountered error converting object to JSON",e);
    		jsonString="{\"error\":\""+e.getMessage()+"\"}";
    	}

    	return jsonString;
	}
	
	private static final SimpleDateFormat WEBSTAMP=new SimpleDateFormat("EEE, dd MMMMM yyyy HH:mm:ss Z");
	private static final SimpleDateFormat FILESTAMP=new SimpleDateFormat("yyyy-MM-dd--HH_mm_ss");
	
	private void setExportResponseSettings(HttpServletResponse response, String filename) {
    	if (filename != null) {
    		if ("GENERATE".equals(filename)) {
    			filename="exportV1-"+FILESTAMP.format(new Date())+".json";
    		}
    		response.addHeader("Content-Disposition", "attachment; filename="+filename+"; modification-date=\""+WEBSTAMP.format(new Date())+"\""); 		
    	    
    	}
    	response.setCharacterEncoding("UTF-8");
	}
	
	public String getFormattedV1JsonString(HttpServletResponse response, String filename) {
		if (response != null && filename != null) {
			setExportResponseSettings(response,filename);
		}
		
		return getJsonStringFromDtoObject(getRemoteVersionOneExportDataDto(),true);
	}
	
	public ExportDataDto getRemoteVersionOneExportDataDto() throws CSWebApplicationException{

		ExportDataDto dto=new ExportDataDto();
		dto.setExportedUsers(getExportedUsers());
		dto.setExportedUserTrackings(getExportedUserTrackings());
		dto.setExportedTrialInstances(getExportedTrialInstances());		
		return dto;
	}
	
	public List<ExportUserDto> getExportedUsers() throws CSWebApplicationException{
	
    	List<com.infor.cloudsuite.dto.export.v1.ExportUserDto> myList;

    	try (
            ResultSet userResultSet=otherJdbcConnection.runQuery(QueryEnum.USER_QUERY.getQueryString())
        ) {
            myList=com.infor.cloudsuite.dto.export.v1.ExportUserDto.listFromSQLQuery(userResultSet);
    	} catch (SQLException sqle) {
    		logger.error("Exception encountered running User Export create for V1",sqle);
    		throw new CSWebApplicationException(StringDefs.GENERAL_ERROR_CODE,sqle);
    	}
    	
    	logger.info("List of user export size:"+myList.size());
    	for (ExportUserDto dto : myList) {
			try (
                ResultSet userRoleResults=otherJdbcConnection.runQuery(QueryEnum.USER_ROLE_QUERY.getQueryString(),dto.getUsername());
                ResultSet userProductResults=otherJdbcConnection.runQuery(QueryEnum.USER_PRODUCT_QUERY.getQueryString(),dto.getUsername())
            ) {

                dto.addRolesFromResult(userRoleResults);
        		dto.addUserProductsFromResult(userProductResults);
    		} catch (SQLException sqle) {
    			logger.error("Exception encountered adding roles and/or user products to User export list (sql code:"+sqle.getErrorCode()+")",sqle);
    			
    			throw new CSWebApplicationException(StringDefs.GENERAL_ERROR_CODE,sqle);
    			
    		}
    	}
    	
    	return myList;
	}
	
	public List<ExportUserTrackingDto> getExportedUserTrackings() throws CSWebApplicationException {
	
		List<ExportUserTrackingDto> trackings;
		
		try (
            ResultSet userTrackResult=otherJdbcConnection.runQuery(QueryEnum.USER_TRACKING_QUERY.getQueryString())
        ){

			trackings=ExportUserTrackingDto.listFromSQLQuery(userTrackResult);
			
		} catch (SQLException sqle) {
			logger.error("Error encountered exporting user trackings",sqle);
			throw new CSWebApplicationException(StringDefs.GENERAL_ERROR_CODE,sqle);
		}
		
		
		return trackings;
	}
	
	public List<ExportTrialInstanceDto> getExportedTrialInstances() throws CSWebApplicationException {
		List<ExportTrialInstanceDto> instances;

        try (
            ResultSet trialResult=otherJdbcConnection.runQuery(QueryEnum.TRIAL_INSTANCE_QUERY.getQueryString())
        ){
            instances=ExportTrialInstanceDto.listFromSQLQuery(trialResult);
		} catch (SQLException sqle) {
			logger.error("Error encountered exported Trial Instances",sqle);
			throw new CSWebApplicationException(StringDefs.GENERAL_ERROR_CODE,sqle);
		}

		return instances;
	}
}
