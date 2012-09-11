package com.infor.cloudsuite.service;

import static org.junit.Assert.*;

import javax.annotation.Resource;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.infor.cloudsuite.AbstractTest;
import com.infor.cloudsuite.dao.*;
import com.infor.cloudsuite.dto.*;
import com.infor.cloudsuite.entity.Company;
import com.infor.cloudsuite.entity.Industry;

import java.util.*;
import org.junit.Test;

@Transactional(propagation = Propagation.REQUIRED)
public class CompanyServiceTest extends AbstractTest {

	@Resource
	CompanyDao companyDao;
	
	@Resource
	IndustryDao industryDao;
	
	@Resource
	CompanyService companyService;
	

	private long addcompanies() {
		

		IndustryDto foodServiceIndustry=companyService.getIndustries().get(0);
		assertNotNull(foodServiceIndustry);
		
		List<CompanyDto> companies=new ArrayList<CompanyDto>();
		
		CompanyDto one=new CompanyDto();
		one.setName("Waffle House");
		one.setNotes("..like my waffle house hash browns");
		one.setInforId("FAKE_company_ID_1");
		one.setIndustryId(foodServiceIndustry.getId());
		companies.add(one);
		
		CompanyDto two=new CompanyDto();
		two.setName("Pizzeria Unos");
		two.setNotes("..try their pesto pizza!");
		two.setInforId("FAKE_company_ID_2");
		two.setIndustryId(foodServiceIndustry.getId());
		companies.add(two);

		for (CompanyDto dto : companies) {
			companyService.createCompany(dto);
		}
		return companies.size();
		
	}
	
	@Test
	@Transactional(propagation=Propagation.REQUIRED)
	public void companyTest() throws Exception {
		
		assertEquals("Should be 5 industries",5,industryDao.count());
		List<Industry> industries = industryDao.findAll();
		for(Industry ind: industries){
			assertNotNull("Id not null", ind.getId());
		}
		
		loginAdminUser();
		
		//{C}RUD
		long start=companyDao.count();
		long added=addcompanies();
		
		assertEquals("Total should be start+added",(start+added),companyDao.count());
		
		//C{R}UD
		List<CompanyDto> allcompanies=companyService.getCompanies();
		assertEquals("Total should equal dao count",companyDao.count(),allcompanies.size());
		
		List<CompanyDto> matchingUno=companyService.getCompaniesMatchingString("unos");
		assertEquals("Should be one matching unos",1,matchingUno.size());
		
		

		//CR{U}D
		CompanyDto toUpdate=allcompanies.get(0); 
		String nametest="The "+toUpdate.getName();
		
		toUpdate.setName(nametest);
		
		CompanyDto returned=companyService.updateCompany(toUpdate);
		assertNotNull(returned);
		assertEquals("ids should equal",toUpdate.getId(),returned.getId());
		assertEquals("Should equal '"+nametest+"'",nametest,returned.getName());
		
		//CRU{D}
		long beforeDelete=companyDao.count();
		Company toDelete=companyDao.findByName("Waffle House");
		
		companyService.deleteCompanyById(toDelete.getId());
		assertEquals("Should be one less",(beforeDelete-1),companyDao.count());
		
		
	}
	
	@Test
	public void testImports() throws Exception {
		assertNotNull(companyDao.findByName("Infor Global"));
		assertNotNull(companyDao.findByName("Test Company"));
		
	}

}
