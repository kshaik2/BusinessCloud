package com.infor.cloudsuite.platform.security;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.infor.cloudsuite.dao.UserDao;
import com.infor.cloudsuite.entity.User;

/**
 * User: bcrow
 * Date: 10/13/11 1:36 PM
 */
@Service
public class CSUserDetailsService implements UserDetailsService{
    private static final Logger logger = LoggerFactory.getLogger(CSUserDetailsService.class);

    @Resource
    private UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
        List<User> users = userDao.findByUsernameWithRoles(username);

        if (users.size() == 0) {
            logger.debug("Name not found!!");
            throw new UsernameNotFoundException("User not found");
        }
        if (users.size() > 1) {
            logger.error("Multiple User with the same userName.");
            throw new IncorrectResultSizeDataAccessException("Too many results.",1,users.size());
        }

        return new SecurityUser(users.get(0));
    }
}
