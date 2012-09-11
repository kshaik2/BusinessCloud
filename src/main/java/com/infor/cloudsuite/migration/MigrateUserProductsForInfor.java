package com.infor.cloudsuite.migration;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.infor.cloudsuite.dao.UserDao;
import com.infor.cloudsuite.entity.User;
import com.infor.cloudsuite.service.StringDefs;
import com.infor.cloudsuite.service.component.UserServiceComponent;

@Component
public class MigrateUserProductsForInfor extends Migrator {

	@Resource
	UserDao userDao;
	@Resource
	UserServiceComponent userServiceComponent;
	
	@Override
	@Transactional
	public void migrate() {		
		for (User user : userDao.findAll()) {
	        if (StringDefs.INFOR_DOMAIN.equals(user.getUsername().split("@")[1])){
	        	userServiceComponent.activateAllUserProductsForUser(user);
	        }
			
		}	
	}	
}
