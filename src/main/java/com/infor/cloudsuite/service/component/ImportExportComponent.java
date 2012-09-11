package com.infor.cloudsuite.service.component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.infor.cloudsuite.dao.*;
import com.infor.cloudsuite.dto.export.v2.*;
import com.infor.cloudsuite.dto.export.v3.ExportAmiDescriptorDto;
import com.infor.cloudsuite.dto.export.v3.ExportCompanyDto;
import com.infor.cloudsuite.dto.export.v3.ExportIndustryDto;
import com.infor.cloudsuite.dto.export.v3.ExportSeedDtoV3;
import com.infor.cloudsuite.entity.*;
import com.infor.cloudsuite.impexp.AbstractImporter;
import com.infor.cloudsuite.platform.CSWebApplicationException;
import com.infor.cloudsuite.platform.amazon.AmazonFactory;
import com.infor.cloudsuite.service.StringDefs;

@Component
public class ImportExportComponent {	
	private static final SimpleDateFormat WEBSTAMP=new SimpleDateFormat("EEE, dd MMMMM yyyy HH:mm:ss Z");
	private static final SimpleDateFormat FILESTAMP=new SimpleDateFormat("yyyy-MM-dd--HH_mm_ss");
	private static final Logger logger=LoggerFactory.getLogger(ImportExportComponent.class);
	@Resource
    private ProductDao productDao;
	@Resource
	private ProductDescriptionDao productDescriptionDao;
    @Resource
    private UserDao userDao;
    @Resource
	TrialEnvironmentDao trialEnvironmentDao;
	@Resource
	TrialInstanceDao trialInstanceDao;
	@Resource
    AmazonCredentialsDao amazonCredentialsDao;
	@Resource
	UserProductDao userProductDao;
	@Resource
	RegionDao regionDao;
    @Resource
    private UserTrackingDao userTrackingDao;
    @Resource
    private AmazonFactory amazonFactory;
    @Resource
    private TrialProductChildDao trialProductChildDao;
    @Resource
    private IndustryDao industryDao;
    @Resource
    private AmiDescriptorDao amiDescriptorDao;
    @Resource
    CompanyDao companyDao;
    @Resource
    private ProductVersionDao productVersionDao;
    @Resource
    private ObjectMapper objectMapper;
    
    
	public String importFromJsonFile(String filename, ImportFileTypeEnum fileTypeEnum, String bucket) {
		JSONObject response=new JSONObject();
		try {
			AbstractImporter importer=null;

			switch (fileTypeEnum) {
			case IN_ARCHIVE : {
				importer=AbstractImporter.getImportDto(filename, true);
				break;
			}
			case ABSOLUTE: {
				importer=AbstractImporter.getImportDto(filename,false);
				break;
			}
			case IN_S3: {
				importer=AbstractImporter.getImportDto(filename,bucket,amazonFactory.getAmazonS3());
				break;
			}
			default: break;
			}


			runAllJsonImports(importer,response);
			
		} catch (JSONException e) {
            logger.error("Encounted exception running import switch.",e);
			
			throw new CSWebApplicationException(StringDefs.GENERAL_ERROR_CODE,e);
		}
			
		return response.toString();
	}
	
	public String importFromJsonString(String jsonString) {
		JSONObject response=new JSONObject();
		try {
			AbstractImporter importer=AbstractImporter.getImportDtoFromJsonString(jsonString);
			runAllJsonImports(importer,response);
		} catch (JSONException e) {
            throw new CSWebApplicationException(StringDefs.GENERAL_ERROR_CODE,e);
		}
	
		return response.toString();
	}
	
	public String importFromJsonData(HashMap<String,Object> jsonMap) {
		
		JSONObject response=new JSONObject();
		
		try {
			AbstractImporter importer=AbstractImporter.getImportDto(jsonMap);
			runAllJsonImports(importer,response);
		} catch (JSONException e) {
            throw new CSWebApplicationException(StringDefs.GENERAL_ERROR_CODE,e);
		}
		
		return response.toString();
	}
	
	private void setDaos(AbstractImporter importer) {
    	importer.setAmazonCredentialsDao(amazonCredentialsDao);
    	importer.setProductDao(productDao);
    	importer.setRegionDao(regionDao);
    	importer.setTrialEnvironmentDao(trialEnvironmentDao);
    	importer.setTrialInstanceDao(trialInstanceDao);
    	importer.setUserDao(userDao);
    	importer.setUserProductDao(userProductDao);
    	importer.setUserTrackingDao(userTrackingDao);
    	importer.setTrialProductChildDao(trialProductChildDao);
    	importer.setProductDescriptionDao(productDescriptionDao);
    	importer.setIndustryDao(industryDao);
    	importer.setAmiDescriptorDao(amiDescriptorDao);
    	importer.setCompanyDao(companyDao);
    	importer.setProductVersionDao(productVersionDao);
	}
	
	public void runAllJsonImports(AbstractImporter importer, JSONObject response) {
		
		setDaos(importer);
    	
    	importer.runAllImports(response);
		
	}
	
	
	private String getJsonStringFromDtoObject(Object export, boolean format) {
    	String jsosString;
    	try {
    		if (format) {
                jsosString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(export);
            }
            else {
                jsosString = objectMapper.writeValueAsString(export);
            }

    	} catch (IOException e) {
            logger.error("Encountered error converting object to JSON", e);
            jsosString = "{\"error\":\""+e.getMessage()+"\"}";
    	}

        return jsosString;
	}
	
	private void setExportResponseSettings(HttpServletResponse response, String filename) {
    	if (filename != null) {
    		if ("GENERATE".equals(filename)) {
    			filename="exportV2-"+FILESTAMP.format(new Date())+".json";
    		}
    		response.addHeader("Content-Disposition", "attachment; filename="+filename+"; modification-date=\""+WEBSTAMP.format(new Date())+"\""); 		
    	    
    	}
    	response.setCharacterEncoding("UTF-8");
	}
	
