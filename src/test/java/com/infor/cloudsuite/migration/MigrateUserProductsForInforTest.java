package com.infor.cloudsuite.migration;

import static org.junit.Assert.*;

import java.util.ArrayList;

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import com.infor.cloudsuite.AbstractTest;
import com.infor.cloudsuite.dao.*;
import com.infor.cloudsuite.entity.*;

public class MigrateUserProductsForInforTest extends AbstractTest {
	
	@Resource
	MigrateUserProductsForInfor migrator;
	
	@Resource
	UserDao userDao;
	
	@Resource
	UserProductDao userProductDao;
	
	@Resource
	ProductDao productDao;
	
	@Transactional
	@Test
	public void testUserProductAddsForInfor() throws Exception {
		
		ArrayList<User> users=new ArrayList<User>();
		for (int i=0; i<10 ; i++) {
			User user=super.createUser("user"+i+"@infor.com", "U"+i, "ser", "user", Role.ROLE_EXTERNAL);
			userDao.save(user);
			user=userDao.findByUsername(user.getUsername());
			assertEquals("User '"+user.getUsername()+"' should have none in count",0,userProductDao.findByUserId(user.getId()).size());
			users.add(user);
		}
	
		migrator.migrate();
		long productSize=productDao.count();
		for (User user : users) {
		
			assertEquals("User '"+user.getUsername()+"' should have products.size() in count {post migrate}",productSize,userProductDao.findByUserId(user.getId()).size());
		}
		
		
		
	}

}
