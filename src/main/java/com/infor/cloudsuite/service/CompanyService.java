package com.infor.cloudsuite.service;

import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.infor.cloudsuite.dao.CompanyDao;
import com.infor.cloudsuite.dao.IndustryDao;
import com.infor.cloudsuite.dto.CompanyDto;
import com.infor.cloudsuite.dto.IndustryDto;
import com.infor.cloudsuite.entity.Company;
import com.infor.cloudsuite.entity.Industry;
import com.infor.cloudsuite.platform.CSWebApplicationException;

/**
 * --NOT-- created on IntelliJ
 * User: dwilliams1
 * Date: 5/23/2012 10:02:41 EDT
 */
@Path("/company")
@Service
public class CompanyService {
    private static final Logger logger = LoggerFactory.getLogger(CompanyService.class);
    
	@Resource
	private CompanyDao companyDao;			
	@Resource
	private IndustryDao industryDao;
	
	
    @GET
    @Path("/getCompaniesMatchingString")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    @Secured(StringDefs.ROLE_EXTERNAL)
    public List<CompanyDto> getCompaniesMatchingString(@QueryParam("match") String matchString) {
    	
    	return companyDao.getDtosByMatch("%"+matchString+"%");
    	
    }
    
    
    @GET
    @Path("/getCompanyById")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    @Secured(StringDefs.ROLE_EXTERNAL)
    public CompanyDto getCompanyById(@QueryParam("id") Long id)
    {
    	return companyDao.getDtoByTableId(id);
    }
    
    @GET
    @Path("/getCompanyByInforId")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    @Secured(StringDefs.ROLE_EXTERNAL)  
    public CompanyDto getCompanyByInforId(@QueryParam("inforId") String id)
    {
    	return companyDao.getDtoByInforId(id);
    }
    
  
    @GET
    @Path("/getCompanies")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    @Secured(StringDefs.ROLE_EXTERNAL)
    public List<CompanyDto> getCompanies()
    {
    	return companyDao.getAllDtos();
    }
    
    
    @GET
    @Path("/getIndustries")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List<IndustryDto> getIndustries()
    {
    	return industryDao.getAllDtos();
    }
    
    
    @POST
    @Path("/createCompany")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional(propagation = Propagation.REQUIRED)
    @Secured(StringDefs.ROLE_EXTERNAL)
    public CompanyDto createCompany(CompanyDto customerDto) throws CSWebApplicationException {
    	
    	Company company=new Company();
    	
    	Long industryId=customerDto.getIndustryId();

    	Industry industry=industryDao.findById(industryId);
    	company.setIndustry(industry);
	
    	company.setName(customerDto.getName());
    	company.setInforId(customerDto.getInforId());
    	company.setNotes(customerDto.getNotes());
    	companyDao.save(company);
    	
    	return companyDao.getDtoByTableId(company.getId());
    	
    	
    }
  
    @POST
    @Path("/updateCompany")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional(propagation = Propagation.REQUIRED)
    @Secured(StringDefs.ROLE_EXTERNAL)
    public CompanyDto updateCompany(CompanyDto customerDto) {
    	
    	Company company=companyDao.findById(customerDto.getId());
    	company.setName(customerDto.getName());
    	company.setIndustry(industryDao.findById(customerDto.getIndustryId()));
    	company.setNotes(customerDto.getNotes());
    	company.setInforId(customerDto.getInforId());
 
    	companyDao.save(company);
    	return companyDao.getDtoByTableId(customerDto.getId());
    }
    
    
    @POST
    @Path("/deleteCompanyById")
    @Transactional(propagation = Propagation.REQUIRED)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(StringDefs.ROLE_SALES)
    public List<CompanyDto> deleteCompanyById(@QueryParam("id") Long tableId) {
    	
    	companyDao.delete(tableId);
    	return companyDao.getAllDtos();
    	
    }
}
