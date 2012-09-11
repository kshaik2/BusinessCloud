package com.infor.cloudsuite.service;

import static org.junit.Assert.assertEquals;

import com.infor.cloudsuite.AbstractTest;
import com.infor.cloudsuite.dao.IndustryDao;
import com.infor.cloudsuite.entity.Industry;

import org.junit.Test;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Transactional(propagation = Propagation.REQUIRED)
public class SeedDataTest extends AbstractTest {

	@Resource
	IndustryDao industryDao;
	
	@Test
	public void checkImportedIndustries() throws Exception {
		
		
		assertEquals("Count in dao should equal count in file",5,industryDao.count());
	
		Industry industry=new Industry();
		industry.setName("Construction");
		industry.setDescription("Bob the builder");
		industryDao.save(industry);
		industryDao.flush();
		
		assertEquals("Count in dao should be one higher",6,industryDao.count());
		
	}
}