	public String exportRequiredData(HttpServletResponse response,String filename, boolean overwriteConflicts, boolean format){
		setExportResponseSettings(response,filename);
    	
    	ExportDataDto export=new ExportDataDto();
    	List<User> users=userDao.findAll();
    	for (User user : users) {
    		export.getExportedUsers().add(new ExportUserDto(user));	
    	}
    	    	
    	List<TrialEnvironment> environments=trialEnvironmentDao.findAll();
    	for (TrialEnvironment environment : environments) {
    		export.getExportedTrialEnvironments().add(new ExportTrialEnvironmentDto(environment));
    	}
    	
    
    	List<TrialInstance> instances=trialInstanceDao.findAll();
    	for (TrialInstance instance : instances) {
    		export.getExportedTrialInstances().add(new ExportTrialInstanceDto(instance));
    	}
    	
    	
    	List<AmazonCredentials> credentials=amazonCredentialsDao.findAll();
    	for (AmazonCredentials cred : credentials) {
    		export.getExportedAmazonCredentials().add(new ExportAmazonCredentialsDto(cred));
    	}
    	
    	List<UserTracking> userTrackings=userTrackingDao.findAll();
    	for (UserTracking tracking : userTrackings) {
    		export.getExportedUserTrackings().add(new ExportUserTrackingDto(tracking));
    	}
    	
    	export.setOverwriteConflicts(overwriteConflicts);
    	
    	return getJsonStringFromDtoObject(export, format);
	}


	public String exportSeedData(HttpServletResponse response, String filename, boolean format) {

		setExportResponseSettings(response,filename);
		
    	ExportSeedDtoV3 export=new ExportSeedDtoV3();
    	
    	List<Region> regions=regionDao.findAll();
    	for (Region region : regions) {
    		export.getExportedRegions().add(new ExportRegionDto(region));
    		
    	}
    	
    	List<Product> products=productDao.findAll();
    	for (Product product : products) {
    		export.getExportedProducts().add(new ExportProductDto(product));
    		
    	}
    	
    	List<ProductDescription> productDescriptions=productDescriptionDao.findAll();
    	for (ProductDescription productDescription : productDescriptions) {
    		export.getExportedProductDescriptions().add(new ExportProductDescriptionDto(productDescription));	
    	}
    	
    	List<TrialProductChild> trialProductChildren=trialProductChildDao.findAll();
    	for (TrialProductChild tpc : trialProductChildren) {
    		export.getExportedTrialProductChildren().add(new ExportTrialProductChildDto(tpc));
    	}
    	
    	List<User> users=userDao.findAll();
    	for (User user : users) {
    		export.getExportedUsers().add(new ExportUserDto(user));	
    	}
    	List<AmazonCredentials> amazonCredentials=amazonCredentialsDao.findAll();
    	for (AmazonCredentials amCred : amazonCredentials) {
    		export.getExportedAmazonCredentials().add(new ExportAmazonCredentialsDto(amCred));
    	}
    	    	
    	List<TrialEnvironment> environments=trialEnvironmentDao.findAll();
    	for (TrialEnvironment environment : environments) {
    		export.getExportedTrialEnvironments().add(new ExportTrialEnvironmentDto(environment));
    	}
    	

    	
    	List<Industry> industries=industryDao.findAll();
    	for (Industry industry : industries) {
    		export.getExportedIndustryDtos().add(new ExportIndustryDto(industry));
    	}
    	
    	List<AmiDescriptor> amiDescriptors=amiDescriptorDao.findAll();
    	for (AmiDescriptor amiDescriptor : amiDescriptors) {
    		export.getExportedAmiDescriptors().add(new ExportAmiDescriptorDto(amiDescriptor));
    	}
    	
    	
    	List<Company> companies= companyDao.findAll();
    	for (Company company : companies) {
    		export.getExportedCompanies().add(new ExportCompanyDto(company));
    	}
    	
    	return getJsonStringFromDtoObject(export, format);
	}
	
	@Transactional
	public String importFromJsonFile(String filename, ImportFileTypeEnum fileTypeEnum, String bucket, String ... importKeys) {
		
		JSONObject response=new JSONObject();
		
		try {
			AbstractImporter importer=null;

			switch (fileTypeEnum) {
			case IN_ARCHIVE : {
				importer=AbstractImporter.getImportDto(filename, true);
				break;
			}
			case ABSOLUTE: {
				importer=AbstractImporter.getImportDto(filename,false);
				break;
			}
			case IN_S3: {
				importer=AbstractImporter.getImportDto(filename,bucket,amazonFactory.getAmazonS3());
				break;
			}
			default: break;
			}

			
			logger.info("Importer of class:"+importer.getClass().getName());
			logger.info("getOverwriteConflicts()?"+importer.getOverwriteConflicts());
			logger.info("Keys:"+Arrays.toString(importKeys));
			setDaos(importer);
			try {
				importer.runSelectedImports(response,importKeys);
			} catch (RuntimeException re) {
				throw new IllegalStateException(re);
			}

		} catch (IllegalStateException | JSONException e) {
            logger.error("Encounted exception running import switch.",e);
			e.printStackTrace();
			throw new CSWebApplicationException(StringDefs.GENERAL_ERROR_CODE,e);
		}
			
		logger.info(response.toString());
		return response.toString();
		
		
	}

	@Transactional
	public void markEnvironmentsAvailable(ProductVersion productVersion,
			Region region) {
		
		trialEnvironmentDao.makeEnvironmentsAvailable(productVersion, region);
		
	}

}
