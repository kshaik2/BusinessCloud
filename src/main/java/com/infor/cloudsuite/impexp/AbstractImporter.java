package com.infor.cloudsuite.impexp;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.infor.cloudsuite.dao.*;


public abstract class AbstractImporter {
	
	public static final SimpleDateFormat SDF=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzz");
	private boolean overwriteConflicts=false;
	
	CompanyDao companyDao;
	UserDao userDao;
	ProductDao productDao;
	TrialEnvironmentDao trialEnvironmentDao;
	TrialInstanceDao trialInstanceDao;
    AmazonCredentialsDao amazonCredentialsDao;
	UserProductDao userProductDao;
	RegionDao regionDao;
	UserTrackingDao userTrackingDao;
	TrialProductChildDao trialProductChildDao;
	ProductDescriptionDao productDescriptionDao;
	IndustryDao industryDao;
	AmiDescriptorDao amiDescriptorDao;
	ProductVersionDao productVersionDao;
	
	ImportTrackingObject tracker=new ImportTrackingObject();
	
	private static final Logger logger=LoggerFactory.getLogger(AbstractImporter.class);
    public static final String LINE = System.getProperty("line.separator");
    public static final String CR="\r";

    
    private JSONObject importJson;
    
	public AbstractImporter() {
		
	}
	

	protected abstract boolean runImport(String key, JSONObject outputValue);
	protected abstract ArrayList<String> getOpKeyList();
	
	
	//override to do anything version specific setup-wise
	public void postInitSetup(){
		//do nothing
	}
	protected void setOverwriteConflicts(boolean overwriteConflicts) {
		this.overwriteConflicts=overwriteConflicts;
		
	}
	public boolean getOverwriteConflicts() {
		return this.overwriteConflicts;
	}
	
	public boolean runAllImports(JSONObject outputValue) {

		boolean success=runSelectedImports(outputValue,getOpKeyList());		
		addToJsonAndIgnore(outputValue,"success",success);
		return success;
	}
	public boolean runSelectedImports(JSONObject outputValue, String ... selected) {
		boolean retValue=true;
		for (String key : selected) {
			retValue=retValue&&runImport(key,outputValue);
		}
	
		addToJsonAndIgnore(outputValue, "success", retValue);
		return retValue;
	}
	
	public boolean runSelectedImports(JSONObject outputValue,List<String> selected) {

		boolean retValue=true;
		for (String key : selected) {
			retValue=retValue&runImport(key,outputValue);
		}
	
		addToJsonAndIgnore(outputValue, "success", retValue);
		return retValue;
	}
	
	//setters 
	public void setJSONObject(JSONObject importJson) {
		this.importJson=importJson;
		postInitSetup();
	}

	
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public void setProductDao(ProductDao productDao) {
		this.productDao = productDao;
	}

	public void setTrialEnvironmentDao(TrialEnvironmentDao trialEnvironmentDao) {
		this.trialEnvironmentDao = trialEnvironmentDao;
	}


	public void setTrialInstanceDao(TrialInstanceDao trialInstanceDao) {
		this.trialInstanceDao = trialInstanceDao;
	}

	public void setAmazonCredentialsDao(AmazonCredentialsDao amazonCredentialsDao) {
		this.amazonCredentialsDao = amazonCredentialsDao;
	}

	public void setUserProductDao(UserProductDao userProductDao) {
		this.userProductDao = userProductDao;
	}

	public void setRegionDao(RegionDao regionDao) {
		this.regionDao = regionDao;
	}

	
	public void setTrialProductChildDao(TrialProductChildDao trialProductChildDao) {
		this.trialProductChildDao = trialProductChildDao;
	}


	public void setUserTrackingDao(UserTrackingDao userTrackingDao) {
		this.userTrackingDao = userTrackingDao;
	}

	public void setProductDescriptionDao(ProductDescriptionDao productDescriptionDao) {
		this.productDescriptionDao = productDescriptionDao;
	}
	

	public void setIndustryDao(IndustryDao industryDao) {
		this.industryDao = industryDao;
	}

	public void setAmiDescriptorDao(AmiDescriptorDao amiDescriptorDao) {
		this.amiDescriptorDao = amiDescriptorDao;
	}


	public void setCompanyDao(CompanyDao companyDao) {
		this.companyDao = companyDao;
	}
	public void setProductVersionDao(ProductVersionDao productVersionDao) {
		this.productVersionDao = productVersionDao;
	}
	//END DAO block
	
	
	//getters for children
	







	public boolean getBooleanFromImportJSON(String key, boolean defaultValue) {
		boolean toRet;
		try {
			toRet=importJson.getBoolean(key);
		} catch (JSONException e) {
            toRet=defaultValue;
		}
		return toRet;
		
	}
	
	public boolean getBooleanFromImportJSON(String key) {
		return getBooleanFromImportJSON(key,false);
		
	}
	
