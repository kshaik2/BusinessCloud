package com.infor.cloudsuite.task;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import com.infor.cloudsuite.AbstractTest;
import com.infor.cloudsuite.dao.ValidationDao;
import com.infor.cloudsuite.entity.Validation;

@Transactional
public class ValidationCleanerWorkerTest extends AbstractTest{

@Resource
ValidationDao validationDao;
@Resource
ValidationCleanerWorker validationCleanerWorker;

@Test
public void testProcessMethod() throws Exception {
	
	
	
	long startCount=validationDao.count();
	
	Validation validation=new Validation();
	validation.setCompany("Random company");
	validation.setCreateDate(subtractDaysFromToday(15));
	validation.setEmail("user@randomcompany.com");
	validation.setFirstName("Random");
	validation.setLastName("Dude");
	validation.setValidationKey("LALALA");
	
	validationDao.save(validation);
	validationDao.flush();
	
	assertEquals((startCount+1),validationDao.count());
	validationCleanerWorker.work();

	
	assertEquals((startCount),validationDao.count());
	
	
	
}


private Date subtractDaysFromToday(int count) {
	Date date=new Date();
	Calendar calendar=new GregorianCalendar();
	calendar.setTime(date);
	calendar.add(Calendar.DATE, (-1)*count);
	
	return calendar.getTime();
}
}