	public JSONArray getJSONArrayFromImportJSON(String key) {
		JSONArray jsonArray=null;
		try {
			jsonArray=importJson.getJSONArray(key);
		} catch (JSONException e) {
            logger.error("exception getting json array from primary json object with key '"+key+"'",e);
		}
		return jsonArray;
	}
	
	public JSONObject getJSONObjectFromImportJSON(String key) {
		JSONObject jsonObject=null;
		try {
			jsonObject=importJson.getJSONObject(key);
		} catch (JSONException e) {
			logger.error("exception getting json object from primary json object with key '"+key+"'",e);
		}
		
		return jsonObject;
	}

	//helper for children
	void addToJsonAndIgnore(JSONObject jsonObject, String key, boolean value) {
		try {
            jsonObject.put(key, value);
		} catch (JSONException ignored) {

		}
	}
	void addToJsonAndIgnore(JSONObject jsonObject, String key, long value) {
		try {
			jsonObject.put(key, value);
		} catch (JSONException ignored) {

		}
	}
	void addToJsonAndIgnore(JSONObject jsonObject, String key, Object value) {
		try {
			jsonObject.put(key, value);
		} catch (JSONException e) {
            //ignore
		}
	}
	
	
	//	static construction stuff
	
	//input streams
	private static InputStream getS3FileInputStream(String bucket, String filename, AmazonS3 s3Client) {
			
		return s3Client.getObject(new GetObjectRequest(bucket, filename)).getObjectContent();
	}
	
	private static InputStream getArchiveInputStream(String filename) {
		
		return AbstractImporter.class.getClassLoader().getResourceAsStream(filename);		
	}
	
	private static InputStream getAbsoluteInputStream(String filename) throws IOException {
		
		return new FileInputStream(filename);
	}
	private static String getJsonStringFromInputStream(InputStream inputStream) throws JSONException {
		StringBuilder builder=new StringBuilder();
		
		
		try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"utf-8"))
        ) {
			String str;
			while ((str = reader.readLine()) != null) {
				if (str.endsWith(CR)) {
					str=str.substring(0,str.length()-1);
				}
				builder.append(str).append(LINE);
			}



		} catch (IOException e) {
            throw new JSONException(e);
        }

		return builder.toString();
		}

	
	
	
	private static String getJsonStringFromFile(String filename, boolean inArchive) throws JSONException {
		
		try {
			if (!inArchive) {
				return getJsonStringFromInputStream(getAbsoluteInputStream(filename));
			}
		
			return getJsonStringFromInputStream(getArchiveInputStream(filename));
		} catch (IOException e) {
            logger.error("Encountered error reading " + filename + (" from absolute path " + "!"),e);
            throw new JSONException(e);
        } catch (JSONException e) {
            logger.error("Encountered error reading "+filename+(inArchive?" from archive ":" from absolute path "+"!"),e);
            throw e;
        }
	}

	
	private static AbstractImporter getImportDto(JSONObject jsonObject) throws JSONException {
		String version=jsonObject.getString("version");
		ImpExpVersion versionEnum;
		try {
			versionEnum=ImpExpVersion.valueOf(version);			
		} catch (IllegalArgumentException e) {
            throw new JSONException(e);
		}
		
		AbstractImporter importer;
		try {
	
		importer=versionEnum.getImporterClass().newInstance();

		} catch (IllegalAccessException | InstantiationException e) {
            logger.error("Error creating concrete instance of AbstractImportDto...",e);
			throw new JSONException(e);
		}

		importer.setJSONObject(jsonObject);
		return importer;
	}
	
	
	
	public static AbstractImporter getImportDto(HashMap<String,Object> jsonMap) throws JSONException{
		
		JSONObject jsonObject=new JSONObject(jsonMap);
		return getImportDto(jsonObject);
		
		
		
	}
	
	public static AbstractImporter getImportDtoFromJsonString(String jsonString) throws JSONException {
		JSONObject jsonObject=new JSONObject(jsonString);
		return getImportDto(jsonObject);
	}
	
	
	public static AbstractImporter getImportDto(String filename, boolean inArchive) throws JSONException{
		
		logger.info("Importing json data from "+(inArchive?"archive":"")+" file:"+filename);
		String jsonString=getJsonStringFromFile(filename,inArchive);
		JSONObject jsonObject=new JSONObject(jsonString);
		return getImportDto(jsonObject);
	}
	
	public static AbstractImporter getImportDto(String filename) throws JSONException{
		return getImportDto(filename,true);
	}

	public static AbstractImporter getImportDto(String filename, String bucket,AmazonS3 amazonS3) throws JSONException {
	
		logger.info("Importing json data from file '"+filename+"', bucket '"+bucket+"'");
		String jsonString=getJsonStringFromInputStream(getS3FileInputStream(bucket,filename,amazonS3));
		JSONObject jsonObject=new JSONObject(jsonString);
		return getImportDto(jsonObject);
	}
	
}

